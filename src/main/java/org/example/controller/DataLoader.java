package org.example.controller;

import com.google.gson.Gson;
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
    // 1. Thuộc tính để lưu trữ dữ liệu đã tải
    private final Map<String, WordEnglish> dictionaryData = new HashMap<>();
    private final TrieTree englishTrie = new TrieTree();

    // Tra cứu tiếng Việt ra tiếng anh
    private final Map<String, List<String>> vietnameseToEnglishMap = new HashMap<>();//key là từ tiếng việt còn value là danh sách từ tiếng anh tương ứng(1 từ tiếng việt có thể có nhiều nghĩa tiếng anh)
    private final TrieTree vietnameseTrie = new TrieTree();

    public DataLoader(String jsonFileName)
    {
        loadData(jsonFileName);
    }

    public void loadList(String jsonFileName)
    {
        Gson gson = new Gson();
        // duyệt danh sách cây dị phân theo thứ tự alphabet
    }

    public void loadData(String jsonFileName) {
        Gson gson = new Gson();
        // TypeToken được dùng vì cấu trúc JSON có từ là Key của một Map
        Type mapType = new TypeToken<Map<String, WordEnglish>>() {}.getType();

        try (Reader reader = new InputStreamReader(
                DataLoader.class.getResourceAsStream("/" + jsonFileName), "UTF-8")) {

            // Đọc toàn bộ file JSON vào một Map tạm thời
            Map<String, WordEnglish> tempMap = gson.fromJson(reader, mapType);

            if (tempMap != null) {
                // 2. Chuyển dữ liệu sang HashMap chính và xây dựng Trie
                for (Map.Entry<String, WordEnglish> entry : tempMap.entrySet()) {
                    //Phần Anh-Việt
                    String englishWord = entry.getKey();
                    WordEnglish wordDetails = entry.getValue();

                    // Lưu vào HashMap (Để tra cứu chi tiết O(1))
                    this.dictionaryData.put(englishWord, wordDetails);

                    // Chèn từ tiếng Anh vào cây Trie (Để tra cứu tiền tố O(L))
                    this.englishTrie.insert(englishWord);

                    //Phần Việt-Anh
                    String vietnameseMeaning = wordDetails.getTextVietnamese();

                    // Nếu nghĩa tiếng Việt này chưa có trong Map thì tạo ra 1 cặp key: từ tiếng việt, value: danh sách liên quan tiếng anh tương ứng(type, ex...)
                    if(!this.vietnameseToEnglishMap.containsKey(vietnameseMeaning))
                    {
                        this.vietnameseToEnglishMap.put(vietnameseMeaning, new ArrayList<>());
                    }

                    // Thêm từ tiếng Anh hiện tại vào danh sách
                    this.vietnameseToEnglishMap.get(vietnameseMeaning).add(englishWord);

                    // Chèn nghĩa tiếng Việt vào cây Trie tiếng Việt (Để tra cứu tiền tố O(L))
                    this.vietnameseTrie.insert(vietnameseMeaning);


                }
                System.out.println("Tải dữ liệu thành công! Số lượng từ: " + dictionaryData.size());//Thông báo để test
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi tải dữ liệu từ file JSON: " + jsonFileName);//Thông báo để test
            e.printStackTrace();
        }
    }

    /**
     * Lưu dữ liệu hiện tại từ dictionaryData vào file JSON
     */
    public void saveDataToJson(String jsonFileName) {
        Gson gson = new Gson();
        try (Writer writer = new OutputStreamWriter(
                new FileOutputStream("src/main/resources/" + jsonFileName), StandardCharsets.UTF_8)) {

            gson.toJson(this.dictionaryData, writer);
            System.out.println("✅ Đã lưu dữ liệu vào file JSON!");

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lưu file JSON: " + e.getMessage());
        }
    }

    public Map<String, WordEnglish> getDictionaryData()
    {
        return this.dictionaryData;
    }

    // Trả về đối tượng Trie cho việc tìm kiếm gợi ý nhanh (O(L))
    public TrieTree getEnglishTrie()
    {
        return this.englishTrie;
    }

    public Map<String, List<String>> getVietnameseToEnglishMap()
    {
        return this.vietnameseToEnglishMap;
    }

    public TrieTree getVietnameseTrie()
    {
        return this.vietnameseTrie;
    }

}
