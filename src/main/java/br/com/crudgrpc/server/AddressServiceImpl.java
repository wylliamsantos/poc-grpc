package br.com.crudgrpc.server;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.DeleteResult;
import com.proto.address.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class AddressServiceImpl extends AddressServiceGrpc.AddressServiceImplBase {

    private MongoClient mongoClient = MongoClients.create("mongodb://root:root@localhost:27017");
    private MongoDatabase database = mongoClient.getDatabase("ntconsult_grpc");
    private MongoCollection<Document> collection = database.getCollection("address");

    @Override
    public void createAddress(CreateAddressRequest request, StreamObserver<CreateAddressResponse> responseObserver) {
        System.out.println("Creating Address");
        Address address = request.getAddress();
        Document doc = new Document("street", address.getStreet())
                .append("number", address.getNumber());
        collection.insertOne(doc);
        String AddressID = doc.getObjectId("_id").toString();
        System.out.println("Inserted Address: " + AddressID);
        CreateAddressResponse response = CreateAddressResponse.newBuilder()
                .setAddress(address.toBuilder().setId(AddressID).build())
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void deleteAddress(DeleteAddressRequest request, StreamObserver<DeleteAddressResponse> responseObserver) {
        String AddressID = request.getAddressId();
        DeleteResult result = null;
        try{
            result = collection.deleteOne(eq("_id", new ObjectId(AddressID)));
        }catch (Exception e){
            System.out.println("Address not found");
            responseObserver.onError(
                    Status.NOT_FOUND
                    .withDescription("Address not found for id: " + AddressID)
                    .augmentDescription(e.getLocalizedMessage())
                    .asRuntimeException()
            );
        }
        if(result.getDeletedCount() == 0){
            System.out.println("Address not found for id: " + AddressID);
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("Address not found for id: " + AddressID)
                            .asRuntimeException()
            );
        }else{
            System.out.println("Address was deleted");
            responseObserver.onNext(
                    DeleteAddressResponse.newBuilder()
                    .setAddressId(AddressID)
                    .build()
            );
            responseObserver.onCompleted();
        }
    }

    @Override
    public void listAddress(ListAddressRequest request, StreamObserver<ListAddressResponse> responseObserver) {
        System.out.println("Streaming Addresss");
        collection.find().iterator().forEachRemaining(document -> responseObserver.onNext(
                ListAddressResponse.newBuilder()
                .setAddress(this.documentToAddress(document))
                .build()
        ));
        responseObserver.onCompleted();
    }

    private Address documentToAddress(Document document){
        return Address.newBuilder()
                .setStreet(document.getString("street"))
                .setNumber(document.getString("number"))
                .build();
    }
}
