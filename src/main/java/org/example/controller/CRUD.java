package org.example.controller;

import org.example.model.WordEnglish;

import java.util.Map;

public class CRUD {
    private final DataLoader loader;

    public CRUD(DataLoader loader) {
        this.loader = loader;
    }

    /**
     * CREATE - Thêm từ mới vào từ điển
     */
    public boolean addWord(String english, WordEnglish word) {
        Map<String, WordEnglish> dict = loader.getDictionaryData();

        if (dict.containsKey(english)) {
            System.out.println("Từ '" + english + "' đã tồn tại!");
            return false;
        }

        dict.put(english, word);
        System.out.println("Đã thêm từ '" + english + "' vào từ điển (RAM)");
        return true;
    }

    /**
     * READ - Tra cứu chi tiết từ
     */
    public WordEnglish readWord(String english) {
        return loader.getDictionaryData().get(english);
    }

    /**
     * UPDATE - Sửa thông tin một từ
     */
    public boolean updateWord(String english, WordEnglish newWord) {
        Map<String, WordEnglish> dict = loader.getDictionaryData();

        if (!dict.containsKey(english)) {
            System.out.println("Không tìm thấy từ '" + english + "' để sửa!");
            return false;
        }

        dict.put(english, newWord);
        System.out.println("Đã cập nhật từ '" + english + "' trong từ điển (RAM)");
        return true;
    }

    /**
     * DELETE - Xóa từ
     */
    public boolean deleteWord(String english) {
        Map<String, WordEnglish> dict = loader.getDictionaryData();

        if (!dict.containsKey(english)) {
            System.out.println("Không tìm thấy từ '" + english + "' để xóa!");
            return false;
        }

        dict.remove(english);
        System.out.println("Đã xóa từ '" + english + "' khỏi từ điển (RAM)");
        return true;
    }

    /**
     * SAVE - Ghi lại file JSON
     */
    public void saveChanges(String jsonFileName) {
        loader.saveDataToJson(jsonFileName);
    }
}
