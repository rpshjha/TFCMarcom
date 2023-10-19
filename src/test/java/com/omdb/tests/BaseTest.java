package com.omdb.tests;

import com.omdb.utils.RALogFilter;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import lombok.extern.log4j.Log4j;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;

import java.io.File;
import java.io.InputStream;

import static com.omdb.OMDBParams.*;
import static com.omdb.utils.ReadJson.getApiInfo;
import static org.hamcrest.CoreMatchers.is;

@Log4j
@Listeners(com.omdb.listeners.TestListener.class)
public class BaseTest {

    private final ThreadLocal<RequestSpecification> requestSpecification = new ThreadLocal<>();
    private final ThreadLocal<ResponseSpecification> responseSpecification = new ThreadLocal<>();

    @BeforeMethod
    void setup() {
        configureRequestSpecification();
        configureResponseSpecification();
    }

    @AfterMethod
    void tear() {
        requestSpecification.remove();
        responseSpecification.remove();
    }

//    private void configureRestAssuredLogging() throws FileNotFoundException {
//        PrintStream fileOutputStream = new PrintStream("log_" + System.currentTimeMillis() + ".txt");
//        RestAssured.filters(new RequestLoggingFilter(fileOutputStream), new ResponseLoggingFilter(fileOutputStream));
//        RestAssured.filters(new RestAssuredLogFilter());
//    }

    private void configureRequestSpecification() {
        requestSpecification.set(RestAssured.given());

        requestSpecification.get().filter(new RALogFilter());
        requestSpecification.get().baseUri(getApiInfo().get("base-uri"));
        requestSpecification.get().param("apikey", getApiInfo().get("api-key"));
    }

    private void configureResponseSpecification() {
        responseSpecification.set(RestAssured.expect());
        responseSpecification.get().statusCode(200);
        responseSpecification.get().time(Matchers.lessThan(5000L));
        responseSpecification.get().statusLine("HTTP/1.1 200 OK");
    }

    protected InputStream getResource(String name) {
        return getClass().getClassLoader().getResourceAsStream("schema" + File.separator + name);
    }

    protected Response getMovieByTitle(String title) {
        return RestAssured.given(requestSpecification.get())
                .queryParam(getMovieByTitle, title)
                .get()
                .then()
                .spec(responseSpecification.get())
                .extract().response();
    }

    protected Response getMovieByTitle(String title, String data) {
        return RestAssured.given(requestSpecification.get())
                .queryParam(getMovieByTitle, title)
                .queryParam(theDataTypeToReturn, data)
                .get()
                .then()
                .spec(responseSpecification.get())
                .extract().response();
    }

    protected Response getMovieByTitleAndYear(String title, String year) {
        return RestAssured.given(requestSpecification.get())
                .queryParam(getMovieByTitle, title)
                .queryParam(yearOfRelease, year)
                .get()
                .then()
                .spec(responseSpecification.get())
                .extract().response();
    }

    protected Response getMovieByTitleAndPlot(String title, String plot) {
        return RestAssured.given(requestSpecification.get())
                .queryParam(getMovieByTitle, title)
                .queryParam(returnShortOrFullPlot, plot)
                .get()
                .then()
                .spec(responseSpecification.get())
                .extract().response();
    }

    protected Response getMovieByIMDBId(String imdbID) {
        return RestAssured.given(requestSpecification.get())
                .queryParam(getMovieByValidIMDbID, imdbID)
                .get()
                .then()
                .spec(responseSpecification.get())
                .extract().response();
    }

    protected Response getMovieByIMDBId(String imdbID, String seasonToFilter) {
        return RestAssured.given(requestSpecification.get())
                .queryParam(getMovieByValidIMDbID, imdbID)
                .queryParam("Season", seasonToFilter)
                .get()
                .then()
                .spec(responseSpecification.get())
                .extract().response();
    }


    protected Response searchByKeywordAndReturn(String keyword) {
        return RestAssured.given(requestSpecification.get())
                .queryParam(searchMovieByTitle, keyword)
                .get()
                .then()
                .spec(responseSpecification.get())
                .extract().response();
    }

    protected Response searchByKeyword(String keyword) {
        return RestAssured.given(requestSpecification.get())
                .queryParam(searchMovieByTitle, keyword)
                .get()
                .then()
                .spec(responseSpecification.get())
                .extract().response();
    }

    protected Response searchByKeyword(String keyword, String type) {
        return RestAssured.given(requestSpecification.get())
                .queryParam(searchMovieByTitle, keyword)
                .queryParam(typeOfResultToReturn, type)
                .get()
                .then()
                .spec(responseSpecification.get())
                .extract().response();
    }

    protected Response searchByKeyword(String keyword, int pageNumber) {
        return RestAssured.given(requestSpecification.get())
                .queryParam(searchMovieByTitle, keyword)
                .queryParam(pageNumberToReturn, pageNumber)
                .get()
                .then()
                .spec(responseSpecification.get())
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
        return RestAssured.given(requestSpecification.get())
                .get()
                .then()
                .assertThat().statusCode(is(200))
                .spec(responseSpecification.get())
                .extract().response();
    }

    protected Response getMoviePoster(String imdbID) {
        return RestAssured.given(requestSpecification.get())
                .queryParam(getMovieByValidIMDbID, imdbID)
                .get(getApiInfo().get("base-uri-poster"))
                .then()
                .spec(responseSpecification.get())
                .extract().response();
    }
}
