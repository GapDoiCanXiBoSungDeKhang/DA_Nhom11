package org.example.view;

import org.example.controller.DataLoader;
import org.example.model.WordEnglish;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Map;

public class WordManagerUI extends javax.swing.JFrame {

    private final DataLoader controller;
    private JTextField searchFieldManager;

    private JList<String> wordList;
    private DefaultListModel<String> listModel;

    private JTextField fieldWord, fieldType, fieldPhonetic;
    private JTextArea fieldMeaning, fieldExample;

    private boolean isDataModified =false;

    private final DictionaryUI parent;
    
    private void updateManagerSuggestion() {
    String text = searchFieldManager.getText().trim().toLowerCase();
    listModel.clear();

    if (text.isEmpty()) {
        controller.getDictionaryData()
                .keySet()
                .stream()
                .sorted(String::compareToIgnoreCase)
                .forEach(listModel::addElement);
        return;
    }

    controller.getDictionaryData()
            .keySet()
            .stream()
            .filter(w -> w.toLowerCase().startsWith(text))
            .sorted(String::compareToIgnoreCase)
            .forEach(listModel::addElement);
}
    
    public WordManagerUI(DictionaryUI parent) {
    this.parent = parent;
    this.controller = parent.controller;   // dùng chung controller (QUAN TRỌNG!)

        initComponents();
        setTitle("Word Manager - Add / Edit / Delete Words");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
        loadWords();
        //BẮT SỰ KIỆN ĐÓNG CỬA SỔ
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit(); // Gọi hàm xử lý việc thoát
            }
        });
    }

    private void initUI() {
        // ==== SEARCH BAR (FOR WORD MANAGER) ====
        searchFieldManager = new JTextField();
        searchFieldManager.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchFieldManager.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        searchFieldManager.setPreferredSize(new Dimension(250, 35));

        searchFieldManager.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateManagerSuggestion();
            }
        });

        // ==== LIST ====
        listModel = new DefaultListModel<>();
        wordList = new JList<>(listModel);
        wordList.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JScrollPane scrollList = new JScrollPane(wordList);

        // ==== PANEL GỒM SEARCH + LIST ====
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(250, 0));

        leftPanel.add(searchFieldManager, BorderLayout.NORTH);
        leftPanel.add(scrollList, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // Form
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        fieldWord = new JTextField();
        fieldType = new JTextField();
        fieldPhonetic = new JTextField();
        fieldMeaning = new JTextArea(4, 20);
        fieldExample = new JTextArea(4, 20);

        fieldMeaning.setLineWrap(true);
        fieldExample.setLineWrap(true);

        form.add(createField("Word (English)", fieldWord));
        form.add(createField("Type", fieldType));
        form.add(createField("Phonetic", fieldPhonetic));
        form.add(createArea("Meaning", fieldMeaning));
        form.add(createArea("Example", fieldExample));
        
        JButton btnAdd = new JButton("Add Word");
        JButton btnUpdate = new JButton("Update Word");
        JButton btnDelete = new JButton("Delete Word");
        JButton btnSave = new JButton("Save to JSON");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnSave);
        // NÚT QUAY VỀ DICTIONARY UI
        JButton btnBack = new JButton("⬅ Back");
        btnBack.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBack.setBackground(new Color(230, 230, 230));
        btnBack.setFocusPainted(false);
        btnBack.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnBack.addActionListener(e -> {

        // Nếu có thay đổi → lưu JSON
        if (isDataModified) {
            controller.saveDataToJson("Vietnamese_english.json");
        }

        // Reload lại controller (Trie + Map + Data)
        controller.reloadFromJson();

        // ĐÓNG WordManagerUI
        this.dispose();

        // MỞ GIAO DIỆN DICTIONARY MỚI
        DictionaryUI ui = new DictionaryUI();
        ui.setVisible(true);
    });

        // add vào panel chứa các nút
        buttonPanel.add(btnBack);

        form.add(buttonPanel);

        add(form, BorderLayout.CENTER);

        // Events
        wordList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedWord();
        });

        btnAdd.addActionListener(e -> addWord());
        btnUpdate.addActionListener(e -> updateWord());
        btnDelete.addActionListener(e -> deleteWord());
        btnSave.addActionListener(e -> {
            controller.saveDataToJson("Vietnamese_english.json");
            isDataModified = false;
            JOptionPane.showMessageDialog(this, "Saved to JSON successfully!");
        });
    }

    private JPanel createField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JPanel createArea(String label, JTextArea area) {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.add(new JLabel(label), BorderLayout.NORTH);
        p.add(new JScrollPane(area), BorderLayout.CENTER);
        return p;
    }

    private void loadWords() {
    listModel.clear();
    searchFieldManager.setText("");

    controller.getDictionaryData()
            .keySet()
            .stream()
            .sorted(String::compareToIgnoreCase)   // SẮP XẾP A → Z
            .forEach(listModel::addElement);
}

    private void loadSelectedWord() {
        String key = wordList.getSelectedValue();
        if (key == null) return;

        WordEnglish w = controller.getDictionaryData().get(key);
        if (w == null) return;

        fieldWord.setText(key);
        fieldType.setText(w.getType());
        fieldPhonetic.setText(w.getTranscription());
        fieldMeaning.setText(w.getTextVietnamese());
        fieldExample.setText(w.getExample());
    }

    private void addWord() {
    String word = fieldWord.getText().trim();

    if (word.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Word cannot be empty");
        return;
    }

    WordEnglish w = new WordEnglish(
            fieldType.getText(),
            fieldPhonetic.getText(),
            fieldMeaning.getText(),
            fieldExample.getText()
    );

    // ❗ thêm từ đúng cách (update Trie + Map)
    controller.addWordToTries(word, w);

    loadWords();
    isDataModified = true;

    JOptionPane.showMessageDialog(this, "Added successfully!");
}

    private void updateWord() {
    String oldWord = wordList.getSelectedValue();
    if (oldWord == null) return;

    String newWord = fieldWord.getText().trim();

    // Lấy word cũ
    WordEnglish w = controller.getDictionaryData().get(oldWord);

    w.setType(fieldType.getText());
    w.setTranscription(fieldPhonetic.getText());
    w.setTextVietnamese(fieldMeaning.getText());
    w.setExample(fieldExample.getText());

    // Đổi tên từ
    if (!oldWord.equals(newWord)) {

        // Xóa từ cũ đúng cách
        controller.removeWordFromTries(oldWord);

        // Thêm lại như từ mới
        controller.addWordToTries(newWord, w);
    }

    loadWords();
    isDataModified = true;
    JOptionPane.showMessageDialog(this, "Updated successfully!");
}

    private void deleteWord() {
    String key = wordList.getSelectedValue();
    if (key == null) return;

    controller.removeWordFromTries(key);

    listModel.removeElement(key);
    isDataModified = true;

    JOptionPane.showMessageDialog(this, "Deleted successfully!");
}

    private void confirmExit() {
        if (!isDataModified) {
            // Nếu KHÔNG có thay đổi, thoát ngay lập tức
            dispose(); // Giải phóng tài nguyên của JFrame này
            System.exit(0); // Thoát toàn bộ ứng dụng
            return;
        }

        // Nếu CÓ thay đổi, hiển thị hộp thoại xác nhận
        int option = JOptionPane.showConfirmDialog(
                this,
                "Từ điển đã được thay đổi nhưng chưa lưu. Bạn có muốn lưu lại không?",
                "Xác nhận Thoát",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            // Người dùng chọn CÓ (Lưu)
            controller.saveDataToJson("Vietnamese_english.json");
            isDataModified = false; // Đã lưu xong

            // Thoát ứng dụng
            dispose();
            System.exit(0);
        } else if (option == JOptionPane.NO_OPTION) {
            // Người dùng chọn KHÔNG (Không lưu)

            //Thoát ứng dụng
            dispose();
            System.exit(0);
        }
        // Nếu người dùng chọn CANCEL, cửa sổ vẫn mở và không làm gì cả
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    public static void main(String args[]) {
          java.awt.EventQueue.invokeLater(() -> {
        DictionaryUI ui = new DictionaryUI();
        new WordManagerUI(ui).setVisible(true);
    });
    }
}

// Variables declaration - do not modify//GEN-BEGIN:variables
// End of variables declaration//GEN-END:variables

