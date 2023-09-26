package com.omdb.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Search {


    @JsonProperty("Title")
    public String title;
    @JsonProperty("Year")
    public String year;
    @JsonProperty("imdbID")
    public String imdbID;
    @JsonProperty("Type")
    public String type;
    @JsonProperty("Poster")
    public String poster;

}
