package org.example.model;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {

    private final Map<Character, TrieNode> children = new HashMap<Character, TrieNode>(); //children sẽ lưu trữ 1 ký và 1 node tiếp theo giống Node -> link
    // Đánh dấu xem có phải là kết thúc của một từ hợp lệ hay không.
    private boolean isEndOfWord;

    public  TrieNode()
    {
        isEndOfWord = false;
    }

    // Các Getters và Setters cần thiết
    public Map<Character, TrieNode> getChildren()
    {
        return children;
    }
    public boolean isEndOfWord()
    {
        return isEndOfWord;
    }

    public void setEndOfWord(boolean endOfWord)
    {
        isEndOfWord = endOfWord;
    }

}
