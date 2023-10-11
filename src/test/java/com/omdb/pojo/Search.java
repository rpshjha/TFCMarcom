package com.omdb.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Search {

    @EqualsAndHashCode.Include
    @JsonProperty("Title")
    private String title;
    @JsonProperty("Year")
    private String year;
    @EqualsAndHashCode.Include
    @JsonProperty("imdbID")
    private String imdbID;
    @JsonProperty("Type")
    private String type;
    @JsonProperty("Poster")
    private String poster;

}
