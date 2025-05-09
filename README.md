# Making Cross-Platform REST API Calls with Kotlin

This repository demonstrates how to to build a REST API with Kotlin to handle travel data using the Amadeus API.

## Prerequisites

To run this project locally, you need to have the following:

-   [Amadeus for developers](https://developers.amadeus.com) account
-   [JDK 17 or later](https://www.oracle.com/ke/java/technologies/downloads/) installed on your local machine
-   [Docker Engine](https://docs.docker.com/engine/install/) and [Docker Compose](https://docs.docker.com/compose/install/) installed on your local machine
-   A code editor. [IntelliJ IDEA](https://www.jetbrains.com/idea/) is recommended for Kotlin development, but any code editor will work.
-   [curl](https://curl.se/) for testing the endpoints.

## Obtaining Amadeus Credentials

1. Sign into your [Amadeus dashboard](https://developers.amadeus.com) and navigate to [My Self-Service Workspace](https://developers.amadeus.com/my-apps):

    ![Amadeus self-service workspace](https://i.imgur.com/HDY0NiU.png)

2. Select **Create a new app** to open the "Create new app" form. In the form, provide "kotlin-rest-api-demo" as the app name and click **Create** to create the application:

    ![Creating a new application](https://i.imgur.com/t9ynCbS.png)

3. Once the app is created, you will be navigated to the details page. Here, take note of your test keys. You will use them later to obtain an access token:

    ![Application details page](https://i.imgur.com/5rFsbiF.png)

## Running Locally

To run this project on your local machine, follow these steps:

1. Clone the project:
    
    ```bash
    git clone https://github.com/kimanikevin254/amadeus-rest-api-demo.git
    ```
   
2. `cd` into the project folder:
    
    ```bash
   cd  amadeus-rest-api-demo
   ```
   
3. Rename `.env.example` to `.env` and replace the placeholder values with the actual values you obtained from the Amadeus dashboard.
4. Start the Docker container:
    
    ```bash
   docker compose up -d --build 
   ```
   
5. Once the container is up and running, execute the command below in the terminal to check if everything is working as expected:

    ```bash
    curl "http://localhost:8080/search/cities?keyword=PARIS&max=2"
    ```

6. You should get the following output:

    ```json
    {
        "data": [
            {
                "type": "location",
                "subType": "city",
                "name": "Paris",
                "iataCode": "PAR",
                "address": {
                    "postalCode": null,
                    "countryCode": "FR",
                    "stateCode": "FR-75"
                },
                "geoCode": {
                    "latitude": 48.85341,
                    "longitude": 2.3488
                }
            },
            {
                "type": "location",
                "subType": "city",
                "name": "Le Touquet-Paris-Plage",
                "iataCode": "LTQ",
                "address": {
                    "postalCode": null,
                    "countryCode": "FR",
                    "stateCode": "FR-62"
                },
                "geoCode": {
                    "latitude": 50.52432,
                    "longitude": 1.58571
                }
            }
        ]
    }
    ```