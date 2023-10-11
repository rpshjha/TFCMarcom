package org.omdb.tests;

import com.omdb.pojo.ErrorResponse;
import com.omdb.pojo.RootResponse;
import com.omdb.pojo.SearchResponse;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.ValidatableResponse;
import org.omdb.utils.ReadJson;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.omdb.OMDBParams.*;
import static org.testng.Assert.assertNotNull;

/**
 * Unit test for simple App.
 */
public class AppTest extends BaseTest {

    @Test
    void shouldSearchByTitleAndYear() {

        ValidatableResponse validatableResponse = RestAssured.given(requestSpecification)
                .queryParam(movieTitleToSearchForByTitle, "Oppenheimer")
                .queryParam(yearOfRelease, 2023)
                .get()
                .then().spec(responseSpecification)
                .and()
                .body(JsonSchemaValidator.matchesJsonSchema(getResource("searchByIdResponse.json")));

        RootResponse response = validatableResponse
                .extract()
                .response()
                .as(RootResponse.class);

        Assert.assertTrue(response.getTitle().contains("Oppen"));
        Assert.assertTrue(response.getYear().contains("2023"));
    }

    @Test
    void shouldSearchById() {

        RootResponse byIdOrTitle = RestAssured.given(requestSpecification)
                .queryParam(movieTitleToSearchForByTitle, "Oppenheimer")
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(RootResponse.class);

        String imdbID = byIdOrTitle.getImdbID();
        assertNotNull(imdbID);

        RootResponse bySearch = RestAssured.given(requestSpecification)
                .queryParam(aValidIMDbID, imdbID)
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(RootResponse.class);

        Assert.assertEquals(byIdOrTitle.getTitle(), bySearch.getTitle());
    }

    @Test
    void shouldSearchByKeyword() {

        SearchResponse searchByKeyword = RestAssured.given(requestSpecification)
                .param(movieTitleToSearchForByKeyword, "case")
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(SearchResponse.class);

        boolean containsKeyword = searchByKeyword.getSearch().stream()
                .allMatch(search -> search.getTitle().toLowerCase().contains("case".toLowerCase()));

        Assert.assertTrue(containsKeyword);
    }

    @Test(dataProvider = "getType")
    void shouldFilterByType(String type) {

        SearchResponse searchByKeyword = RestAssured.given(requestSpecification)
                .queryParam(movieTitleToSearchForByKeyword, "episode")
                .queryParam(typeOfResultToReturn, type)
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(SearchResponse.class);

        boolean containsKeyword = searchByKeyword.getSearch().stream()
                .allMatch(search -> search.getTitle().toLowerCase().contains("episode".toLowerCase()));

        Assert.assertTrue(containsKeyword);

        boolean isOfType = searchByKeyword.getSearch().stream()
                .allMatch(search -> search.getType().toLowerCase().contains(type.toLowerCase()));

        Assert.assertTrue(isOfType);
    }

    @Test(description = "verify that search result should be different on first and last page")
    void verifyPagination() {

        SearchResponse searchOnFirstPage = RestAssured.given(requestSpecification)
                .queryParam(movieTitleToSearchForByKeyword, "Batman")
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(SearchResponse.class);

        int lastPage = Math.abs(Integer.parseInt(searchOnFirstPage.getTotalResults()) / 10) + 1;

        SearchResponse searchOnLastPage = RestAssured.given(requestSpecification)
                .queryParam(movieTitleToSearchForByKeyword, "Batman")
                .queryParam(pageNumberToReturn, lastPage)
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(SearchResponse.class);

        Assert.assertNotEquals(searchOnLastPage.getSearch().get(0).getImdbID(), searchOnFirstPage.getSearch().get(0).getImdbID());
    }

    @Test
    void shouldReturnPlotBasedOnParam() {

        RootResponse shortPlot = RestAssured.given(requestSpecification)
                .queryParam(movieTitleToSearchForByTitle, "Batman")
                .queryParam(returnShortOrFullPlot, "short")
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(RootResponse.class);

        RootResponse fullPlot = RestAssured.given(requestSpecification)
                .queryParam(movieTitleToSearchForByTitle, "Batman")
                .queryParam(returnShortOrFullPlot, "full")
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(RootResponse.class);

        Assert.assertTrue(fullPlot.getPlot().length() > shortPlot.getPlot().length());
    }

    @Test
    public void shouldNotGetResponseWithoutApiKey() {

        ErrorResponse response = RestAssured.given()
                .queryParam("t", "Harry Potter")
                .get(ReadJson.get("base-uri"))
                .then()
                .statusCode(401)
                .extract().response().as(ErrorResponse.class);

        Assert.assertTrue(response.getError().contains("No API key provided."));
    }

    @Test
    void shouldGetErrorForInvalidSearch() {

        ErrorResponse response = RestAssured.given(requestSpecification)
                .queryParam(aValidIMDbID, "tt00000000")
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(ErrorResponse.class);

        Assert.assertTrue(response.getError().contains("Error getting data."), "Error getting data");
    }

    @DataProvider(name = "getType")
    public Object[][] getType() {
        return new Object[][]{
                {"movie"},
                {"series"},
        };
    }

}
