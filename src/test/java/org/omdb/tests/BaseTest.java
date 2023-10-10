package org.omdb.tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.omdb.utils.ReadJson;
import org.omdb.utils.RestAssuredRequestFilter;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.InputStream;

public class BaseTest {

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

    protected InputStream getResource(String name) {
        InputStream searchByIdSchema = getClass().getClassLoader()
                .getResourceAsStream("schema" + File.separator + name);
        assert searchByIdSchema != null;
        return searchByIdSchema;
    }
}
