package BasicsOfRestAssured;

import Files.PayLoad;
import Files.ReusableMethods;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class LibraryAPI {

    @Test
    public void addBook(){
        RestAssured.baseURI="http://216.10.245.166";
        String response=given().header("Content-Type","application/json")
                .body(PayLoad.addBook())
                .when().post("/Library/Addbook.php")
                .then().log().all().assertThat().statusCode(200)
                .extract().response().body().asString();
        System.out.println(response);

//        JsonPath js= ReusableMethods.rawToJson(response);
//        String id=js.get("name").toString();
//        System.out.println(id);
    }
}
