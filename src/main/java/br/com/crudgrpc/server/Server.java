package br.com.crudgrpc.server;

import io.grpc.ServerBuilder;
import java.io.IOException;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.println("Server start");
        io.grpc.Server server = ServerBuilder.forPort(50051)
                .addService(new UserServiceImpl())
                .addService(new AddressServiceImpl())
                .build();
        server.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.shutdown();
        }));
        server.awaitTermination();
    }
}
