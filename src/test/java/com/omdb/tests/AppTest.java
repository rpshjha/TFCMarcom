package com.omdb.tests;

import com.omdb.pojo.ErrorResponse;
import com.omdb.pojo.FilterBySeason;
import com.omdb.pojo.GetMovieByIdOrTitle;
import com.omdb.pojo.SearchResponse;
import io.qameta.allure.*;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

import static com.omdb.utils.ReadJson.getRandomMovie;
import static org.testng.Assert.assertNotNull;

@Epic("Enhance OMDB API Usability and Reliability")
@Feature("OMDB API Functionality Verification")
public class AppTest extends BaseTest {

    @Test(groups = "regression", description = "Verify that the OMDB API correctly searches for movies based on both the movie title and release year provided in the search query.")
    @Story("Search Functionality Verification")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the functionality of a search feature that allows users to search movies by Title")
    void verifyMovieDetailsByTitleAndYear() {

        Map<String, String> movie = getRandomMovie();

        GetMovieByIdOrTitle response = getMovieByTitleAndYear(movie.get("title"), movie.get("year")).as(GetMovieByIdOrTitle.class);

        Assert.assertEquals(response.getTitle(), movie.get("title"), "expected response to contain provided title and year");
        Assert.assertEquals(response.getYear(), movie.get("year"), "expected response to contain provided title and year");
    }

    @Test(groups = "sanity", description = "Verify that the OMDB API successfully retrieves detailed movie information when an IMDb ID is provided in the search query.")
    @Story("Search Functionality Verification")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the functionality of a search feature that allows users to search movie by OMDB ID")
    void verifyMovieDetailsByIMDBId() {

        Map<String, String> movie = getRandomMovie();

        GetMovieByIdOrTitle byIdOrTitle = getMovieByTitle(movie.get("title")).as(GetMovieByIdOrTitle.class);

        String imdbID = byIdOrTitle.getImdbID();
        assertNotNull(imdbID);

        GetMovieByIdOrTitle bySearch = getMovieByIMDBId(imdbID).as(GetMovieByIdOrTitle.class);

        Assert.assertEquals(byIdOrTitle.getTitle(), bySearch.getTitle(), "expected OMDB api to contain searched IMDB Id in results");
    }

    @Test(groups = "sanity", description = "Verify that the OMDB API successfully searches for movies based on a keyword provided in the search query.")
    @Story("Search Functionality Verification")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the functionality of a search feature that allows users to search movies by Keyword")
    void verifyMovieDetailsBySearchKeyword() {

        SearchResponse searchByKeyword = searchByKeyword("case").as(SearchResponse.class);

        boolean containsKeyword = searchByKeyword.getSearch().stream()
                .allMatch(search -> search.getTitle().toLowerCase().contains("case".toLowerCase()));

        Assert.assertTrue(containsKeyword, "expected search api to contain the keyword in result");
    }

    @Test(groups = "sanity", dataProvider = "getType", description = "Verify that the OMDB API correctly filters movies by type when a type filter is applied.")
    @Story("Filtering and Pagination")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the functionality of a OMDB API to filter movie title or by type")
    void verifyMovieDetailsShouldFilterByType(String type) {

        SearchResponse searchByKeyword = searchByKeyword("episode", type).as(SearchResponse.class);

        boolean containsKeyword = searchByKeyword.getSearch().stream()
                .allMatch(search -> search.getTitle().toLowerCase().contains("episode".toLowerCase()));

        Assert.assertTrue(containsKeyword);

        boolean isOfType = searchByKeyword.getSearch().stream()
                .allMatch(search -> search.getType().toLowerCase().contains(type.toLowerCase()));

        Assert.assertTrue(isOfType, "expected type to be " + type);
    }

    @Test(groups = "sanity", description = "Verify that the OMDB API provides different search results when users navigate between the first and last pages of search results, demonstrating correct pagination behavior.")
    @Story("Filtering and Pagination")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the Pagination functionality of a OMDB API")
    void shouldShowDifferentResultsOnDifferentSearchResultPages() {

        SearchResponse searchOnFirstPage = searchByKeyword("case").as(SearchResponse.class);

        int lastPage = Math.abs(Integer.parseInt(searchOnFirstPage.getTotalResults()) / 10) + 1;

        SearchResponse searchOnLastPage = searchByKeyword("case", lastPage).as(SearchResponse.class);

        Assert.assertNotEquals(searchOnLastPage.getSearch().get(0).getImdbID(), searchOnFirstPage.getSearch().get(0).getImdbID(), "expected different content for separate pages");
    }

    @Test(groups = "sanity", description = "Verify that the OMDB API successfully returns the plot of a movie when the 'plot' parameter is provided in the request.")
    @Story("TV Series Season Information Retrieval")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the functionality of a OMDB API to Return Plot Based On Param")
    void shouldReturnPlotBasedOnParam() {

        String movie = "Batman";

        GetMovieByIdOrTitle shortPlot = getMovieByTitleAndPlot(movie, "short").as(GetMovieByIdOrTitle.class);

        GetMovieByIdOrTitle fullPlot = getMovieByTitleAndPlot(movie, "full").as(GetMovieByIdOrTitle.class);

        Assert.assertTrue(fullPlot.getPlot().length() > shortPlot.getPlot().length(), "expected full plot length too be greater than short plot length");
    }

