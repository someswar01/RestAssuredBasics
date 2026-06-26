package BasicsOfRestAssured;

import BasicsOfRestAssured.Pojo.pojoForDeserializationWithCourcesAPI.GetCourse;
import BasicsOfRestAssured.Pojo.pojoForDeserializationWithCourcesAPI.WebAutomation;
import Files.ReusableMethods;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;

public class OAuthValidationCompleteWithDeserialization {

    public static void main(String[] args) {

        /*****************************************************************
         * STEP 1 : Generate OAuth Access Token     by sending form parameters
         *****************************************************************/

        String tokenResponse = given()
                .baseUri("https://rahulshettyacademy.com")
                .formParam("client_id",
                        "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
                .formParam("client_secret",
                        "erZOWM9g3UtwNRj340YYaK_W")
                .formParam("grant_type",
                        "client_credentials")
                .formParam("scope",
                        "trust")
                .when()
                .post("/oauthapi/oauth2/resourceOwner/token")
                .asString();

        System.out.println("Token Response :");
        System.out.println(tokenResponse);

        JsonPath tokenJson = ReusableMethods.rawToJson(tokenResponse);

        String accessToken =
                tokenJson.getString("access_token");

        /*****************************************************************
         * STEP 2 : Validate Token
         *****************************************************************/

        Assert.assertNotNull(accessToken);
        Assert.assertFalse(accessToken.isEmpty());

        System.out.println("\nAccess Token : " + accessToken);

        /*****************************************************************
         * STEP 3 : Get Course Details API Response
         *****************************************************************/

        Response response =
                given()
                        .baseUri("https://rahulshettyacademy.com")
                        .param("access_token", accessToken)
                        .when()
                        .get("/oauthapi/getCourseDetails");

        /*****************************************************************
         * STEP 4 : Status Code Validation
         *****************************************************************/

        Assert.assertEquals(
                response.statusCode(),
                401);

        System.out.println("\nStatus Code Validated Successfully");

        /*****************************************************************
         * STEP 5 : Header Validation
         *****************************************************************/

        String contentType =response.getHeader("Content-Type");

        Assert.assertTrue(contentType.contains("application/json"));

        System.out.println("Content-Type Validated Successfully");

        /*****************************************************************
         * STEP 6 : JsonPath Validation
         *****************************************************************/

        JsonPath js =
                ReusableMethods.rawToJson(
                        response.asString());

        Assert.assertEquals(js.getString("instructor"),"RahulShetty");

        Assert.assertEquals(js.getString("expertise"),"Automation");

        System.out.println("Instructor and Expertise Validated");

        /*****************************************************************
         * STEP 7 : Deserialization
         *****************************************************************/

        GetCourse gc = response.as(GetCourse.class);

        /*****************************************************************
         * STEP 8 : Root Level Validation
         *****************************************************************/

        Assert.assertEquals(
                gc.getInstructor(),
                "RahulShetty");

        Assert.assertEquals(
                gc.getExpertise(),
                "Automation");

        System.out.println("POJO Root Fields Validated");

        /*****************************************************************
         * STEP 9 : Print Basic Details
         *****************************************************************/

        System.out.println("\nInstructor : "
                + gc.getInstructor());

        System.out.println("URL : "
                + gc.getUrl());

        System.out.println("LinkedIn : "
                + gc.getLinkedIn());

        /*****************************************************************
         * STEP 10 : Find SoapUI Course Price
         *****************************************************************/


    for (int i=0;i<gc.getCourses().getApi().size();i++){
        String title=gc.getCourses().getApi().get(i).getCourseTitle();
        if(title.equalsIgnoreCase("SoapUI Webservices testing")){
            System.out.println(gc.getCourses().getApi().get(i).getPrice());
        }
    }

        /*****************************************************************
         * STEP 11 : Validate Web Automation Courses
         *****************************************************************/

        String[] expectedCourses = {
                "Selenium Webdriver Java",
                "Cypress",
                "Protractor"
        };

        List<String> actualCourses =
                new ArrayList<>();

        for (WebAutomation course :
                gc.getCourses().getWebAutomation()) {

            actualCourses.add(
                    course.getCourseTitle());
        }

        for (int i = 0;
             i < expectedCourses.length;
             i++) {

            Assert.assertEquals(
                    actualCourses.get(i),
                    expectedCourses[i]);

            System.out.println(
                    (i + 1)
                            + ". Course Validated : "
                            + actualCourses.get(i));
        }

        /*****************************************************************
         * STEP 12 : Validate Course Counts
         *****************************************************************/

        int apiCount =gc.getCourses().getApi().size();

        int webCount =gc.getCourses().getWebAutomation().size();

        int mobileCount =gc.getCourses().getMobile().size();

        System.out.println("\nAPI Courses Count : "
                + apiCount);

        System.out.println("Web Courses Count : "
                + webCount);

        System.out.println("Mobile Courses Count : "
                + mobileCount);

        Assert.assertEquals(apiCount, 2);
        Assert.assertEquals(webCount, 3);
        Assert.assertEquals(mobileCount, 1);

        /*****************************************************************
         * STEP 13 : Total Course Validation
         *****************************************************************/

        int totalCourses =
                apiCount +
                        webCount +
                        mobileCount;

        Assert.assertEquals(totalCourses, 6);

        System.out.println(
                "\nTotal Courses : "
                        + totalCourses);

        /*****************************************************************
         * STEP 14 : Price Validation
         *****************************************************************/

        for (WebAutomation course :
                gc.getCourses()
                        .getWebAutomation()) {

            Assert.assertNotNull(
                    course.getPrice());

            System.out.println(
                    course.getCourseTitle()
                            + " Price : "
                            + course.getPrice());
        }

        /*****************************************************************
         * STEP 15 : Soft Assertions
         *****************************************************************/

        SoftAssert softAssert =
                new SoftAssert();

        softAssert.assertEquals(
                gc.getInstructor(),
                "RahulShetty");

        softAssert.assertEquals(
                gc.getExpertise(),
                "Automation");

        softAssert.assertEquals(
                gc.getUrl(),
                "rahulshettycademy.com");

        softAssert.assertAll();

        /*****************************************************************
         * TEST COMPLETED
         *****************************************************************/

        System.out.println(
                "\n******** ALL VALIDATIONS PASSED ********");
    }
}