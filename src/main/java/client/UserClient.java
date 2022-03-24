package client;

import com.proto.user.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class UserClient {

    public static void main(String[] args) {
        UserClient main = new UserClient();
        main.run();
    }

    private void run(){
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 50051)
                .usePlaintext()
                .build();
        UserServiceGrpc.UserServiceBlockingStub userClient = UserServiceGrpc.newBlockingStub(channel);

        User user = User.newBuilder()
                .setName("ntconsult")
                .setEmail("ntconsult@ntconsult.com")
                .build();
        CreateUserResponse createUserResponse = userClient.createUser(
                CreateUserRequest.newBuilder()
                        .setUser(user)
                        .build()
        );
        System.out.println(createUserResponse.toString());

        String userId = createUserResponse.getUser().getId();
        DeleteUserResponse deleteUserResponse = userClient.deleteUser(
                DeleteUserRequest.newBuilder().setUserId(userId).build()
        );
        System.out.println(deleteUserResponse.toString());

        userClient.listUser(ListUserRequest.newBuilder().build()).forEachRemaining(
                listUserResponse -> System.out.println(listUserResponse.getUser().toString())
        );

    }

}
