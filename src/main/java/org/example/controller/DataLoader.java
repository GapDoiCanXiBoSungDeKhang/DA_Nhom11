package org.example.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.model.WordEnglish;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Map;

public class DataLoader {
    public static Map<String, WordEnglish> loadDictionary() {
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, WordEnglish>>() {}.getType();

        try (Reader reader = new InputStreamReader(
                DataLoader.class.getResourceAsStream("/Vietnamese_english.json"), "UTF-8")) {
            return gson.fromJson(reader, mapType);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of();
        }
    }
}
