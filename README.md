# TFCMarcom

# Automated Testing for OMDB API

This repository contains automated tests for the [OMDB (Open Movie Database) API](https://www.omdbapi.com/), utilizing Java and RestAssured libraries. The tests are designed to ensure the OMDB API functions correctly and provide reliable data for movies and TV series.

Libraries used

* [MAVEN](https://maven.apache.org/download.cgi)
* [JAVA](https://www.oracle.com/java/technologies/downloads/#java17)
* [REST ASSURED](https://rest-assured.io/)
* [TESTNG](https://testng.org/doc/)
* [ALLURE REPORT](https://allurereport.org/docs/)

## Getting Started

To begin using these automated tests, follow the below steps:

### Prerequisites

Before running the tests, ensure you have the following prerequisites in place.

* [Java 17](https://www.oracle.com/java/technologies/downloads/#java17) or above
* [Maven](https://maven.apache.org/install.html)

## HOW TO RUN THE TEST

To run the automated tests, execute the following command in your terminal

Go to project folder and type:

    cd TFCMarcom/ 

In command window and run:

```bash
mvn clean test
```

Above command will run all the API tests

## Test Report

This project integrates with Allure for test reporting. After running the tests, you can generate interactive and informative test reports using Allure. Open the generated report with:

```bash
allure serve
```

If you get below error message in running the above command, 

    bash: allure: command not found

then you can use one of the following ways to get Allure:

Using Homebrew:

    $ brew install allure

For Windows, Allure is available from the Scoop commandline-installer. To install Allure, download and install Scoop and then execute in the Powershell:
    
    scoop install allure

Default TestNG reports can be accessed at 

    /TFCMarcom/target/surefire-reports/emailable-report.html

## Logs

Logs is generated at the below location

    ./log/log_{currentTimeMills}.txt

## Test Cases 

Kindly click on the below link to access test cases

* [Test Cases](https://docs.google.com/spreadsheets/d/1oLfWtkv9VlvYo50ioyHWvSGMl1Zp_DIeM8KE8JIouHM/edit?usp=sharing)
