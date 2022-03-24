# poc-grpc

Demonstration project of gRPC that includes a simple server, 2 services and consuming client.

---
**NOTE**

Stub generation inside the project

--- 

## Installing

Just run build gradle task in project root path.

### Stub Generation

This process is automaticly executed once you have well configured protobuf plugin in your build.gradle
All your stubs are created in build/generated/source/proto/main path.

## Running the app

You need a simple instance of mongodb running locally, so here we're going to use docker-compose to simplify the process.

```bash
docker-compose up
```

- After you run the server by class Server.java 
- Then the client by class UserClient.java
