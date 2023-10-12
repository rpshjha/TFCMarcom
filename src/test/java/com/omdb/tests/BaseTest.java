package com.omdb.tests;

import com.omdb.utils.ReadJson;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.testng.annotations.BeforeTest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.PrintStream;

public class BaseTest {

    protected RequestSpecification requestSpecification;
    protected ResponseSpecification responseSpecification = null;

    @BeforeTest
    void setup() throws FileNotFoundException {

        PrintStream fileOutPutStream = new PrintStream("log/log_" + System.currentTimeMillis() + ".txt");
        RestAssured.filters(new RequestLoggingFilter(fileOutPutStream), new ResponseLoggingFilter(fileOutPutStream));

        requestSpecification = RestAssured.given();
        requestSpecification.baseUri(ReadJson.get("base-uri"));
        requestSpecification.param("apikey", ReadJson.get("api-key"));

        responseSpecification = RestAssured.expect();
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
