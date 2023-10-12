package com.omdb.tests;

import com.omdb.pojo.ErrorResponse;
import com.omdb.pojo.RootResponse;
import com.omdb.pojo.SearchResponse;
import com.omdb.utils.ReadJson;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;

import static com.omdb.OMDBParams.*;
import static org.hamcrest.CoreMatchers.is;
import static org.testng.Assert.assertNotNull;

@Epic("OMDB API Integration Tests")
@Feature("Verify Basic Search Operations on API")
public class AppTest extends BaseTest {

    @Test(description = "Verify that the OMDB API correctly searches for movies based on both the movie title and release year provided in the search query.")
    @Story("Search by Title and Year")
    @Severity(SeverityLevel.NORMAL)
    @Description("verify the functionality of a search feature that allows users to search movie by Title")
    void shouldSearchByTitleAndYear() {

        RootResponse response = RestAssured.given(requestSpecification)
                .queryParam(getMovieByIdOrTitle, "Oppenheimer")
                .queryParam(yearOfRelease, 2023)
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(RootResponse.class);

        Assert.assertTrue(response.getTitle().contains("Oppen"), "expected response to contain provided title and year");
        Assert.assertTrue(response.getYear().contains("2023"), "expected response to contain provided title and year");
    }

    @Test(description = "Verify that the OMDB API successfully retrieves detailed movie information when an IMDb ID is provided in the search query.")
    @Story("Search by ID")
    @Severity(SeverityLevel.NORMAL)
    @Description("verify the functionality of a search feature that allows users to search movie by OMDB ID")
    void shouldSearchById() {

        RootResponse byIdOrTitle = RestAssured.given(requestSpecification)
                .queryParam(getMovieByIdOrTitle, "Oppenheimer")
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(RootResponse.class);

        String imdbID = byIdOrTitle.getImdbID();
        assertNotNull(imdbID);

        RootResponse bySearch = RestAssured.given(requestSpecification)
                .queryParam(getMovieByaValidIMDbID, imdbID)
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(RootResponse.class);

        Assert.assertEquals(byIdOrTitle.getTitle(), bySearch.getTitle(), "expected search api to contain searched IMDB Id in results");
    }

    @Test(description = "Verify that the OMDB API successfully searches for movies based on a keyword provided in the search query.")
    @Story("Search by keyword")
    @Severity(SeverityLevel.NORMAL)
    @Description("verify the functionality of a search feature that allows users to search movie by Keyword")
    void shouldSearchByKeyword() {

        SearchResponse searchByKeyword = RestAssured.given(requestSpecification)
                .param(searchMovieByTitle, "case")
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(SearchResponse.class);

        boolean containsKeyword = searchByKeyword.getSearch().stream()
                .allMatch(search -> search.getTitle().toLowerCase().contains("case".toLowerCase()));

        Assert.assertTrue(containsKeyword, "expected search api to contain the keyword in result");
    }

    @Test(dataProvider = "getType", description = "Verify that the OMDB API correctly filters movies by type when a type filter is applied.")
    @Story("Filter by Type")
    @Severity(SeverityLevel.NORMAL)
    @Description("verify the functionality of a OMDB API to filter movie title by type")
    void shouldFilterByType(String type) {

        SearchResponse searchByKeyword = RestAssured.given(requestSpecification)
                .queryParam(searchMovieByTitle, "episode")
                .queryParam(typeOfResultToReturn, type)
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(SearchResponse.class);

        boolean containsKeyword = searchByKeyword.getSearch().stream()
                .allMatch(search -> search.getTitle().toLowerCase().contains("episode".toLowerCase()));

        Assert.assertTrue(containsKeyword);

        boolean isOfType = searchByKeyword.getSearch().stream()
                .allMatch(search -> search.getType().toLowerCase().contains(type.toLowerCase()));

        Assert.assertTrue(isOfType, "expected type to be " + type);
    }

    @Test(description = "Verify that the OMDB API provides different search results when users navigate between the first and last pages of search results, demonstrating correct pagination behavior.")
    @Story("Search Pagination")
    @Severity(SeverityLevel.NORMAL)
    @Description("verify the Pagination functionality of a OMDB API")
    void verifyPagination() {

        SearchResponse searchOnFirstPage = RestAssured.given(requestSpecification)
                .queryParam(searchMovieByTitle, "Batman")
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(SearchResponse.class);

        int lastPage = Math.abs(Integer.parseInt(searchOnFirstPage.getTotalResults()) / 10) + 1;

        SearchResponse searchOnLastPage = RestAssured.given(requestSpecification)
                .queryParam(searchMovieByTitle, "Batman")
                .queryParam(pageNumberToReturn, lastPage)
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(SearchResponse.class);

        Assert.assertNotEquals(searchOnLastPage.getSearch().get(0).getImdbID(), searchOnFirstPage.getSearch().get(0).getImdbID(), "expected different content for separate pages");
    }

