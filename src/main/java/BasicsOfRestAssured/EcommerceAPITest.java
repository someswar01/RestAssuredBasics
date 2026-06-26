package BasicsOfRestAssured;

import BasicsOfRestAssured.Pojo.LoginRequest;
import BasicsOfRestAssured.Pojo.LoginResponse;
import BasicsOfRestAssured.Pojo.OrderDetails;
import BasicsOfRestAssured.Pojo.Orders;
import Files.ReusableMethods;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class EcommerceAPITest {
    public static void main(String[] args) {

        System.out.println("========== ECOMMERCE API FLOW STARTED ==========");

        // ==================================================
        // LOGIN
        // ==================================================
        System.out.println("Step 1 : Login and Get Token");

        RequestSpecification loginReqSpec = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .setContentType(ContentType.JSON)
                .build();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserEmail("Shanker@123.com");
        loginRequest.setUserPassword("Shanker@123");

        LoginResponse loginResponse = given()
                .spec(loginReqSpec)
                .body(loginRequest)
                .when()
                .post("/api/ecom/auth/login")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response()
                .as(LoginResponse.class);

        String token = loginResponse.getToken();
        String userId = loginResponse.getUserId();

        System.out.println("Login Successful");
        System.out.println("User Id : " + userId);

        // ==================================================
        // ADD PRODUCT
        // ==================================================
        System.out.println("\nStep 2 : Add Product");

        RequestSpecification addProductSpec = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .addHeader("authorization", token)
                .build();

        String addProductResponse = given()
                .spec(addProductSpec)
                .param("productName", "Laptop")
                .param("productAddedBy", userId)
                .param("productCategory", "Electronics")
                .param("productSubCategory", "Gaming")
                .param("productPrice", "85000")
                .param("productDescription", "Latest Model")
                .param("productFor", "Automation Practice")
                .multiPart("productImage",
                        new File("src/main/resources/FilesForUpload/HeadPhones1.jpg"))
                .when()
                .post("/api/ecom/product/add-product")
                .then()
                .assertThat()
                .statusCode(201)
                .extract()
                .response()
                .asString();

        JsonPath productJson = new JsonPath(addProductResponse);
        String productId = productJson.getString("productId");

        System.out.println("Product Added Successfully");
        System.out.println("Product Id : " + productId);

        // ==================================================
        // CREATE ORDER
        // ==================================================
        System.out.println("\nStep 3 : Create Order");

        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setCountry("India");
        orderDetails.setProductOrderedId(productId);

        List<OrderDetails> orderList = new ArrayList<>();
        orderList.add(orderDetails);

        Orders orders = new Orders();
        orders.setOrders(orderList); // IMPORTANT

        RequestSpecification createOrderSpec = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .addHeader("authorization", token)
                .setContentType(ContentType.JSON)
                .build();

        String createOrderResponse = given()
                .spec(createOrderSpec)
                .body(orders)
                .when()
                .post("/api/ecom/order/create-order")
                .then()
                .log().all()
                .extract()
                .response()
                .asString();

        System.out.println("Order Created Successfully");
        System.out.println(createOrderResponse);

        // ==================================================
        // DELETE PRODUCT
        // ==================================================
        System.out.println("\nStep 4 : Delete Product");

        String deleteResponse = given()
                .baseUri("https://rahulshettyacademy.com")
                .header("authorization", token)
                .pathParam("productId", productId)
                .when()
                .delete("/api/ecom/product/delete-product/{productId}")
                .then()
                .assertThat()
                .statusCode(200)
                .extract()
                .response()
                .asString();

        System.out.println("Product Deleted Successfully");
        System.out.println(deleteResponse);

        System.out.println("\n========== ECOMMERCE API FLOW COMPLETED ==========");
    }
}
