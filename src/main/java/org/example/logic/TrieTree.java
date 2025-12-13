package org.example.logic;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class TrieTree {
    private final TrieNode root;
    public TrieTree()
    {
        root = new TrieNode();
    }

    public void insert(String word)
    {
        TrieNode current = root; // Tạo nút gốc, Bắt đầu duyệt xuống(Gốc luôn là 1 node rỗng)
        for (int i = 0; i < word.length(); i++) // Tạo vòng lặp for để lấy các ký tự từ key
        {
            char c = word.charAt(i);
            Map<Character, TrieNode> children = current.getChildren(); //lấy node con để kiểm tra

            // Nếu node con chưa tồn tại, tạo nút mới
            if (!children.containsKey(c))
            {
                children.put(c, new TrieNode());
            }

            // Nếu node đã tồn tại di chuyển xuống nút con
            current = children.get(c);
        }
        // Đánh dấu nút cuối cùng là kết thúc của một từ
        current.setEndOfWord(true);
    }

    /*
     * Tìm kiếm tất cả các từ bắt đầu bằng 'prefix'.
     * Trả về danh sách các từ gợi ý.
     */
    public List<String> searchByPrefix(String prefix)
    {
        prefix = prefix.toLowerCase();
        List<String> results = new ArrayList<>();//Tạo ds chứa các từ gợi ý
        TrieNode current = root; // Taọ 1 node rỗng

        for (int i = 0; i < prefix.length(); i++)//lấy các ký tự từ đoạn từ nhập vào
        {
            char c = prefix.charAt(i);
            current= current.getChildren().get(c);

            // Nếu một ký tự trong prefix không tồn tại, không có từ nào khớp
            if (current == null)
            {
                return results; // Trả về danh sách rỗng
            }
        }
        // 2. Bắt đầu Duyệt sâu (DFS) từ prefix node để thu thập các từ
        findAllWordsFromNode(current, prefix, results);
        return results;
    }

    /*
     * Phương thức đệ quy (DFS) để thu thập các từ
     */
    private void findAllWordsFromNode(TrieNode node, String currentWord, List<String> results)
    {
        // Nếu node hiện tại là kết thúc của một từ, thêm từ đó vào danh sách các từ gợi ý
        if (node.isEndOfWord())
        {
            results.add(currentWord);
        }
        for(Map.Entry<Character, TrieNode> entry: node.getChildren().entrySet()) //Duyệt qua tất cả các nút con của nút hiện tại
        {
            char c = entry.getKey(); //Lấy ký tự của nút con (chính là ký tự tiếp theo trong từ).
            TrieNode child = entry.getValue();//Lấy đối tượng TrieNode tương ứng với ký tự đó.

            // Gọi đệ quy, thêm ký tự hiện tại vào từ đang xây dựng
            findAllWordsFromNode(child, currentWord + c, results);
        }
    }

    public List<String> getAllWords() {
        List<String> result = new ArrayList<>();
        collectWords(root, "", result);
        return result;
    }

    private void collectWords(TrieNode node, String prefix, List<String> out) {
        if (node == null) return;

        if (node.isEndOfWord()) {
            out.add(prefix);
        }

        for (Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()) {
            char c = entry.getKey();
            TrieNode child = entry.getValue();
            collectWords(child, prefix + c, out);
        }
    }

}
