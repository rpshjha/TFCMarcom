package org.omdb.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.omdb.utils.ReadJson;
import org.omdb.utils.RestAssuredRequestFilter;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static com.omdb.OMDBParams.*;
import static com.omdb.ValidOptions.TypeOfResultToReturn.series;

/**
 * Unit test for simple App.
 */
public class AppTest {

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification = null;

    @BeforeTest
    void setup() {
        requestSpecification = RestAssured.given().filter(new RestAssuredRequestFilter());
        requestSpecification.baseUri(ReadJson.get("base-uri"));
        requestSpecification.param("apikey", ReadJson.get("api-key"));

        responseSpecification = RestAssured.expect();
        responseSpecification.contentType(ContentType.JSON);
        responseSpecification.statusCode(200);
        responseSpecification.time(Matchers.lessThan(5000L));
        responseSpecification.statusLine("HTTP/1.1 200 OK");
    }

    @Test
    void searchById() {

        ResponseBody responseBody = RestAssured
                .given(requestSpecification)
                .param(aValidIMDbID, "tt0120338")
                .get()
                .then().spec(responseSpecification)
                .extract().response();

    }

    @Test
    void searchByTitle() {

        ResponseBody responseBody = RestAssured.given(requestSpecification)
                .param(movieTitleToSearchForByTitle, "titanic")
                .get()
                .then().spec(responseSpecification)
                .extract().response();
    }

    @Test
    void searchByKeyword() {

        ResponseBody responseBody = RestAssured
                .given(requestSpecification)
                .param(movieTitleToSearchForByKeyword, "Hannibal")
                .param(typeOfResultToReturn, series)
                .get()
                .then().spec(responseSpecification)
                .extract().response();

    }
}
