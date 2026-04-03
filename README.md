# Read Me First

This is a combination of things that can be used as they are, and incredibly contrived examples.

There are bad practices left in because they allow easy demonstrations, but which would be unacceptable
in production code. For example, users should not be able to sign up as admins with zero checks

## Subprojects

### [Backend](./backend/README.md)

Java/Spring Boot project used to implement the domain logic

### [Frontend](./frontend/README.md)

Typescript/React project used to present an user-friendly way to interact with the business logic

### [Ingress](./ingress/README.md)

Docker only. Redirects http requests to other containers depending on its configuration

Used to have multiple containers under a single http domain

## Docker
 is a
Dockern abstraction layer that allows applications to run in a loosely isolated context called a container

Install docker and docker-compose following the instructions in https://docs.docker.com/get-docker/

Every project has a Dockerfile that specifies how its code should be run

### Running

To run the whole system in docker compose

1. Review the environment variables in .env

2. Start the system
   ```bash
   docker compose up -d --build --remove-orphans
   ```

## CI/CD

This project is designed to deploy to a server we have in the cloud

![Server layout, showing how all the containers nest](docs/server.png)

## Entregables

Sprint 1:
https://drive.google.com/drive/folders/1JAMdP34s8OLOotGd5m4oXwyUlAucoQpO?usp=sharing

Sprint 2:
https://drive.google.com/drive/folders/1dtzMeCxkHgLxN3_OLnjQF8mw5vPutKhN?usp=drive_link

Sprint 3:
https://drive.google.com/drive/folders/1DEfI4ww14RdhMfvByBbSFFvNKEcsvBzI?usp=sharing 

Sprint 4:
https://drive.google.com/drive/folders/1KCzumwZ2PRlQMttitAg8Vr1LYzRJZUaX?usp=sharing

Entrega Final:
https://drive.google.com/drive/folders/1W9O6uJU6BrYQebC8KHChqLTS-ksYDja0?usp=sharing