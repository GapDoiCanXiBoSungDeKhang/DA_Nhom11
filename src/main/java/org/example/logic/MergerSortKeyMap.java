package org.example.logic;

import org.example.model.WordEnglish;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.text.Collator;
import java.util.Locale;

public class MergerSortKeyMap {
    private static final Collator COLLATOR; // khai báo công cụ so sánh theo ngôn ngữ

    static {
        // Khởi tạo Collator theo luật ngôn ngữ tiếng Việt (Việt Nam)
        // "vi"  : mã ngôn ngữ (Vietnamese)
        // "VN"  : mã quốc gia (Vietnam)
        COLLATOR = Collator.getInstance(new Locale("vi", "VN"));
        // Thiết lập mức độ so sánh:
        // PRIMARY nghĩa là:
        //  - Không phân biệt chữ hoa / chữ thường
        //  - Sắp xếp đúng thứ tự bảng chữ cái tiếng Việt
        //  - Xử lý dấu tiếng Việt theo chuẩn từ điển
        //    ví dụ: a < á < â < b < c < d < đ
        COLLATOR.setStrength(Collator.PRIMARY);
    }

    // sắp xếp theo từ điển tiếng anh
    public Map<String, WordEnglish> sortEnToVn(Map<String, WordEnglish> listOfWords) {
        String[] keys = listOfWords.keySet().toArray(new String[0]);
        mergeSort(keys, 0, keys.length - 1);
        LinkedHashMap<String, WordEnglish> sortedMap = new LinkedHashMap<>();
        for (String key : keys) {
            sortedMap.put(key, listOfWords.get(key));
        }
        return sortedMap;
    }

    // sắp xếp theo từ điển tiếng Việt
    public Map<String, List<String>> sortVnToEn(Map<String, List<String>> listOfWords) {
        // lấy key của máp bỏ vào mảng => [hello, angry, happy, ...]
        String[] keys = listOfWords.keySet().toArray(new String[0]);
        // cắt ra từng phần tử và gộp
        mergeSort(keys, 0, keys.length - 1);
        // LinkedHashMap map có thứ tự
        LinkedHashMap<String, List<String>> sortedMap = new LinkedHashMap<>();
        for (String key : keys) {
            sortedMap.put(key, listOfWords.get(key));
        }
//        (từ mảng keys => [angry, baby, cry, ...] =>{"angry": ..., "baby": ..., "cry": ..., ...}
        return sortedMap;
    }


    public void mergeSort(String[] keys, int l, int r) {
        if (l < r) {
            int m = l + (r - l) / 2;
            mergeSort(keys, l, m);
            mergeSort(keys, m + 1, r);
            merge(keys, l, m, r);
        }
    }

    public void merge(String[] list, int l, int m, int r) {
        int n1 = m - l + 1;
        int n2 = r - m;
        String[] L = new String[n1];
        String[] R = new String[n2];

        for (int i = 0; i < n1; i++) L[i] = list[l + i];
        for (int j = 0; j < n2; j++) R[j] = list[m + 1 + j];

        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            if (COLLATOR.compare(L[i], R[j]) <= 0) {
                list[k] = L[i];
                i++;
            } else {
                list[k] = R[j];
                j++;
            }
            k++;
        }
        while (i < n1) {
            list[k] = L[i];
            i++;
            k++;
        }
        while (j < n2) {
            list[k] = R[j];
            j++;
            k++;
        }
    }

    public Map<String, WordEnglish> sortByLastViewedAt(Map<String, WordEnglish> listOfWords) {
        String[] keys = listOfWords.keySet().toArray(new String[0]);
        mergeSortByViewed(keys, 0, keys.length - 1, listOfWords);
        LinkedHashMap<String, WordEnglish> sortedMap = new LinkedHashMap<>();
        for (String key : keys) sortedMap.put(key, listOfWords.get(key));

        return sortedMap;
    }

    public void mergeSortByViewed(String[] keys, int l, int r, Map<String, WordEnglish> listOfWords) {
        if (l < r) {
            int m = l + (r - l) / 2;
            mergeSortByViewed(keys, l, m, listOfWords);
            mergeSortByViewed(keys, m + 1, r, listOfWords);
            mergeByViewed(keys, l, m, r, listOfWords);
        }
    }

    public void mergeByViewed(String[] keys, int l, int m, int r, Map<String, WordEnglish> listOfWords) {
        int n1 = m - l + 1;
        int n2 = r - m;
        String[] L = new String[n1];
        String[] R = new String[n2];

        for (int i = 0; i < n1; i++) L[i] = keys[l + i];
        for (int j = 0; j < n2; j++) R[j] = keys[m + 1 + j];
        int i = 0, j = 0, k = l;
        while (i < n1 && j < n2) {
            long t1 = listOfWords.get(L[i]).getLastViewedTimestamp();
            long t2 = listOfWords.get(R[j]).getLastViewedTimestamp();
            if (t1 >= t2) {
                keys[k] = L[i];
                i++;
            } else {
                keys[k] = R[j];
                j++;
            }
            k++;
        }
        while (i < n1) {
            keys[k] = L[i];
            i++;
            k++;
        }
        while (j < n2) {
            keys[k] = R[j];
            j++;
            k++;
        }
    }
}