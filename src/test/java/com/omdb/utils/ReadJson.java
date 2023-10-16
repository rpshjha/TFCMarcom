package com.omdb.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReadJson {

    private static final String TEST_DATA_FILE = "testdata/data.json";

    private static Map<String, Object> readJsonFromFile(String filepath) {
        String data;
        try {
            data = new String(Objects.requireNonNull(ReadJson.class.getClassLoader().getResourceAsStream(filepath)).readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.readValue(data, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        return String.valueOf(readJsonFromFile(TEST_DATA_FILE).get(key));
    }

    public static Map<String, String> getApiInfo() {
        return (Map<String, String>) readJsonFromFile(TEST_DATA_FILE).get("api-info");
    }

    public static Map<String, String> getRandomMovie() {
        List<Map<String, String>> allMovies = (List<Map<String, String>>) readJsonFromFile(TEST_DATA_FILE).get("movies");

        if (allMovies.isEmpty()) {
            throw new IllegalArgumentException("The list of movies is empty.");
        }

        int randomIndex = (int) (Math.random() * allMovies.size());
        return allMovies.get(randomIndex);
    }

    public static Map<String, String> getMovieByTitle(String movieName) {
        List<Map<String, String>> allMovies = (List<Map<String, String>>) readJsonFromFile(TEST_DATA_FILE).get("movies");

        return allMovies.stream()
                .filter(movie -> movie.get("title").contains(movieName))
                .findAny().get();
    }

}
