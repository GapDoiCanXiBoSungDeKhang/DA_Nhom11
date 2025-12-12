package org.example.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.example.logic.TrieTree;
import org.example.model.WordEnglish;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class DataLoader {

    private final Map<String, WordEnglish> dictionaryData = new HashMap<>();

    private TrieTree englishTrie = new TrieTree();
    private TrieTree vietnameseTrie = new TrieTree();
    private Map<String, List<String>> vietnameseToEnglishMap = new HashMap<>();

    private final String jsonFileName;

    public DataLoader(String jsonFileName) {
        this.jsonFileName = jsonFileName;
        loadData(jsonFileName);
    }

    /*=========================================================
     *                RELOAD TOÀN BỘ JSON
     =========================================================*/
    public void reloadFromJson() {
        loadFromJson();   // đọc lại file
        buildTries();     // build lại trie + map
    }

    /*=========================================================
     *        >>> ĐỌC JSON TỪ FILE (CHO reloadFromJson) <<<
     =========================================================*/
    private void loadFromJson() {

        dictionaryData.clear();
        vietnameseToEnglishMap.clear();
        englishTrie = new TrieTree();
        vietnameseTrie = new TrieTree();

        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, WordEnglish>>() {}.getType();

        try (Reader reader = new InputStreamReader(
                new FileInputStream("src/main/resources/data/" + jsonFileName),
                StandardCharsets.UTF_8)) {

            Map<String, WordEnglish> temp = gson.fromJson(reader, mapType);

            if (temp != null)
                dictionaryData.putAll(temp);

        } catch (Exception e) {
            System.err.println("Lỗi loadFromJson(): " + e.getMessage());
        }
    }

    /*=========================================================
     *                 BUILD LẠI TRIE + MAP
     =========================================================*/
    public void buildTries() {

        englishTrie = new TrieTree();
        vietnameseTrie = new TrieTree();
        vietnameseToEnglishMap = new HashMap<>();

        for (var entry : dictionaryData.entrySet()) {
            String eng = entry.getKey();
            WordEnglish w = entry.getValue();

            englishTrie.insert(eng);

            String vn = w.getTextVietnamese();
            vietnameseToEnglishMap
                    .computeIfAbsent(vn, k -> new ArrayList<>())
                    .add(eng);

            vietnameseTrie.insert(vn);
        }

        System.out.println("✔ buildTries() hoàn tất — tổng từ: " + dictionaryData.size());
    }

    /*=========================================================
     *          ADD WORD (UPDATE TRIE + MAP)
     =========================================================*/
    public void addWordToTries(String eng, WordEnglish w) {
        dictionaryData.put(eng, w);

        englishTrie.insert(eng);

        String vn = w.getTextVietnamese();
        vietnameseToEnglishMap
                .computeIfAbsent(vn, k -> new ArrayList<>())
                .add(eng);

        vietnameseTrie.insert(vn);
    }

    /*=========================================================
     *       REMOVE WORD (UPDATE TRIE + MAP)
     =========================================================*/
    public void removeWordFromTries(String eng) {

        WordEnglish w = dictionaryData.get(eng);
        if (w == null) return;

        String vn = w.getTextVietnamese();

        // gỡ khỏi map VN → ENG
        if (vietnameseToEnglishMap.containsKey(vn)) {
            vietnameseToEnglishMap.get(vn).remove(eng);

            if (vietnameseToEnglishMap.get(vn).isEmpty())
                vietnameseToEnglishMap.remove(vn);
        }

        dictionaryData.remove(eng);

        // Nếu TrieTree của bạn có delete() thì dùng:
        // englishTrie.delete(eng);
        // vietnameseTrie.delete(vn);

        // Nếu KHÔNG có delete → rebuild toàn bộ
        buildTries();
    }

    /*=========================================================
     *                   LOAD BAN ĐẦU (khởi động app)
     =========================================================*/
    public void loadData(String jsonFile) {

    Gson gson = new Gson();
    Type mapType = new TypeToken<Map<String, WordEnglish>>() {}.getType();

    try (Reader reader = new InputStreamReader(
            new FileInputStream("src/main/resources/" + jsonFile),
            StandardCharsets.UTF_8)) {

        Map<String, WordEnglish> temp = gson.fromJson(reader, mapType);
        if (temp != null) dictionaryData.putAll(temp);

        buildTries();
        System.out.println("✔ Loaded JSON runtime: " + temp.size());
        
    } catch (Exception e) {
        System.err.println("Error loadData(): " + e.getMessage());
    }
}

    /*=========================================================
     *                 SAVE JSON (WordManager)
     =========================================================*/
    public void saveDataToJson(String jsonFile) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream("src/main/resources/data/" + jsonFile),
                StandardCharsets.UTF_8)) {

            gson.toJson(dictionaryData, writer);
            System.out.println("✔ Đã lưu JSON!");

        } catch (Exception e) {
            System.err.println("Lỗi save JSON: " + e.getMessage());
        }
    }

    /*=========================================================
     *                    GETTERS
     =========================================================*/
    public Map<String, WordEnglish> getDictionaryData() {
        return dictionaryData;
    }

    public TrieTree getEnglishTrie() { return englishTrie; }

    public TrieTree getVietnameseTrie() { return vietnameseTrie; }

    public Map<String, List<String>> getVietnameseToEnglishMap() {
        return vietnameseToEnglishMap;
    }
}
