package com.omdb.utils;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import lombok.extern.log4j.Log4j;

@Log4j
public class RALogFilter implements Filter {

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        // Log the request details
        log.info("Rest Assured Request:");
        log.info(requestSpec.getMethod() + " " + requestSpec.getURI());
        log.info("Headers: " + requestSpec.getHeaders());
        log.info("Parameters: " + requestSpec.getQueryParams());
        if (requestSpec.getBody() != null) {
            log.info("Request Body: " + requestSpec.getBody().toString());
        }

        // Proceed with the request
        Response response = ctx.next(requestSpec, responseSpec);

        // Log the response details
        log.info("Rest Assured Response:");
        log.info("Status Code: " + response.getStatusCode());
        log.info("Headers: " + response.getHeaders());
        if (!response.getContentType().contains("image/jpeg"))
            log.info("Response Body: " + response.getBody().prettyPrint());
        return response;
    }
}

