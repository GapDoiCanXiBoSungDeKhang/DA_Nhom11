package org.example.view;

import org.example.controller.DataLoader;
import org.example.model.WordEnglish;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class WordManagerUI extends javax.swing.JFrame {

    private final DataLoader controller;

    private JList<String> wordList;
    private DefaultListModel<String> listModel;

    private JTextField fieldWord, fieldType, fieldPhonetic;
    private JTextArea fieldMeaning, fieldExample;

    public WordManagerUI() {
        this.controller = new DataLoader("data/Vietnamese_english.json");

        setTitle("Word Manager - Add / Edit / Delete Words");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
        loadWords();
    }

    private void initUI() {
        listModel = new DefaultListModel<>();
        wordList = new JList<>(listModel);
        wordList.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JScrollPane leftPane = new JScrollPane(wordList);
        leftPane.setPreferredSize(new Dimension(250, 0));

        add(leftPane, BorderLayout.WEST);

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

        for (String key : controller.getDictionaryData().keySet()) {
            listModel.addElement(key);
        }
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

        controller.getDictionaryData().put(word,
            new WordEnglish(
                fieldType.getText(),
                fieldPhonetic.getText(),
                fieldMeaning.getText(),
                fieldExample.getText()
            )
        );

        listModel.addElement(word);
        JOptionPane.showMessageDialog(this, "Added successfully!");
    }

    private void updateWord() {
        String oldWord = wordList.getSelectedValue();
        if (oldWord == null) return;

        String newWord = fieldWord.getText();

        WordEnglish w = controller.getDictionaryData().get(oldWord);
        w.setType(fieldType.getText());
        w.setTranscription(fieldPhonetic.getText());
        w.setTextVietnamese(fieldMeaning.getText());
        w.setExample(fieldExample.getText());

        if (!oldWord.equals(newWord)) {
            controller.getDictionaryData().remove(oldWord);
            controller.getDictionaryData().put(newWord, w);

            listModel.removeElement(oldWord);
            listModel.addElement(newWord);
        }

        JOptionPane.showMessageDialog(this, "Updated successfully!");
    }

    private void deleteWord() {
        String key = wordList.getSelectedValue();
        if (key == null) return;

        controller.getDictionaryData().remove(key);
        listModel.removeElement(key);

        JOptionPane.showMessageDialog(this, "Deleted successfully!");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
        java.awt.EventQueue.invokeLater(() -> new WordManagerUI().setVisible(true));
    }
}

// Variables declaration - do not modify//GEN-BEGIN:variables
// End of variables declaration//GEN-END:variables

