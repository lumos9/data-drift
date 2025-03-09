#!/bin/bash

SCRIPT_DIR="$(realpath "$(dirname "$0")")"
source "${SCRIPT_DIR}/logger.sh"

# Function to display the countdown
countdown() {
    local seconds=$1
    while [ "$seconds" -gt 0 ]; do
        if [[ "$OSTYPE" == "darwin"* ]]; then
            # macOS
            echo -ne "$(date -u -r "$seconds" +%H:%M:%S)s\r"
        else
            # Linux/Unix
            echo -ne "$(date -u -d @"$seconds" +%H:%M:%S)s\r"
        fi
        sleep 1
        ((seconds--))
    done
    echo -e "\n"
}

# Variables
KAFKA_CONTAINER_NAME="broker"
TOPIC_NAME="ecommerce-transactions"
BROKER_HOST="localhost"
BROKER_PORT="9092"
NUM_PARTITIONS=1
REPLICATION_FACTOR=1

# Check if Kafka broker is up and running on localhost port 9092
docker_ps_output=$(docker ps --filter "publish=9092" --format "{{.Names}}")

if [ -z "$docker_ps_output" ]
then
    info "Kafka broker is not running on localhost:9092 in any Docker container. Starting..."
    COMPOSE_FILE="kafka-docker-compose.yml"
    # Start the services
    run_command docker compose -f $COMPOSE_FILE up -d --remove-orphans
    # Check the status of the services
    compose_output=$(docker compose -f $COMPOSE_FILE ps)
    compose_status=$?
    if [ $compose_status -eq 0 ]; then
        info "All services are successfully initiated."
    else
        error "services failed to start."
        error "$compose_output"
        exit 1
    fi
    info "Waiting for a minute to load Kafka services..."
    countdown 60
fi

# Function to check if a Kafka topic exists
topic_exists() {
    TOPIC_NAME=$1
    docker exec $KAFKA_CONTAINER_NAME kafka-topics --list --bootstrap-server $BROKER_HOST:$BROKER_PORT | grep -w "$TOPIC_NAME" > /dev/null 2>&1
    return $?
}

# Function to delete a Kafka topic if it exists
delete_topic_if_exists() {
    TOPIC_NAME=$1
    info "Checking if Kafka Topic '$TOPIC_NAME' already exists..."
    if topic_exists "$TOPIC_NAME"; then
        info "Kafka Topic '$TOPIC_NAME' already exists. Deleting it..."
        docker exec $KAFKA_CONTAINER_NAME kafka-topics --delete --topic "$TOPIC_NAME" --bootstrap-server $BROKER_HOST:$BROKER_PORT
        if [ $? -eq 0 ]; then
            info "Kafka Topic '$TOPIC_NAME' deleted successfully."
        else
            error_exit "Failed to delete topic '$TOPIC_NAME'."
        fi
    else
        info "Kafka Topic '$TOPIC_NAME' does not exist. No action taken."
    fi
}

docker_ps_output=$(docker ps --filter "publish=9092" --format "{{.Names}}")
info "Checking the status of '$docker_ps_output' container.."

kafka_container=$(docker exec -it "$docker_ps_output" bash -c 'nc -zv localhost 9092 2>&1')
if [[ "$kafka_container" == *"succeeded"* || "$kafka_container" == *"Connected to"* ]]
then
    info "Kafka broker is up and running on localhost:9092 in container: $docker_ps_output"
    delete_topic_if_exists $TOPIC_NAME
else
    warn "Kafka broker is not accessible on localhost:9092 in the Docker container.."
    info "Waiting for another minute to load Kafka services..."
    countdown 60
    kafka_container=$(docker exec -it "$docker_ps_output" bash -c 'nc -zv localhost 9092 2>&1')
    if [[ "$kafka_container" == *"succeeded"* || "$kafka_container" == *"Connected to"* ]]
    then
        info "Kafka broker is up and running on localhost:9092 in container: $docker_ps_output"
        delete_topic_if_exists $TOPIC_NAME
    else
        error "Something went wrong launching Confluence kafka via docker container"
        error "${kafka_container}"
        exit 1
    fi
fi

info "Setting up new kafka topic '$TOPIC_NAME'.."

# Create the Kafka topic
run_command docker exec -it \
  $KAFKA_CONTAINER_NAME \
  kafka-topics \
  --create \
  --topic "$TOPIC_NAME" \
  --bootstrap-server $BROKER_HOST:$BROKER_PORT \
  --partitions $NUM_PARTITIONS \
  --replication-factor $REPLICATION_FACTOR

info "Kafka topic '$TOPIC_NAME' created successfully"