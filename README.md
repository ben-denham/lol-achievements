# lol-achievements

A web application for tracking achievements for League of Legends
players.

## Prerequisites

You will need [Leiningen][1] 2.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run

## Running with Docker

Also see the Dockerfile.

### Setup

Install Docker from http://docs.docker.com/installation/ubuntulinux/

Install Docker Compose from https://docs.docker.com/compose/install/

### Running the app

```bash
docker-compose up
```

### Updating images

If you make a change to the Dockerfile, or update any dependencies,
you should run the following command to update the docker image:

```bash
docker-compose build
```

### Compiling the app

To compile the app from the docker container:

```bash
docker run -it --rm -v "$PWD":/usr/src/app -w /usr/src/app clojure lein uberjar
```
