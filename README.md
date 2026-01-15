# Assessment Engine
## About

Assessment Engine is a console-based survey and test management system that allows users to create, display, load, store, take, modify, grade, and tabulate surveys and tests through a menu-driven interface.

## Highlights

Designed a console-based survey and test assessment engine

Supported creation, execution, and persistence of assessments through a menu-driven interface

Supported multiple question types, including Multiple Choice, True/False, Matching, Short Answer, Essay, and Date questions

Implemented automatic grading logic via a Gradable interface

Developed a file-based persistence layer using Java serialization and structured directories

## Installation

This project is built using Java and Apache Maven.

### Requirements

Java 22.0.2

Apache Maven

Verify Your Environment

Ensure Java and Maven are installed:

```bash
java --version
mvn --version
```

Install Dependencies (Ubuntu / Debian)

If Java or Maven is not installed:

```bash 
sudo apt update
sudo apt install -y openjdk-22-jdk maven
```

Build the Project

From the project root directory:

```bash
mvn clean package
```

After the build completes, locate the generated JAR file:

```bash
ls target
```

Run the Application

Replace <your-jar-name> with the generated JAR file name:

```bash
java -jar target/<your-jar-name>.jar
```

## Overview

The application operates through a text-based, menu-driven interface that allows users to manage both Surveys and Tests.

At the top level, users select whether to work with a survey or a test, each providing its own submenu with options to create, display, load, save, modify, take, grade, and tabulate assessments.

The system supports the following question types:

True / False

Multiple Choice (single or multiple responses)

Short Answer

Essay

Date

Matching

For Surveys, the system stores user responses and provides tabulation features to summarize results.

For Tests, the system additionally supports defining correct answers, displaying tests with or without answer keys, and automatically grading responses using the Gradable interface. Essay questions are excluded from auto-grading to ensure accurate score calculation.

All assessments and responses are persisted using Java serialization, allowing data to remain available across program executions. The application also includes robust input validation to handle improper input gracefully without crashing.
