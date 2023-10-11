package org.omdb.tests;

import com.omdb.pojo.ErrorResponse;
import com.omdb.pojo.RootResponse;
import com.omdb.pojo.SearchResponse;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.ValidatableResponse;
import org.omdb.utils.ReadJson;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.omdb.OMDBParams.*;
import static org.testng.Assert.assertNotNull;

@Epic("OMDB API Test")
@Feature("Verify Basic Search Operations on API")
public class AppTest extends BaseTest {

    @Test(description = "shouldSearchByTitleAndYear")
    @Story("Search by Title and Year")
    @Severity(SeverityLevel.NORMAL)
    @Description("verify the functionality of a search feature that allows users to search movie by Title")
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
    @Story("Search by ID")
    @Severity(SeverityLevel.NORMAL)
    @Description("verify the functionality of a search feature that allows users to search movie by OMDB ID")
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
    @Story("Search by keyword")
    @Severity(SeverityLevel.NORMAL)
    @Description("verify the functionality of a search feature that allows users to search movie by Keyword")
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
    @Story("Filter by Type")
    @Severity(SeverityLevel.NORMAL)
    @Description("verify the functionality of a OMDB API to filter movie title by type")
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
    @Story("Search Pagination")
    @Severity(SeverityLevel.NORMAL)
    @Description("verify the Pagination functionality of a OMDB API")
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
    @Story("Should Return Plot Based On Param")
    @Severity(SeverityLevel.NORMAL)
    @Description("verify the functionality of a OMDB API to Return Plot Based On Param")
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

        Assert.assertTrue(response.getError().contains("No API key provided."));
    }

    @Test
    @Story("Should Get Error for invalid Omdb ID")
    @Severity(SeverityLevel.NORMAL)
    @Description("verify that OMDB Api Should Return Error for a invalid OMDB ID")
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