    @Test(description = "Verify that the OMDB API successfully returns the plot of a movie when the 'plot' parameter is provided in the request.")
    @Story("Should Return Plot Based On Param")
    @Severity(SeverityLevel.NORMAL)
    @Description("verify the functionality of a OMDB API to Return Plot Based On Param")
    void shouldReturnPlotBasedOnParam() {

        RootResponse shortPlot = RestAssured.given(requestSpecification)
                .queryParam(getMovieByIdOrTitle, "Batman")
                .queryParam(returnShortOrFullPlot, "short")
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(RootResponse.class);

        RootResponse fullPlot = RestAssured.given(requestSpecification)
                .queryParam(getMovieByIdOrTitle, "Batman")
                .queryParam(returnShortOrFullPlot, "full")
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(RootResponse.class);

        Assert.assertTrue(fullPlot.getPlot().length() > shortPlot.getPlot().length(), "expected full plot length too be greater than short plot length");
    }

    @Test(description = "Verify that the OMDB API enforces the requirement for an API key and denies access to unauthorized requests without a valid key.")
    @Story("Should Return Error Without API Key")
    @Severity(SeverityLevel.NORMAL)
    @Description("verify that OMDB Api Should Return Error Without API Key")
    public void shouldNotGetResponseWithoutApiKey() {

        ErrorResponse response = RestAssured.given()
                .queryParam("t", "Harry Potter")
                .get(ReadJson.get("base-uri"))
                .then()
                .statusCode(401)
                .extract().response().as(ErrorResponse.class);

        Assert.assertTrue(response.getError().contains("No API key provided."), "expected No API Key Provided error message");
    }

    @Test(description = "Verify that the OMDB API appropriately handles and responds with an error message when an invalid or non-existent search query is submitted.")
    @Story("Should Get Error for invalid Omdb ID")
    @Severity(SeverityLevel.CRITICAL)
    @Description("verify that OMDB Api Should Return Error for a invalid OMDB ID")
    void shouldGetErrorForInvalidSearch() {

        ErrorResponse response = RestAssured.given(requestSpecification)
                .queryParam(getMovieByaValidIMDbID, "tt00000000")
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(ErrorResponse.class);

        Assert.assertTrue(response.getError().contains("Error getting data."), "Error getting data");
    }

    @Test(description = "Verify that the OMDB API returns movie data in compliance with the predefined schema when getting movie by IMDb ID or Title.")
    @Story("Validate Movies by ID or Title API Schema")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the API response conforms to the schema without any validation errors")
    void validateMoviesByIdOrTitleAPISchema() {

        Response response = RestAssured.given(requestSpecification)
                .queryParam(getMovieByIdOrTitle, "Oppenheimer")
                .get()
                .then().spec(responseSpecification)
                .extract().response();

        MatcherAssert.assertThat(response.body().asString(), JsonSchemaValidator.matchesJsonSchema(getResource("moviesByIdOrTitle.json")));
    }

    @Test(description = "Verify that the OMDB API returns movie data in compliance with the predefined schema when searching by Movie Title")
    @Story("Validate Search API Schema")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the API response conforms to the schema without any validation errors")
    void validateSearchAPISchema() {

        Response response = RestAssured.given(requestSpecification)
                .queryParam(searchMovieByTitle, "Oppenheimer")
                .get()
                .then().spec(responseSpecification)
                .extract().response();

        MatcherAssert.assertThat(response.body().asString(), JsonSchemaValidator.matchesJsonSchema(getResource("searchByTitle.json")));
    }

    @Test(description = " Verify that the OMDB API correctly responds with either XML or JSON format based on the 'type' query parameter provided in the request.")
    @Story("validateResponseTypeToBe")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the API can return The data type as JSON and XML both")
    void validateResponseTypeToBe() {

        Arrays.stream(new String[]{"json", "xml"}).forEach(data ->
                {
                    Response response = RestAssured.given(requestSpecification)
                            .queryParam(getMovieByIdOrTitle, "Titanic")
                            .queryParam(theDataTypeToReturn, data)
                            .get()
                            .then().spec(responseSpecification)
                            .extract().response();

                    String actualContentType = response.getContentType().substring(response.getContentType().lastIndexOf("/") + 1, response.getContentType().lastIndexOf(";")).trim();

                    Assert.assertEquals(actualContentType, data, "expected response content type to be " + data);
                }
        );
    }

    @Test(description = "Verify that the OMDB API appropriately handles requests where neither IMDb ID nor a movie title is provided, and returns an error response.")
    @Story("Should Return Error Without A valid IMDb ID or Movie title")
    @Severity(SeverityLevel.TRIVIAL)
    @Description("verify that OMDB Api Should Return Error Without A valid IMDb ID or Movie title")
    public void shouldNotGetResponseWithoutIMDbIDOrMovieTitle() {

        ErrorResponse response = RestAssured.given(requestSpecification)
                .get(ReadJson.get("base-uri"))
                .then()
                .assertThat().statusCode(is(200))
                .spec(responseSpecification)
                .extract().response().as(ErrorResponse.class);

        Assert.assertTrue(response.getError().contains("Incorrect IMDb ID."), "expected Incorrect IMDb ID. error message");
    }

    @DataProvider(name = "getType")
    public Object[][] getType() {
        return new Object[][]{
                {"movie"},
                {"series"},
        };
    }

}