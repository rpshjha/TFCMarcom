package com.omdb.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SearchResponse {
    @JsonProperty("Search")
    public List<Search> search;
    @JsonProperty("totalResults")
    public String totalResults;
    @JsonProperty("Response")
    public String response;

}
