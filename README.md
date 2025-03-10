# 🌊 **Data Drift**

*A High-Performance Java-Based ETL Framework*

## 🚀 **Overview**

**Data Drift** is a flexible and scalable ETL (Extract, Transform, Load) framework built in Java, designed to handle
diverse data sources and sinks with ease. It enables seamless data integration from streaming platforms like Local File System, Aws S3, Kafka and
traditional databases to modern storage and processing systems. All the processing can be done with just a yml config file

## 🎯 **Features**

- ✅ Support for multiple data sources (e.g., files, S3, Kafka, databases)
- ✅ All sources and sinks are configured with one config file
- ✅ Configurable pipelines for different transformation needs
- ✅ High-performance, fault-tolerant processing
- ✅ Modular architecture for easy extension

### Prerequisites

Before you begin, ensure you have met the following requirements:

- You have installed Java 21 or higher.
- You have installed Docker.
- You have installed Netcat.
- You have installed Git.

[//]: # (- You have a [OS type] machine. [Specify any OS-specific instructions if necessary].)

### 🛠️ **Installation**

1. Clone the repository:
    ```bash
    git clone https://github.com/lumos9/data-drift.git
    ```
2. Navigate to the project directory:
    ```bash
    cd data-drift
    ```
3. Build the project
    ```bash
    ./gradlew clean build
     ```

## Usage

### 1. **Batch ETL – File Source to Postgres Sink**

**Description:** This pipeline reads data from a file and inserts it into a Postgres table.  
**Prerequisite:** Ensure Postgres is running and accessible.

1. Setup and ensure Postgres is running and accessible. Ignore this step if the db is already available
    ```bash
   #Setup postgres db locally via Docker
   setup/db/jdbc/postgres/setup-pg.sh
   
   #Check if container is listening on port
   nc -vz localhost 5432
   #Should say something similar to following depending on OS you are running
   #Connection to localhost port 5432 [tcp/postgresql] succeeded!
    ```
2. Run Data Drift App with config file
    ```bash
    java -cp build/libs/data-drift-1.0-SNAPSHOT-all.jar org.example.Entrypoint config/source/file/file-source-to-db-sink.yml
    ```

### 2. **Batch ETL – AWS S3 Source to Postgres Sink**

**Description:** This pipeline reads data from AWS S3 file and inserts it into a Postgres table.
**Prerequisite:** Ensure Postgres is running and accessible.

1. Setup and ensure Postgres is running and accessible. Ignore this step if the db is already available
    ```bash
   #Setup postgres db locally via Docker
    setup/db/jdbc/postgres/setup-pg.sh
   
   #Check if container is listening on port
   nc -vz localhost 5432
   #Should say something similar to following depending on OS you are running
   #Connection to localhost port 5432 [tcp/postgresql] succeeded!
    ```
2. Run Data Drift App with config file
   ```bash
   java -cp build/libs/data-drift-1.0-SNAPSHOT-all.jar org.example.Entrypoint config/source/s3/aws-s3-source-to-db-sink.yml
   ```

### 2. **Batch ETL – AWS S3 Source to StdOut Sink**

**Description:** This pipeline reads data from AWS S3 file and logs to StdOut. StdOut is default Sink if no Sink is
configured.

1. Run Data Drift App with config file
   ```bash
   java -cp build/libs/data-drift-1.0-SNAPSHOT-all.jar org.example.Entrypoint config/source/s3/aws-s3-source-to-stdout-sink.yml
   ```

### 3. **Batch ETL – Postgres Source to StdOut Sink**

**Description:** This pipeline reads data from Postgres db table and logs to StdOut. StdOut is default Sink if no Sink
is configured.
**Prerequisite:** Ensure Postgres is running and accessible.

1. Setup and ensure Postgres is running and accessible. Ignore this step if the db is already available
    ```bash
   #Setup postgres db locally via Docker
   setup/db/jdbc/postgres/setup-pg.sh
   
   #Check if container is listening on port
   nc -vz localhost 5432
   #Should say something similar to following depending on OS you are running
   #Connection to localhost port 5432 [tcp/postgresql] succeeded!
    ```
2. Run Data Drift App with config file
   ```bash
   java -cp build/libs/data-drift-1.0-SNAPSHOT-all.jar org.example.Entrypoint config/source/db/db-source.yml
   ```

### 4. **Streaming ETL – Kafka Source to StdOut Sink**

**Description:** This pipeline reads data from a file and inserts it into a Postgres table.  
**Prerequisite:** Ensure Kafka Broker is running and accessible.

1. Start and create Kafka Topic via Docker. This may need 16GB of RAM
    ```bash
    setup/kafka/launch_kafka.sh
    ```
2. Run Data Drift App (Kafka Consumer) in one terminal
    ```bash
    java -cp build/libs/data-drift-1.0-SNAPSHOT-all.jar org.example.Entrypoint config/source/kafka/kafka-source.yml
    ```
3. Run Kafka Producer in another terminal
    ```bash
    ./kafka-producer-to-broker.sh
    ```

[//]: # (Example:)

[//]: # (```bash)

[//]: # ([example command or code snippet])

[//]: # (```)

[//]: # (## Configuration)

[//]: # ()

[//]: # (### Environment Variables)

[//]: # ()

[//]: # (This project requires the following environment variables to be set:)

[//]: # ()

[//]: # (- `ENV_VAR_1`: Description of ENV_VAR_1)

[//]: # (- `ENV_VAR_2`: Description of ENV_VAR_2)

[//]: # ()

[//]: # (### Configuration File)

[//]: # ()

[//]: # (You can configure the project by editing the `config.file` located at `[path to config file]`. Below is an example configuration:)

[//]: # ()

[//]: # (```json)

[//]: # ({)

[//]: # (  "config_key_1": "value",)

[//]: # (  "config_key_2": "value")

[//]: # (})

[//]: # (```)

## Contributing

We welcome contributions!

### Reporting Issues

If you encounter any issues, please create a new issue in this repository. Make sure to provide enough detail for us to
understand and replicate the issue.

### Pull Requests

1. Fork the repository.
2. Create a new branch:
    ```bash
    git checkout -b feature/your-feature-name
    ```
3. Make your changes and commit them:
    ```bash
    git commit -m "Add feature/your-feature-name"
    ```
4. Push to your branch:
    ```bash
    git push origin feature/your-feature-name
    ```
5. Open a pull request.

[//]: # (Please ensure your code adheres to our coding standards and includes appropriate tests.)

[//]: # (## License)

[//]: # ()

[//]: # (This project is licensed under the [LICENSE NAME]. See the [LICENSE]&#40;LICENSE&#41; file for more details.)

## Contact

For any inquiries or questions, please [Contact me](mailto:nchat.dev@proton.me)

[//]: # (---)

[//]: # ()

[//]: # (Thank you for checking out **ETL Pipeline**! We hope you find it useful and engaging. Happy coding!)

[//]: # ()

[//]: # ([Optional: Include any acknowledgments or credits here])

[//]: # ()

[//]: # (---)

[//]: # (*Note: Replace placeholders with actual information relevant to your project.*)
