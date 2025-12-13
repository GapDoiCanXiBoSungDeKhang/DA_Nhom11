package org.example;

import org.example.logic.MergerSortKeyMap;
import org.example.model.WordEnglish;
import org.example.ulti.TTS;
import javax.swing.SwingUtilities;
import org.example.view.DictionaryUI;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
         SwingUtilities.invokeLater(() -> {
            new DictionaryUI().setVisible(true);
//        //Test không giao diện
//
//
//
//        // Khởi tạo DataLoader. Toàn bộ dữ liệu được tải và Trie được xây dựng tại đây.
//        DataLoader loader = new DataLoader("Vietnamese_english.json");
//        // Bắt đầu kiểm tra tính năng:
//        System.out.println("=========================================");
//        System.out.println("        KIỂM TRA TÍNH NĂNG TỪ ĐIỂN      ");
//        System.out.println("=========================================");
//
//        //Anh-Việt
//        System.out.println("I.KIỂM TRA ANH-VIỆT");
//
//        // Test 1: Tiền tố 'a'
//        String prefix1 = "a";
//        List<String> suggestions1 = loader.getEnglishTrie().searchByPrefix(prefix1);
//        System.out.println("Gợi ý cho prefix '" + prefix1 + "': " + suggestions1);
//
//        // Test 2: Tiền tố 'app'
//        String prefix2 = "appl";
//        List<String> suggestions2 = loader.getEnglishTrie().searchByPrefix(prefix2);
//        System.out.println("Gợi ý cho prefix '" + prefix2 + "': " + suggestions2);
//        // Kết quả mong đợi: ["apple", "apply"]
//
//        // Test 3: Tiền tố không tồn tại
//        String prefix3 = "xyz";
//        List<String> suggestions3 = loader.getEnglishTrie().searchByPrefix(prefix3);
//        System.out.println("Gợi ý cho prefix '" + prefix3 + "': " + suggestions3);
//
//        // Test 4: Tiền tố không tồn tại
//        String prefix4 = "A";
//        List<String> suggestions4 = loader.getEnglishTrie().searchByPrefix(prefix4);
//        System.out.println("Gợi ý cho prefix '" + prefix4 + "': " + suggestions4);
//
//
//        // submit tiếng anh
//
//        // --- 2. KIỂM TRA TÍNH NĂNG TRA CỨU CHI TIẾT (HashMap) ---
//        System.out.println("\n--- 2. Kiểm tra HashMap ANH-VIỆT (Tra cứu chi tiết) ---");
//        Map<String, WordEnglish> dictionary = loader.getDictionaryData();
//
//        // Test 1: Tra cứu từ 'apple'
//        String word1 = "apple";
//        WordEnglish result1 = dictionary.get(word1);
//        System.out.println("Tra cứu từ '" + word1 + "':");
//        if (result1 != null)
//        {
//            System.out.println("  Nghĩa tiếng Việt: " + result1.getTextVietnamese());
//            System.out.println("  Chi tiết: ");
//            System.out.println("    từ loại: "+result1.getType());
//            System.out.println("    Phiên âm: "+result1.getTranscription());
//            System.out.println("    Câu ví dụ: "+result1.getExample());
//        }
//        else
//        {
//            System.out.println("  Không tìm thấy từ.");
//        }
//
//        // Test 2 : Tra cứu từ 'computer'
//        String word2 = "computer";
//        WordEnglish result2 = dictionary.get(word2);
//        System.out.println("Tra cứu từ '" + word2 + "':");
//        if (result2 != null)
//        {
//            System.out.println("  Nghĩa tiếng Việt: " + result2.getTextVietnamese());
//        }
//        else
//        {
//            System.out.println("  Không tìm thấy từ.");
//        }
//        // kết thúc tiếng anh
//
//
//
//        //VIỆT-ANH
//        System.out.println("\nII.KIỂM TRA ANH-VIỆT");
//
//        // Test 1 kiểm tra tiền tố q
//        String vn_prefix1 = "q";
//        List<String> vn_suggestions1 = loader.getVietnameseTrie().searchByPrefix(vn_prefix1);
//        System.out.println("Gợi ý tiếng Việt cho prefix '" + vn_prefix1 + "': " + vn_suggestions1);
//
//        // Test 2 Kiểm tra tiền tố quả
//        String vn_prefix2 = "quả";
//        List<String> vn_suggestions2 = loader.getVietnameseTrie().searchByPrefix(vn_prefix2);
//        System.out.println("Gợi ý tiếng Việt cho prefix '" + vn_prefix2 + "': " + vn_suggestions2);
//
//
//
//
//
//        // --- 2. KIỂM TRA TÍNH NĂNG TRA CỨU VIỆT-ANH ---
//        System.out.println("\n--- 2. Kiểm tra HashMap ANH-VIỆT (Tra cứu chi tiết) ---");
//
//
//        // submit tiếng Việt Nam
//
//        Map<String, List<String>> dataVietAnh = loader.getVietnameseToEnglishMap();
//        // Test 1: tra cứu từ 'công ty'
//        String word3 = "công ty";
//
//        // Lấy danh sách các từ tiếng Anh có cùng nghĩa tiếng việt với word3
//        List<String> lisEnglishWords = dataVietAnh.get(word3);
//
//        System.out.println("Tra cứu từ '" + word3 + "':");
//        if(lisEnglishWords != null && !lisEnglishWords.isEmpty())
//        {
//            boolean isMultiEnglish = lisEnglishWords.size() > 1;
//            int i=0;
//            for (String englishWord : lisEnglishWords) // Duyệt qua danh sách các từ tiếng anh
//            {
//                WordEnglish result3 = dictionary.get(englishWord);
//                if(isMultiEnglish)
//                    System.out.println("  Nghĩa tiếng Anh thứ " + (++i)+": "+englishWord);
//                else
//                    System.out.println("Nghĩa tiếng Anh: "+englishWord);
//                System.out.println("  Chi tiết: ");
//                System.out.println("    từ loại: "+result1.getType());
//                System.out.println("    Phiên âm: "+result1.getTranscription());
//                System.out.println("    Câu ví dụ: "+result1.getExample());
//            }
//        }
//
//        // kết thúc
//
//        System.out.println("\n--- 3. CRUD(thêm, xóa, chỉnh sửa, xem)) ---");

//        CRUD crud = new CRUD(loader);

        // Thêm từ mới
//        WordEnglish newWord = new WordEnglish();
//        newWord.setTextVietnamese("bàn phím");
//        newWord.setType("noun");
//        newWord.setExample("I bought a new keyboard for my computer.");
//        newWord.setTranscription("ˈkiː.bɔːd");
//
//        crud.addWord("keyboard", newWord);

        // Sửa
//        WordEnglish updateWord = new WordEnglish();
//        updateWord.setTextVietnamese("bút chì");
//        updateWord.setType("noun");
//        updateWord.setExample("He drew with a pencil.");
//        updateWord.setTranscription("ˈpen.səl");
//
//        crud.updateWord("pencil", updateWord);
//
//        // Xóa
//        crud.deleteWord("pencil");
//
//        WordEnglish readWord = crud.readWord("keyboard");
//
//        if (readWord != null) {
//            System.out.println("Từ tiếng Anh: " + "keyboard");
//            System.out.println("Nghĩa tiếng Việt: " + readWord.getTextVietnamese());
//            System.out.println("Loại từ: " + readWord.getType());
//            System.out.println("Ví dụ: " + readWord.getExample());
//            System.out.println("Phiên âm: " + readWord.getTranscription());
//        } else {
//            System.out.println("Không tìm thấy từ 'keyboard' trong từ điển.");
//        }

        // Lưu thay đổi ra file
//        crud.saveChanges("Vietnamese_english.json");
        // --- 3. KIỂM TRA TÍNH NĂNG TTS ĐỂ ĐỌC TỪ VỰNG ---
//        System.out.println("\n--- 3. Kiểm tra tính năng TTS để đọc từ vựng ---");
//        TTS.init();
//        String testWord = "Here is the passage to test the TTS feature. If you can hear this then the feature has been added successfully";
//        System.out.println("Đoạn test: " + testWord);
//        TTS.speak(testWord);
//
//        //Tạm dừng hàm main để nghe giọng đọc để test
//        try {
//            TimeUnit.SECONDS.sleep(15);
//        } catch (InterruptedException ignored) {}
//
//        TTS.close();

//
//        Map<String, WordEnglish> sortedWords = record.getListOfWords();
//
//        // Vòng lặp in ra thông tin từng từ
//        for (Map.Entry<String, WordEnglish> entry : sortedWords.entrySet()) {
//            String word = entry.getKey();
//            WordEnglish info = entry.getValue();
//
//            System.out.println("Word: " + word);
//            System.out.println("Meaning: " + info.getTextVietnamese());
//            System.out.println("Type: " + info.getType());
//            System.out.println("Example: " + info.getExample());
//            System.out.println("Transcription: " + info.getTranscription());
//            System.out.println("Favourite: " + info.isFavourite());
//            System.out.println("Created At: " + info.getCreatedAt());
//            System.out.println("Updated At: " + info.getUpdatedAt());
//            System.out.println("---------------------------");
//        }

       });
    }
}
