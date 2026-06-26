package BasicsOfRestAssured;

import BasicsOfRestAssured.Pojo.pojoForSerializationOfAnAddPlaceInMap.AddPlace;
import BasicsOfRestAssured.Pojo.pojoForSerializationOfAnAddPlaceInMap.Location;
import Files.PayLoad;
import Files.ReusableMethods;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

public class AddPlaceIntoMapsWithSerialization {

    @Test
    public void verifyCRUDOperations() {

        // Create Place Payload using Serialization
        AddPlace addPlace = new AddPlace();

        addPlace.setAddress("29, side layout, cohen 08");
        addPlace.setName("Frontline house");
        addPlace.setLanguage("French-IN");
        addPlace.setPhone_number("(+91) 983 893 3937");
        addPlace.setWebsite("https://rahulshettyacademy.com");
        addPlace.setAccuracy(52);
        List<String> types = new ArrayList<>();
        types.add("shoe park");
        types.add("shop");
        addPlace.setTypes(types);

        Location location = new Location();
        location.setLat(-38.383494);
        location.setLng(33.427362);

        addPlace.setLocation(location);
        System.out.println(" Created Place Payload using Serialization");

        RestAssured.baseURI = "https://rahulshettyacademy.com";

        // ==================================
        // POST - Add Place
        // ==================================
        System.out.println("POST - Add Place");

        String addPlaceResponse = given()
                .queryParam("key", "qaclick123")
                .header("Content-Type", "application/json")
                .body(addPlace)
                .when()
                .post("/maps/api/place/add/json")
                .then()
                .assertThat()
                .statusCode(200)
                .body("scope", equalTo("APP"))
                .extract()
                .response()
                .asString();

        JsonPath js = new JsonPath(addPlaceResponse);
        String placeId = js.getString("place_id");

        System.out.println("Created Place ID : " + placeId);

        // ==================================
        // GET - Verify Place Created
        // ==================================
        System.out.println("GET - Verify Place Created");

        String getResponse = given()
                .queryParam("key", "qaclick123")
                .queryParam("place_id", placeId)
                .when()
                .get("/maps/api/place/get/json")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response()
                .asString();

        JsonPath getJson = ReusableMethods.rawToJson(getResponse);
        Assert.assertEquals(
                getJson.getString("address"),
                "29, side layout, cohen 08");

        // ==================================
        // PUT - Update Address
        // ==================================
        System.out.println("PUT - Update Address");

        String newAddress = "Chinnahulthi, Pattikonda M";

        given()
                .queryParam("key", "qaclick123")
                .header("Content-Type", "application/json")
                .body(PayLoad.updatePlace(placeId,newAddress))
                .when()
                .put("/maps/api/place/update/json")
                .then()
                .assertThat()
                .statusCode(200)
                .body("msg",
                        equalTo("Address successfully updated"));

        System.out.println("new address updated validate success Message");
        // ==================================
        // GET - Verify Updated Address
        // ==================================
        System.out.println("GET - Verify Updated Address");

        String updatedResponse = given()
                .queryParam("key", "qaclick123")
                .queryParam("place_id", placeId)
                .when()
                .get("/maps/api/place/get/json")
                .then()
                .extract()
                .response()
                .asString();

        JsonPath updatedJson = new JsonPath(updatedResponse);

        Assert.assertEquals(
                updatedJson.getString("address"),
                newAddress);

        System.out.println("Updated Address Validated Successfully");
        System.out.println("Address Updated Successfully");

        // ==================================
        // DELETE - Delete Place
        // ==================================
        System.out.println("DELETE - Delete Place");
        given()
                .queryParam("key", "qaclick123")
                .header("Content-Type", "application/json")
                .body(PayLoad.deletePlace(placeId))
                .when()
                .delete("/maps/api/place/delete/json")
                .then()
                .assertThat()
                .statusCode(200)
                .body("status", equalTo("OK"));

        System.out.println("Place Deleted Successfully");
    }
}
