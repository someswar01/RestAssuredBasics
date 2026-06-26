package BasicsOfRestAssured;

import Files.PayLoad;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.List;

public class ComplexJsonParse {
    public static void main(String[] args) {
        JsonPath js=new JsonPath(PayLoad.CoursePrice());

        //no of cases
       int count= js.getInt("courses.size()");
        System.out.println(count);

        //purchase amount
        int pa=js.getInt("dashboard.purchaseAmount");
        System.out.println(pa);

        //print title of the first course
        String firstCourseName=js.getString("courses[0].title");
        System.out.println("First Course Title:- "+firstCourseName);

        //print all course titles and their respective prices

        for(int i=0;i<count;i++){
            String titles=js.getString("courses["+i+"].title");
            int prices=js.getInt("courses["+i+"].price");

            System.out.println("Title of array :- "+ titles+" and its price is:- "+prices);

            System.out.println(js.get("courses["+i+"].price").toString());
        }

        for (int i=0;i<count;i++){
            String tit=js.getString("courses["+i+"].title");
            if(tit.equals("RPA")){
                int copies= js.getInt("courses["+i+"].copies");
                System.out.println("Retrieving value of copies when title matches RPA :-"+copies);
                break;
            }
        }

        int purchaseAmount=js.getInt("dashboard.purchaseAmount");
        int totalAmount=0;
        for (int i = 0; i< count; i++){
            int prices=js.getInt("courses["+i+"].price");
            int copies=js.getInt("courses["+i+"].copies");
            int pricePerCourse=prices*copies;
            totalAmount+=pricePerCourse;

        }
        Assert.assertEquals(totalAmount,purchaseAmount);
    }
}
