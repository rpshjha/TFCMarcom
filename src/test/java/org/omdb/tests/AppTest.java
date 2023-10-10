package org.omdb.tests;

import com.omdb.pojo.RootResponse;
import com.omdb.pojo.SearchResponse;
import io.restassured.RestAssured;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.ValidatableResponse;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.omdb.OMDBParams.*;
import static com.omdb.ValidOptions.TypeOfResultToReturn.movie;
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
                .param(typeOfResultToReturn, movie)
                .get()
                .then().spec(responseSpecification)
                .extract().response().as(SearchResponse.class);

        boolean containsKeyword = searchByKeyword.getSearch().stream()
                .allMatch(search -> search.getTitle().toLowerCase().contains("case".toLowerCase()));

        Assert.assertTrue(containsKeyword);

    }
}
