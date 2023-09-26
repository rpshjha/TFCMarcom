package com.omdb.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Rating {

    @JsonProperty("Source")
    public String source;
    @JsonProperty("Value")
    public String value;
}
