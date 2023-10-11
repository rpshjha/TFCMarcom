package com.omdb.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ErrorResponse {

    @JsonProperty("Response")
    public String response;
    @JsonProperty("Error")
    public String error;

}

