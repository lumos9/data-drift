#!/bin/bash

# Enable debugging output (remove or comment out for production)
set -x  # Echo commands before executing

# Get the absolute directory path of this script (setup_pg.sh)
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

# Debug: Show key path information
#echo "Script invoked as: $0" >&2
#echo "Current working directory: $(pwd)" >&2
#echo "Resolved script directory: $SCRIPT_DIR" >&2

# Construct the path to logger.sh in /full/path/setup
# Assuming /full/path/setup is the root of your project
SETUP_DIR="$(dirname "$(dirname "$(dirname "$SCRIPT_DIR")")")"
LOGGER_PATH="$SETUP_DIR/logger.sh"

# Fallback: Check if PROJECT_ROOT is valid
if [ -z "$SETUP_DIR" ] || [ ! -d "$SETUP_DIR" ]; then
    echo "Error: Failed to determine setup dir. SETUP_DIR='$SETUP_DIR'" >&2
    exit 1
fi

# Check if logger.sh exists in the project root
if [ -f "$LOGGER_PATH" ]; then
    echo "Found logger.sh at: $LOGGER_PATH" >&2
    # Source logger.sh
    source $LOGGER_PATH
else
    echo "Error: logger.sh not found at $LOGGER_PATH" >&2
    echo "Directory contents of $SETUP_DIR:" >&2
    #ls -l "$SETUP_DIR" >&2
    exit 1
fi

# Disable debugging output (remove or comment out for production)
set +x

# Function to print error message and exit
function error_exit {
    error "$1" 1>&2
    exit 1
}

# Check if Docker is installed and print version
if command -v docker &> /dev/null
then
    docker_version=$(docker --version)
    info "Docker is installed: $docker_version"
else
    error_exit "Docker is not installed. Please install Docker and try again."
fi

# Check if Java is installed and ensure it's version 17 or above
if command -v java &> /dev/null
then
    java_version=$(java -version 2>&1 | awk -F[\"_] 'NR==1 {print $2}')
    # shellcheck disable=SC2071
    if [[ "$java_version" < "21" ]]
    then
        error_exit "Java version is $java_version. Please install Java 21 or above."
    else
        info "Java is installed: version $java_version"
    fi
else
    error_exit "Java is not installed. Please install Java 17 or above and try again."
fi

# Check if Python is installed and print version, ensure it's version 3 or above
if command -v python3 &> /dev/null
then
    python_version=$(python3 --version 2>&1 | awk '{print $2}')
    # shellcheck disable=SC2071
    if [[ "$python_version" < "3" ]]
    then
        error_exit "Python version is $python_version. Please install Python 3 or above."
    else
        info "Python is installed: version $python_version"
    fi
else
    error_exit "Python is not installed. Please install Python 3 or above and try again."
fi

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

# Function to check if all services are running
check_services() {
    #echo "$status" | grep -q "Exit"
    # shellcheck disable=SC2181
    if [ $? -eq 0 ]; then
        info "All services are successfully initiated."
    else
        error "services failed to start."
        #echo "$status"
        exit 1
    fi
}


info "Setting up local postgres db.."
CONTAINER_NAME=local_postgres

# Check if container is running
run_command_ignore_exit_status "docker ps -q -f name=\"^${CONTAINER_NAME}$\""
if docker ps -q -f name="^${CONTAINER_NAME}$" | grep -q .; then
    info "Stopping container: $CONTAINER_NAME"
    run_command_ignore_exit_status "docker stop \"$CONTAINER_NAME\""

    info "Removing container: $CONTAINER_NAME"
    run_command_ignore_exit_status "docker rm \"$CONTAINER_NAME\""
else
    info "Container '$CONTAINER_NAME' is not running."
fi

PG_SETUP_PATH="${SETUP_DIR}/db/jdbc/postgres"
#mkdir -p "${PG_SETUP_PATH}/local_db"
COMPOSE_FILE="${PG_SETUP_PATH}/postgres-docker-compose.yml"
run_command docker compose -f "${COMPOSE_FILE}" up -d

run_command docker cp "${PG_SETUP_PATH}/wait-for-postgres-startup.sh" local_postgres:/wait-for-postgres-startup.sh

# Execute the readiness check from the host machine
run_command docker exec -it local_postgres /wait-for-postgres-startup.sh

run_command docker cp "${PG_SETUP_PATH}/create_table.sql" local_postgres:/create_table.sql

# Check if postgres container is up and running on localhost port 5432
docker_ps_output=$(docker ps --filter "publish=5432" --format "{{.Names}}")
#postgres_container=$(docker exec -it "$docker_ps_output" bash -c 'nc -zv localhost 5432 2>&1')
postgres_db_status=$(nc -zv localhost 5432 2>&1)
if [[ "$postgres_db_status" == *"succeeded"* || "$postgres_db_status" == *"Connected to"* ]]
then
    info "Postgres DB is up and running on localhost:5432 in container: $docker_ps_output"
    run_command docker exec -i local_postgres psql -U postgres -d postgres -f /create_table.sql
else
    error_exit "Unable to connect postgres db via container"
fi

info "setup complete!"