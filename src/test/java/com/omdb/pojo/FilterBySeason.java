package com.omdb.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.List;

@Getter
public class FilterBySeason {

    @JsonProperty("Title")
    private String title;
    @JsonProperty("Season")
    private String season;
    @JsonProperty("totalSeasons")
    private String totalSeasons;
    @JsonProperty("Episodes")
    private List<Episode> episodes;
    @JsonProperty("Response")
    private String response;

}