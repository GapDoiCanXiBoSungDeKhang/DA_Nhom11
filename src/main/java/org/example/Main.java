package org.example;

import org.example.controller.DataLoader;
import org.example.model.WordEnglish;

import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, WordEnglish> dictionary = DataLoader.loadDictionary();
        for (Map.Entry<String, WordEnglish> entry : dictionary.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
        WordEnglish hello = dictionary.get("hello");
        if (hello != null) {
            System.out.println("\nChi tiết từ 'hello':");
            System.out.println("Nghĩa tiếng Việt: " + hello.getTextVietnamese());
            System.out.println("Ví dụ: " + hello.getExample());
        }
    }
}
