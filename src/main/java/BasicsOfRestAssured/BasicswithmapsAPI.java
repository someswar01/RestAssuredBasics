package BasicsOfRestAssured;
import Files.PayLoad;
import Files.ReusableMethods;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class BasicswithmapsAPI {
    public static void main(String[] args) {
//        Given - all input details
//        When - Submit the API -resources ,http method
//        Then - validate response

        RestAssured.baseURI="https://rahulshettyacademy.com";
        String response=given().log().all().queryParam("key","qaclick123").header("Content-Type","application/json")
                .body(PayLoad.AddPlace()).when().post("/maps/api/place/add/json")
                .then().assertThat().statusCode(200).body("scope",equalTo("APP"))
                .header("Server",equalTo("Apache/2.4.52 (Ubuntu)"))
                .extract().response().asString();

        System.out.println(response);

        JsonPath js =new JsonPath(response);
        String placeID=js.getString("place_id");

        System.out.println("Actual Place ID:- "+placeID);


        //Update Place
        String newAddressToUpdate="Chinnahulthi,pattikonda Mandal";
        given().log().all().queryParam("key","qaclick123").header("Content-Type","application/json")
                .body(PayLoad.updatePlace(placeID,newAddressToUpdate))
                .when().put("/maps/api/place/update/json")
                .then().log().all().assertThat().statusCode(200)
                .body("msg",equalTo("Address successfully updated"));

         //get place
        String getPlaceResponse=given().queryParam("key","qaclick123")
                .queryParam("place_id",placeID)
                .when().get("/maps/api/place/get/json")
                .then().log().all().assertThat().statusCode(200).extract().response().asString();

        JsonPath js1=ReusableMethods.rawToJson(getPlaceResponse);
        String updatedAddressRetrivingFromGetReq= js1.getString("address");

        Assert.assertEquals(newAddressToUpdate,updatedAddressRetrivingFromGetReq);
    }
}
