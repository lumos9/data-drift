# 🌊 **Data Drift**

*A High-Performance Java-Based ETL Framework*

## 🚀 **Overview**

**Data Drift** is a flexible and scalable ETL (Extract, Transform, Load) framework built in Java, designed to handle
diverse data sources and sinks with ease. It enables seamless data integration from streaming platforms like Kafka and
traditional databases to modern storage and processing systems.

## 🎯 **Features**

- ✅ Support for multiple data sources (e.g., Kafka, databases)
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
3. Set up the pipeline
    ```bash
    ./setup.sh
    ```
4. Build the project
    ```bash
    ./gradlew clean build
     ```

## Usage

To use this project, follow these steps:

1. Start the Kafka Consumer one shell:
    ```bash
    ./start-kafka-consumer-flink-db-sink.sh
    ```
2. Start the Kafka Producer in another shell:
    ```bash
    ./start-kafka-producer.sh
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
