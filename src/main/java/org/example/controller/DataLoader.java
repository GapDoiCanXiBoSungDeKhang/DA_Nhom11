package org.example.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.example.logic.TrieTree;
import org.example.model.WordEnglish;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DataLoader {

    private final Map<String, WordEnglish> dictionaryData = new HashMap<>();
    private final TrieTree englishTrie = new TrieTree();

    // Map tiếng Việt → danh sách tiếng Anh
    private final Map<String, List<String>> vietnameseToEnglishMap = new HashMap<>();
    private final TrieTree vietnameseTrie = new TrieTree();

    public DataLoader(String jsonFileName) {
        loadData(jsonFileName);
    }

    public void loadData(String jsonFileName) {
        Gson gson = new Gson();
        Type mapType = new TypeToken<Map<String, WordEnglish>>() {}.getType();

        try (InputStream inputStream =
                     getClass().getClassLoader().getResourceAsStream(jsonFileName)) {

            //Sửa lỗi: không dùng "data/" nữa
            if (inputStream == null) {
                throw new FileNotFoundException("Không tìm thấy file trong resources: " + jsonFileName);
            }

            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

            Map<String, WordEnglish> tempMap = gson.fromJson(reader, mapType);

            if (tempMap != null) {
                for (Map.Entry<String, WordEnglish> entry : tempMap.entrySet()) {

                    String englishWord = entry.getKey();
                    WordEnglish wordDetails = entry.getValue();

                    dictionaryData.put(englishWord, wordDetails);
                    englishTrie.insert(englishWord);

                    String vn = wordDetails.getTextVietnamese();
                    vietnameseToEnglishMap
                            .computeIfAbsent(vn, k -> new ArrayList<>())
                            .add(englishWord);

                    vietnameseTrie.insert(vn);
                }

                System.out.println("Tải dữ liệu thành công! Số lượng từ: " + dictionaryData.size());
            }

        } catch (Exception e) {
            System.err.println("Lỗi khi tải dữ liệu JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void saveDataToJson(String jsonFileName) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()     //bật pretty print. Gson sẽ tự động căng lề, xuống dòng, thụt đầu dòng như file gốc
                .disableHtmlEscaping()    //Tắt việc “escape” các ký tự đặc biệt như /, <, >… → giữ nguyên phiên âm /ˈæp.əl/như cũ.
                .create();
        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream("src/main/resources/data/" + jsonFileName), StandardCharsets.UTF_8)) {

            gson.toJson(this.dictionaryData, writer);
            System.out.println("Đã lưu dữ liệu vào file JSON!");

        } catch (Exception e) {
            System.err.println("Lỗi khi lưu file JSON: " + e.getMessage());
        }
    }

    public Map<String, WordEnglish> getDictionaryData() {
        return this.dictionaryData;
    }

    // duyệt theo tiêu chí tìm kiếm các từ đã lưu
    public List<WordEnglish> loadDataFavourite(Map<String, WordEnglish> words) {
        List<WordEnglish> favourites = new ArrayList<>();

        for (WordEnglish w : words.values()) {
            if (w.isFavourite()) {
                favourites.add(w);
            }
        }

        return favourites;
    }


    public TrieTree getEnglishTrie() {
        return this.englishTrie;
    }

    public Map<String, List<String>> getVietnameseToEnglishMap() {
        return this.vietnameseToEnglishMap;
    }

    public TrieTree getVietnameseTrie() {
        return this.vietnameseTrie;
    }
}
