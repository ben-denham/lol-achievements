# Taken from: https://hub.docker.com/_/clojure/
FROM clojure

# Make a directory for the app in the Docker container.
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

# By copying project.clj by itself and installing dependencies before
# the rest of the files are copied, an intermediate image with
# dependencies will exist and only be updated when dependencies in
# project.clj change.
COPY project.clj /usr/src/app/
RUN lein deps

# Copy the rest of the app files.
COPY . /usr/src/app

# Expose ports 3000 and 7000.
EXPOSE 3000
EXPOSE 7000

# Run the application.
CMD ["lein", "run"]
