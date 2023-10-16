package com.omdb.tests;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.*;

import static com.omdb.OMDBParams.*;
import static com.omdb.utils.ReadJson.getApiInfo;
import static org.hamcrest.CoreMatchers.is;

public class BaseTest {

    private RequestSpecification requestSpecification;
    private ResponseSpecification responseSpecification;

    @BeforeMethod
    void setup() throws IOException {

        configureRestAssuredLogging();

        configureRequestSpecification();

        configureResponseSpecification();
    }

    @AfterMethod
    void tear() {
        requestSpecification = null;
        responseSpecification = null;
    }

    private void configureRestAssuredLogging() throws FileNotFoundException {
        PrintStream fileOutputStream = new PrintStream("log_" + System.currentTimeMillis() + ".txt");
        RestAssured.filters(new RequestLoggingFilter(fileOutputStream), new ResponseLoggingFilter(fileOutputStream));
    }

    private void configureRequestSpecification() {
        requestSpecification = RestAssured.given();

        requestSpecification.baseUri(getApiInfo().get("base-uri"));
        requestSpecification.param("apikey", getApiInfo().get("api-key"));
    }

    private void configureResponseSpecification() {
        responseSpecification = RestAssured.expect();
        responseSpecification.statusCode(200);
        responseSpecification.time(Matchers.lessThan(5000L));
        responseSpecification.statusLine("HTTP/1.1 200 OK");
    }

    protected InputStream getResource(String name) {
        return getClass().getClassLoader().getResourceAsStream("schema" + File.separator + name);
    }

    protected Response getMovieByTitle(String title) {
        return RestAssured.given(requestSpecification)
                .queryParam(getMovieByTitle, title)
                .get()
                .then()
                .spec(responseSpecification)
                .extract().response();
    }

    protected Response getMovieByTitle(String title, String data) {
        return RestAssured.given(requestSpecification)
                .queryParam(getMovieByTitle, title)
                .queryParam(theDataTypeToReturn, data)
                .get()
                .then()
                .spec(responseSpecification)
                .extract().response();
    }

    protected Response getMovieByTitleAndYear(String title, String year) {
        return RestAssured.given(requestSpecification)
                .queryParam(getMovieByTitle, title)
                .queryParam(yearOfRelease, year)
                .get()
                .then()
                .spec(responseSpecification)
                .extract().response();
    }

    protected Response getMovieByTitleAndPlot(String title, String plot) {
        return RestAssured.given(requestSpecification)
                .queryParam(getMovieByTitle, title)
                .queryParam(returnShortOrFullPlot, plot)
                .get()
                .then()
                .spec(responseSpecification)
                .extract().response();
    }

    protected Response getMovieByIMDBId(String imdbID) {
        return RestAssured.given(requestSpecification)
                .queryParam(getMovieByValidIMDbID, imdbID)
                .get()
                .then()
                .spec(responseSpecification)
                .extract().response();
    }

    protected Response getMovieByIMDBId(String imdbID, String seasonToFilter) {
        return RestAssured.given(requestSpecification)
                .queryParam(getMovieByValidIMDbID, imdbID)
                .queryParam("Season", seasonToFilter)
                .get()
                .then()
                .spec(responseSpecification)
                .extract().response();
    }


    protected Response searchByKeywordAndReturn(String keyword) {
        return RestAssured.given(requestSpecification)
                .queryParam(searchMovieByTitle, keyword)
                .get()
                .then()
                .spec(responseSpecification)
                .extract().response();
    }

    protected Response searchByKeyword(String keyword) {
        return RestAssured.given(requestSpecification)
                .queryParam(searchMovieByTitle, keyword)
                .get()
                .then()
                .spec(responseSpecification)
                .extract().response();
    }

    protected Response searchByKeyword(String keyword, String type) {
        return RestAssured.given(requestSpecification)
                .queryParam(searchMovieByTitle, keyword)
                .queryParam(typeOfResultToReturn, type)
                .get()
                .then()
                .spec(responseSpecification)
                .extract().response();
    }

    protected Response searchByKeyword(String keyword, int pageNumber) {
        return RestAssured.given(requestSpecification)
                .queryParam(searchMovieByTitle, keyword)
                .queryParam(pageNumberToReturn, pageNumber)
                .get()
                .then()
                .spec(responseSpecification)
                .extract().response();
    }

    protected Response sendRequestWithoutApiKey(String title) {
        return RestAssured.given()
                .queryParam(getMovieByTitle, title)
                .get(getApiInfo().get("base-uri"))
                .then()
                .statusCode(401)
                .extract().response();
    }

    protected Response sendEmptyRequest() {
        return RestAssured.given(requestSpecification)
                .get()
                .then()
                .assertThat().statusCode(is(200))
                .spec(responseSpecification)
                .extract().response();
    }

    protected Response getMoviePoster(String imdbID) {
        return RestAssured.given(requestSpecification)
                .queryParam(getMovieByValidIMDbID, imdbID)
                .get(getApiInfo().get("base-uri-poster"))
                .then()
                .spec(responseSpecification)
                .extract().response();
    }
}