    @Test(groups = "sanity", description = "Verify that the OMDB API enforces the requirement for an API key and denies access to unauthorized requests without a valid key.")
    @Story("Error Handling and Schema Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that OMDB Api Should Return Error Without API Key")
    public void shouldNotGetResponseWithoutApiKey() {

        Map<String, String> movie = getRandomMovie();

        ErrorResponse response = sendRequestWithoutApiKey(movie.get("title")).as(ErrorResponse.class);

        Assert.assertTrue(response.getError().contains("No API key provided."), "expected No API Key Provided error message");
    }

    @Test(groups = "sanity", description = "Verify that the OMDB API appropriately handles and responds with an error message when an invalid or non-existent search query is submitted.")
    @Story("Error Handling and Schema Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verify that OMDB Api Should Return Error for a invalid OMDB ID")
    void shouldGetErrorForInvalidSearch() {

        ErrorResponse response = getMovieByIMDBId("tt00000000").as(ErrorResponse.class);

        Assert.assertTrue(response.getError().contains("Error getting data."), "Error getting data");
    }

    @Test(groups = "sanity", description = "Verify that the OMDB API returns movie data in compliance with the predefined schema when getting movie by IMDb ID or Title.")
    @Story("Error Handling and Schema Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the API response conforms to the schema without any validation errors")
    void validateMoviesByIdOrTitleAPISchema() {

        Response response = getMovieByTitle("Batman");

        MatcherAssert.assertThat(response.body().asString(), JsonSchemaValidator.matchesJsonSchema(getResource("moviesByIdOrTitle.json")));
    }

    @Test(groups = "sanity", description = "Verify that the OMDB API returns movie data in compliance with the predefined schema when searching by Movie Title")
    @Story("Error Handling and Schema Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the API response conforms to the schema without any validation errors")
    void validateSearchAPISchema() {

        Response response = searchByKeywordAndReturn("Open");

        MatcherAssert.assertThat(response.body().asString(), JsonSchemaValidator.matchesJsonSchema(getResource("searchByTitle.json")));
    }

    @Test(groups = "sanity", description = " Verify that the OMDB API correctly responds with either XML or JSON format based on the 'type' query parameter provided in the request.")
    @Story("Filtering and Pagination")
    @Severity(SeverityLevel.MINOR)
    @Description("Verify that the API can return The data type as JSON and XML both")
    void validateResponseTypeToBe() {

        Arrays.stream(new String[]{"json", "xml"}).forEach(data ->
                {
                    Response response = getMovieByTitle("Titanic", data);

                    String actualContentType = response.getContentType().substring(response.getContentType().lastIndexOf("/") + 1, response.getContentType().lastIndexOf(";")).trim();

                    Assert.assertEquals(actualContentType, data, "expected response content type to be " + data);
                }
        );
    }

    @Test(groups = "sanity", description = "Verify that the OMDB API appropriately handles requests where neither IMDb ID nor a movie title is provided, and returns an error response.")
    @Story("Error Handling and Schema Validation")
    @Severity(SeverityLevel.TRIVIAL)
    @Description("Verify that OMDB Api Should Return Error Without A valid IMDb ID or Movie title")
    public void shouldNotGetResponseWithoutIMDbIDOrMovieTitle() {

        ErrorResponse response = sendEmptyRequest().as(ErrorResponse.class);

        Assert.assertTrue(response.getError().contains("Incorrect IMDb ID."), "expected Incorrect IMDb ID. error message");
    }

    @Test(groups = "sanity", description = "Verify that the OMDB API successfully retrieves information about a specific season of a TV series.")
    @Story("TV Series Season Information Retrieval")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the OMDB API successfully retrieves information about a specific season of a TV series.")
    public void shouldFilterResultsBasedOnSpecificSeason() {

        GetMovieByIdOrTitle getMovieByIdOrTitle = getMovieByIMDBId("tt0944947").as(GetMovieByIdOrTitle.class);

        String totalSeason = getMovieByIdOrTitle.getTotalSeasons();
        String seasonToFilter = String.valueOf(new Random().nextInt(1, Integer.parseInt(totalSeason)));

        FilterBySeason filterBySeason = getMovieByIMDBId("tt0944947", seasonToFilter).as(FilterBySeason.class);

        Assert.assertEquals(filterBySeason.getSeason(), seasonToFilter, "expected filtered season in the response");
    }

    @Test(groups = "sanity", description = "Verify that the Poster API returns Poster")
    @Story("Poster API")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify that the Poster API returns Poster for a valid Movie")
    public void shouldReturnMoviePosterForAValidMovie() {

        Response response = getMoviePoster("tt0944947");

        Assert.assertTrue(response.contentType().contains("image/jpeg"), "expected content type as image/jpeg");
    }

    @DataProvider(name = "getType")
    public Object[][] getType() {
        return new Object[][]{
                {"movie"},
                {"series"},
        };
    }

}
