package com.omdb.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class ReadJson {

    public static String get(String key) {

        Map<String, String> objectMap;
        try {
            String data = new String(Objects.requireNonNull(ReadJson.class.getClassLoader().getResourceAsStream("data.json")).readAllBytes());
            ObjectMapper mapper = new ObjectMapper();

            objectMap = mapper.readValue(data, Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return String.valueOf(objectMap.get(key));
    }
}
