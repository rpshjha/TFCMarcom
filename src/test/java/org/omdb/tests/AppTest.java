package org.omdb.tests;

import com.omdb.pojo.RootResponse;
import com.omdb.pojo.SearchResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.omdb.utils.ReadJson;
import org.omdb.utils.RestAssuredRequestFilter;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.InputStream;

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

        InputStream searchByIdSchema = getClass().getClassLoader()
                .getResourceAsStream("searchByIdResponse.json");

        ValidatableResponse validatableResponse = RestAssured
                .given(requestSpecification)
                .param(aValidIMDbID, "tt0120338")
                .get()
                .then().spec(responseSpecification)
                .and().assertThat().body(JsonSchemaValidator.matchesJsonSchema(searchByIdSchema));

        RootResponse response = validatableResponse.and().extract()
                .response().as(RootResponse.class);

        Assert.assertTrue(response.getImdbID().equals("tt0120338"));
    }

    @Test
    void searchByTitle() {

        InputStream searchByIdSchema = getClass().getClassLoader()
                .getResourceAsStream("searchByIdResponse.json");

        ValidatableResponse validatableResponse = RestAssured.given(requestSpecification)
                .param(movieTitleToSearchForByTitle, "We")
                .get()
                .then().spec(responseSpecification)
                .and().assertThat().body(JsonSchemaValidator.matchesJsonSchema(searchByIdSchema));

        RootResponse response = validatableResponse.and().extract()
                .response().as(RootResponse.class);

        Assert.assertTrue(response.getTitle().contains("We"));
    }

    @Test
    void searchByKeyword() {

        InputStream searchByIdSchema = getClass().getClassLoader()
                .getResourceAsStream("searchByKeywordResponse.json");

        ValidatableResponse validatableResponse = RestAssured
                .given(requestSpecification)
                .param(movieTitleToSearchForByKeyword, "Just")
                .param(typeOfResultToReturn, series)
                .get()
                .then().spec(responseSpecification)
                .and().assertThat().body(JsonSchemaValidator.matchesJsonSchema(searchByIdSchema));

        SearchResponse response = validatableResponse.and().extract()
                .response().as(SearchResponse.class);

        Assert.assertTrue(response.getSearch().get(0).getTitle().contains("Just"));

    }
}
