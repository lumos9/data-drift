#!/bin/bash

# Define container names and network
NAMENODE_CONTAINER="namenode"
DATANODE_CONTAINER="datanode"
NETWORK_NAME="hadoop-net"
HADOOP_IMAGE_NN="bde2020/hadoop-namenode:latest"
HADOOP_IMAGE_DN="bde2020/hadoop-datanode:latest"

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Check for Docker installation
if ! command_exists docker; then
    echo "❌ Docker is not installed. Please install Docker first." >&2
    exit 1
fi

# Ensure Docker is running
if (! docker stats --no-stream >/dev/null 2>&1 ); then
    echo "❌ Docker daemon is not running. Please start Docker." >&2
    exit 1
fi

echo "✅ Docker is installed and running."

# Create a Docker network if it doesn't exist
if ! docker network ls | grep -q "$NETWORK_NAME"; then
    echo "🔧 Creating Docker network: $NETWORK_NAME..."
    docker network create "$NETWORK_NAME"
else
    echo "✅ Docker network $NETWORK_NAME already exists."
fi

# Pull Hadoop Docker images
echo "📥 Pulling Hadoop images..."
docker pull "$HADOOP_IMAGE_NN" || { echo "❌ Failed to pull $HADOOP_IMAGE_NN"; exit 1; }
docker pull "$HADOOP_IMAGE_DN" || { echo "❌ Failed to pull $HADOOP_IMAGE_DN"; exit 1; }

# Start NameNode
if docker ps -a --format '{{.Names}}' | grep -q "$NAMENODE_CONTAINER"; then
    echo "♻️ Restarting existing NameNode container..."
    docker stop "$NAMENODE_CONTAINER" && docker rm "$NAMENODE_CONTAINER"
fi

echo "🚀 Starting NameNode..."
docker run -d --name "$NAMENODE_CONTAINER" \
    --net "$NETWORK_NAME" \
    -p 9870:9870 -p 9000:9000 \
    -e CLUSTER_NAME=test-cluster \
    -e CORE_CONF_fs_defaultFS=hdfs://namenode:9000 \
    "$HADOOP_IMAGE_NN"

if [ $? -ne 0 ]; then
    echo "❌ Failed to start NameNode container." >&2
    exit 1
fi

# Start DataNode
if docker ps -a --format '{{.Names}}' | grep -q "$DATANODE_CONTAINER"; then
    echo "♻️ Restarting existing DataNode container..."
    docker stop "$DATANODE_CONTAINER" && docker rm "$DATANODE_CONTAINER"
fi

echo "🚀 Starting DataNode..."
docker run -d --name "$DATANODE_CONTAINER" \
    --net "$NETWORK_NAME" \
    -e CORE_CONF_fs_defaultFS=hdfs://namenode:9000 \
    -e HDFS_CONF_dfs_replication=1 \
    -e SERVICE_PRECONDITION="namenode:9000" \
    "$HADOOP_IMAGE_DN"

if [ $? -ne 0 ]; then
    echo "❌ Failed to start DataNode container." >&2
    exit 1
fi

# Wait for containers to initialize
echo "⏳ Waiting for HDFS to start..."
sleep 10

# Check HDFS status
echo "🔍 Checking HDFS status..."
docker exec -it "$NAMENODE_CONTAINER" hdfs dfsadmin -report

echo "🎉 HDFS setup complete!"
echo "🌐 Access NameNode UI at: http://localhost:9870"
echo "📂 Use HDFS commands with: docker exec -it $NAMENODE_CONTAINER hdfs dfs -ls /"
