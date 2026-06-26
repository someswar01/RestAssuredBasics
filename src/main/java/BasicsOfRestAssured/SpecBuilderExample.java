package BasicsOfRestAssured;

import BasicsOfRestAssured.Pojo.pojoForSerializationOfAnAddPlaceInMap.AddPlace;
import BasicsOfRestAssured.Pojo.pojoForSerializationOfAnAddPlaceInMap.Location;
import Files.PayLoad;
import Files.ReusableMethods;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class SpecBuilderExample {
    @Test
    public void verifyCRUDOperationsWithReqAndResSpec() {

        System.out.println("========== CRUD OPERATION STARTED ==========");

        // Create Place Payload
        System.out.println("Step 1 : Creating Place Payload");

        AddPlace addPlace = new AddPlace();
        addPlace.setAccuracy(52);
        addPlace.setAddress("29, side layout, cohen 08");
        addPlace.setName("Frontline house");
        addPlace.setLanguage("French-IN");
        addPlace.setPhone_number("(+91) 983 893 3937");
        addPlace.setWebsite("https://rahulshettyacademy.com");

        List<String> types = new ArrayList<>();
        types.add("shoe park");
        types.add("shop");
        addPlace.setTypes(types);

        Location location = new Location();
        location.setLat(-38.383494);
        location.setLng(33.427362);
        addPlace.setLocation(location);

        // Request Spec
        RequestSpecification req = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .addQueryParam("key", "qaclick123")
                .setContentType(ContentType.JSON)
                .build();

        // Response Spec
        ResponseSpecification res = new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();

        // POST
        System.out.println("Step 2 : Sending POST Request - Create Place");

        String addPlaceResponse = given()
                .spec(req)
                .body(addPlace)
                .when()
                .post("/maps/api/place/add/json")
                .then()
                .spec(res)
                .body("scope", equalTo("APP"))
                .extract()
                .response()
                .asString();

        JsonPath js = new JsonPath(addPlaceResponse);
        String placeId = js.getString("place_id");

        System.out.println("Place Created Successfully");
        System.out.println("Generated Place ID : " + placeId);

        // GET
        System.out.println("Step 3 : Sending GET Request - Verify Created Place");

        String getResponse = given()
                .spec(req)
                .queryParam("place_id", placeId)
                .when()
                .get("/maps/api/place/get/json")
                .then()
                .spec(res)
                .extract()
                .response()
                .asString();

        JsonPath getJson = new JsonPath(getResponse);

        Assert.assertEquals(
                getJson.getString("address"),
                "29, side layout, cohen 08");

        System.out.println("Initial Address Validation Passed");

        // PUT
        String newAddress = "Chinnahulthi, Pattikonda M";

        System.out.println("Step 4 : Sending PUT Request - Update Address");
        System.out.println("New Address : " + newAddress);

        given()
                .spec(req)
                .body(PayLoad.updatePlace(placeId, newAddress))
                .when()
                .put("/maps/api/place/update/json")
                .then()
                .spec(res)
                .body("msg",
                        equalTo("Address successfully updated"));

        System.out.println("Address Updated Successfully");

        // GET
        System.out.println("Step 5 : Sending GET Request - Verify Updated Address");

        String updatedResponse = given()
                .spec(req)
                .queryParam("place_id", placeId)
                .when()
                .get("/maps/api/place/get/json")
                .then()
                .spec(res)
                .extract()
                .response()
                .asString();

        JsonPath updatedJson = new JsonPath(updatedResponse);

        Assert.assertEquals(
                updatedJson.getString("address"),
                newAddress);

        System.out.println("Updated Address Validation Passed");

        // DELETE
        System.out.println("Step 6 : Sending DELETE Request - Delete Place");

        given()
                .spec(req)
                .body(PayLoad.deletePlace(placeId))
                .when()
                .delete("/maps/api/place/delete/json")
                .then()
                .spec(res)
                .body("status", equalTo("OK"));

        System.out.println("Place Deleted Successfully");

        System.out.println("========== CRUD OPERATION COMPLETED ==========");
    }
}
