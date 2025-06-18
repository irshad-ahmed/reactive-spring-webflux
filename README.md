# reactive-spring-webflux

## Starting MongoDB with Docker

You can start a local MongoDB instance using Docker with the following command:

```
docker run --name mongodb -d -p 27017:27017 -v mongodb_data:/data/db mongo:latest
```

- This will run MongoDB on port 27017.

To stop and remove the container:

```
docker stop mongodb && docker rm mongodb
```

## Spring Webflux

#### Install Mongo DB in MAC

- Run the below command to install the **MongoDB**.

```
brew services stop mongodb
brew uninstall mongodb

brew tap mongodb/brew
brew install mongodb-community
```

-  How to restart MongoDB in your local machine.

```
brew services restart mongodb-community
```

#### Install Mongo DB in Windows

- Follow the steps in the below link to install Mongo db in Windows.

https://docs.mongodb.com/manual/tutorial/install-mongodb-on-windows/
