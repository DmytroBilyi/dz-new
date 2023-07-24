import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Date;

import static org.hamcrest.Matchers.equalTo;

public class RestAssuredTests {
    // http://restful-booker.herokuapp.com/
    public static String TOKEN_VALUE;
    public static final String TOKEN = "token";

    @BeforeMethod
    public void setup() {
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        JSONObject body = new JSONObject();
        body.put("password", "password123");
        body.put("username", "admin");
        Response response = RestAssured.given()
                .body(body.toString())
                .post("/auth");
        response.prettyPrint();
        TOKEN_VALUE = response.then().extract().jsonPath().get(TOKEN);
    }

    @Test
    public void createBooking() {
        BookingDates bookingDates = new BookingDates()
                .builder()
                .checkin(LocalDate.of(2019, 02, 01))
                .checkout(LocalDate.of(2019, 02, 02))
                .build();
        CreateBookingBody body = new CreateBookingBody()
                .builder()
                .firstname("Dmytro")
                .lastname("Bilyi")
                .totalprice(100)
                .depositpaid(true)
                .additionalneeds("Breakfast")
                .bookingdates(bookingDates)
                .build();
        //   JSONObject body = new JSONObject();
        //   body.put("firstname", "Dmytro");
        //   body.put("lastname", "Bilyi");
        //   body.put("totalprice", 100);
        //   body.put("depositpaid", true);
        //   JSONObject bookingdates = new JSONObject();
        //   bookingdates.put("checkin", "2013-02-23");
        //  bookingdates.put("checkout", "2014-10-23");
        //  body.put("bookingdates", bookingdates);
        // body.put("additionalneeds", "Breakfast");

        Response response = RestAssured.given().log().all()
                .body(body)
                .post("/booking");
        response.prettyPrint();
    }

    @Test
    public void getIDs() {
        Response response = RestAssured.get("/booking/");
        response.then().statusCode(200);
        response.prettyPrint();
    }

    @Test
    public void changePriceOfBooking() {
        JSONObject body = new JSONObject();
        body.put("totalprice", 113);
        Response response = RestAssured.given().log().all()
                .cookie(TOKEN, TOKEN_VALUE)
                .body(body.toString())
                .patch("/booking/{id}", 2333);
        response.then().statusCode(200);
        response.prettyPrint();

    }

    @Test
    public void changeNameAndAdditionalNeeds() {
        CreateBookingBody booking = RestAssured
                .given()
                .when()
                .get("/booking/36")
                .as(CreateBookingBody.class);
        booking.setFirstname("test");
        booking.setAdditionalneeds("test");

        Response response = RestAssured
                .given()
                .auth()
                .preemptive()
                .basic("admin", "password123")
                .body(booking)
                .when()
                .put("/booking/36");
        response.prettyPrint();
        response.then().statusCode(200);

    }

    @Test
    public void deleteBooking() {
        Response response = RestAssured.given().log().all()
                .given()
                .auth()
                .preemptive()
                .basic("admin", "password123")
                .delete("/booking/{id}", 594);
        response.prettyPrint();


    }
}
