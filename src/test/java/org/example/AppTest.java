package org.example;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.example.utils.ReadJson;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Unit test for simple App.
 */
public class AppTest {

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification = null;

    public PrintStream log;
    RequestLoggingFilter requestLoggingFilter;
    ResponseLoggingFilter responseLoggingFilter;

    @BeforeTest
    void setup() {
        requestSpecification = RestAssured.given();
        requestSpecification.baseUri(ReadJson.get("base-uri"));
        requestSpecification.param("apikey", ReadJson.get("api-key"));

        try {
            log = new PrintStream(new FileOutputStream("test_logging.txt"), true);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        requestLoggingFilter = new RequestLoggingFilter(log);
        responseLoggingFilter = new ResponseLoggingFilter(log);

        responseSpecification = RestAssured.expect();
        responseSpecification.contentType(ContentType.JSON);
        responseSpecification.statusCode(200);
        responseSpecification.time(Matchers.lessThan(5000L));
        responseSpecification.statusLine("HTTP/1.1 200 OK");
    }

    @Test
    void searchByTitle() {

        ResponseBody responseBody = RestAssured.given(requestSpecification)
                .param("t", "titanic")
                .get()
                .then().spec(responseSpecification)
                .extract().response();
    }

    @Test
    void searchById() {

        ResponseBody responseBody = RestAssured
                .given(requestSpecification)
                .param("i", "tt0120338")
                .get()
                .then().spec(responseSpecification)
                .extract().response();

    }
}
