package com.omdb.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ErrorResponse {

    @JsonProperty("Response")
    private String response;
    @JsonProperty("Error")
    private String error;

}

