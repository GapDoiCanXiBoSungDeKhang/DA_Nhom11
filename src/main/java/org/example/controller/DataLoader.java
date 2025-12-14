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
    // Thuộc tính để lưu trữ dữ liệu đã tải
    private final Map<String, WordEnglish> dictionaryData = new HashMap<>(); // HashMap tra cứu từ tiếng Anh sang tiếng Việt ( Hashmap chính)

    private TrieTree englishTrie = new TrieTree();// Tạo cây tiền tố gợi ý từ tiếng Anh
    private TrieTree vietnameseTrie = new TrieTree();// Tạo cây tiền tố gợi ý từ tiếng Việt
    private Map<String, List<String>> vietnameseToEnglishMap = new HashMap<>(); // HashMap tra cứu ngược từ tiếng Việt sang Tiếng Anh

    private final String jsonFileName;

    public DataLoader(String jsonFileName) {
        this.jsonFileName = jsonFileName;
        loadData(jsonFileName);
    }


    // LOAD BAN ĐẦU (khởi động app)
    public void loadData(String jsonFile) {

        Gson gson = new Gson();
        // TypeToken được dùng để định dạng cấu trúc JSON có từ là Key của một Map và value là một WordEnglish
        Type mapType = new TypeToken<Map<String, WordEnglish>>() {}.getType();

        // Lấy đường dẫn file json
        try (Reader reader = new InputStreamReader(
                new FileInputStream("src/main/resources/" + jsonFile),
                StandardCharsets.UTF_8)) {

            // Đưa dữ liệu file json vào  map tạm(temp)
            Map<String, WordEnglish> temp = gson.fromJson(reader, mapType);

            // Nếu temp không rỗng thì đưa vào hasmap chính(Để tra cứu chi tiết O(1))
            if (temp != null) dictionaryData.putAll(temp);

            // Đưa từ tiếng Anh hoặc từ tiếng Việt vào cây tiền tố tương ứng
            buildTries();
            System.out.println("✔ Loaded JSON runtime: " + temp.size());

        } catch (Exception e) {
            System.err.println("Error loadData(): " + e.getMessage());
        }
    }

    //RELOAD TOÀN BỘ JSON Khi ta cập nhật hoặc thêm xóa sửa từ điển
    public void reloadFromJson() {
        loadFromJson();   // đọc lại file
        buildTries();     // build lại trie + map
    }

    // ĐỌC JSON TỪ FILE (CHO reloadFromJson)
    private void loadFromJson() {

        // xóa các thuộc tính cũ nếu có (trường hợp là sau khi cập nhật từ điển)
        dictionaryData.clear();
        vietnameseToEnglishMap.clear();
        englishTrie = new TrieTree();
        vietnameseTrie = new TrieTree();

        Gson gson = new Gson();
        // TypeToken được dùng để định dạng cấu trúc JSON có từ là Key của một Map và value là một WordEnglish
        Type mapType = new TypeToken<Map<String, WordEnglish>>() {}.getType();

        try (Reader reader = new InputStreamReader(
                new FileInputStream("src/main/resources/data/" + jsonFileName),
                StandardCharsets.UTF_8)) {

            // Đọc toàn bộ file JSON vào một Map tạm thời
            Map<String, WordEnglish> temp = gson.fromJson(reader, mapType);

            //  Chuyển dữ liệu sang HashMap chính
            if (temp != null)
                dictionaryData.putAll(temp);

        } catch (Exception e) {
            System.err.println("Lỗi loadFromJson(): " + e.getMessage());
        }
    }

    // BUILD TRIE ANH-VIỆT và VIỆT-ANH
    public void buildTries() {

        englishTrie = new TrieTree();
        vietnameseTrie = new TrieTree();
        vietnameseToEnglishMap = new HashMap<>();

        for (var entry : dictionaryData.entrySet()) {
            String eng = entry.getKey();
            WordEnglish w = entry.getValue();

            // Chèn từ tiếng Anh vào cây Trie (Để tra cứu tiền tố O(L))
            englishTrie.insert(eng);

            String vn = w.getTextVietnamese(); //Lấy ra định nghĩa tiếng Việt từ đối tượng WordEnglish
            vietnameseToEnglishMap
                    .computeIfAbsent(vn, k -> new ArrayList<>()) // Kiểm tra xem từ tiếng Việt đó đã là key trong hashmap chưa.
                                                                        // Nếu chưa tạo ra một key mới là từ tiếng Việt(vn) đó và tạo 1 value mới là danh sách từ tiếng anh trống
                                                                        // Nếu đã có trả về danh sách ArayList hiện có
                    .add(eng); // Thêm từ tiếng anh vào danh sách đó ( Kể cả mới tạo hay cái cũ)

            vietnameseTrie.insert(vn); // Thêm từ tiếng Việt đó vào cây tiền tố (Để tra cứu tiền tố O(L))
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
        buildTries();
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

    // duyệt theo tiêu chí tìm kiếm các từ đã lưu
    public List<String> loadDataFavourite(Map<String, WordEnglish> words) {
        List<String> favourites = new ArrayList<>();
        for (Map.Entry<String, WordEnglish> entry : words.entrySet()) {
            if (entry.getValue().isFavourite()) {
                favourites.add(entry.getKey());
            }
        }

        return favourites;
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
