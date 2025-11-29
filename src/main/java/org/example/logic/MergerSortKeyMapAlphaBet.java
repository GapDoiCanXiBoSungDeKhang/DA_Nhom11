package org.example.logic;

import org.example.controller.DataLoader;
import org.example.model.WordEnglish;

import java.util.LinkedHashMap;
import java.util.Map;

public class MergerSortKeyMapAlphaBet {
    private DataLoader loader;

    public MergerSortKeyMapAlphaBet(String fileName) {
        this.loader = new DataLoader(fileName);
    }

    public Map<String, WordEnglish> getListOfWords() {
        Map<String, WordEnglish> listOfWords = this.loader.getDictionaryData();
//        Lấy key ra mảng
        String[] keys = listOfWords.keySet().toArray(new String[0]);
        mergeSort(keys, 0, keys.length - 1);
        LinkedHashMap<String, WordEnglish> sortedMap = new LinkedHashMap<>();
        for (String key : keys) {
            sortedMap.put(key, listOfWords.get(key));
        }
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
            if (L[i].compareTo(R[j]) <= 0) {
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
}
