package org.example;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RestAssuredRequestFilter implements Filter {

    private static final Log log = LogFactory.getLog(RestAssuredRequestFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification filterableRequestSpecification, FilterableResponseSpecification filterableResponseSpecification, FilterContext filterContext) {
        Response response = filterContext.next(filterableRequestSpecification, filterableResponseSpecification);
        if (response.statusCode() != 200) {
            log.error(filterableRequestSpecification.getMethod() + " " + filterableRequestSpecification.getURI() + " => " +
                    response.getStatusCode() + " " + response.getStatusLine());
        }
        log.info(filterableRequestSpecification.getMethod() + " " + filterableRequestSpecification.getURI() + " \n Request Body =>" + filterableRequestSpecification.getBody() + "\n Response Status => " +
                response.getStatusCode() + " " + response.getStatusLine() + " \n Response Body => " + response.getBody().prettyPrint());
        return response;
    }
}
