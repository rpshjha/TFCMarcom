package com.omdb.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SearchResponse {
    @JsonProperty("Search")
    public List<Search> search;
    @JsonProperty("totalResults")
    public String totalResults;
    @JsonProperty("Response")
    public String response;

}
