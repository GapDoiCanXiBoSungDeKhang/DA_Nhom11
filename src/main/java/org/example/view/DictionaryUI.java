package org.example.view;

import org.example.controller.DataLoader;
import org.example.model.WordEnglish;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

public class DictionaryUI extends JFrame {

    // SEARCH FIELD WITH LEFT PADDING 
    class SearchTextField extends JTextField {
        public SearchTextField() {
            super();
            setFont(new Font("SansSerif", Font.PLAIN, 16));
            setPreferredSize(new Dimension(320, 40));
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        }
    }

    // VARIABLES 
    private final DataLoader controller;

    private JTextField searchField;
    private JButton searchButton;

    private JList<String> suggestionList;
    private DefaultListModel<String> listModel;

    private JTextArea meaningArea, exampleArea;
    private JLabel typeLabel, phoneticLabel;

    private boolean isEnglishMode = true;

    // macOS colors
    private final Color macBg = new Color(245, 245, 248);
    private final Color cardBg = Color.WHITE;
    private final Color border = new Color(220, 220, 225);
    private final Color macBlue = new Color(0, 122, 255);
    private final Color macLightBlue = new Color(180, 215, 255);

    // CONSTRUCTOR
    public DictionaryUI() {

        controller = new DataLoader("Vietnamese_english.json"); // LOAD FROM RESOURCES ✔

        setTitle("English ↔ Vietnamese Dictionary");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(macBg);

        // SEARCH BAR 
        searchField = new SearchTextField();
        searchButton = createSearchButton();

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(macBg);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);

        // MODE SWITCH
        JToggleButton modeSwitch = new JToggleButton("English → Vietnamese");
        modeSwitch.setFont(new Font("SansSerif", Font.BOLD, 14));
        modeSwitch.setBackground(macBlue);
        modeSwitch.setForeground(Color.WHITE);
        modeSwitch.setFocusPainted(false);
        modeSwitch.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        modeSwitch.addActionListener(e -> {
            isEnglishMode = !isEnglishMode;
            modeSwitch.setText(isEnglishMode ? "English → Vietnamese" : "Vietnamese → English");
            listModel.clear();
            clearDetail();
        });

        JPanel topPanel = new JPanel(new BorderLayout(20, 20));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        topPanel.setBackground(macBg);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(modeSwitch, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // SUGGESTION LIST 
        listModel = new DefaultListModel<>();
        suggestionList = new JList<>(listModel);
        suggestionList.setFont(new Font("SansSerif", Font.PLAIN, 16));
        suggestionList.setSelectionBackground(macLightBlue);

        JScrollPane scrollList = new JScrollPane(suggestionList);
        scrollList.setPreferredSize(new Dimension(260, 0));
        scrollList.setBorder(BorderFactory.createLineBorder(border, 1, true));

        add(scrollList, BorderLayout.WEST);

        // DETAILS PANEL
        JPanel detail = new JPanel();
        detail.setLayout(new BoxLayout(detail, BoxLayout.Y_AXIS));
        detail.setBackground(macBg);
        detail.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        typeLabel = new JLabel("Type: ");
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));

        phoneticLabel = new JLabel("Phonetic: ");
        phoneticLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));

        meaningArea = createCard("Meaning");
        exampleArea = createCard("Example");

        detail.add(typeLabel);
        detail.add(Box.createVerticalStrut(5));
        detail.add(phoneticLabel);
        detail.add(Box.createVerticalStrut(10));
        detail.add(meaningArea);
        detail.add(Box.createVerticalStrut(15));
        detail.add(exampleArea);

        add(detail, BorderLayout.CENTER);

        // EVENTS 
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { updateSuggestions(); }
        });

        searchButton.addActionListener(e -> updateSuggestions());

        suggestionList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String word = suggestionList.getSelectedValue();
                if (word == null) return;
                if (isEnglishMode) showEnglishWord(word);
                else showVietnameseWord(word);
            }
        });
    }

    //CREATE SEARCH BUTTON (LOAD ICON CORRECTLY) 
    private JButton createSearchButton() {
        JButton btn = new JButton();

        // Load icon from resources ✔
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/search.png"));
        Image scaled = icon.getImage().getScaledInstance(28, 28, Image.SCALE_SMOOTH);
        btn.setIcon(new ImageIcon(scaled));

        btn.setBackground(Color.WHITE);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btn.setFocusable(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return btn;
    }

    //  CARD TEXT AREA 
    private JTextArea createCard(String title) {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("SansSerif", Font.PLAIN, 16));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        area.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(border, 1, true),
                title
        ));
        area.setBackground(cardBg);
        return area;
    }

    // SEARCH UPDATE 
    private void updateSuggestions() {
        String text = searchField.getText().trim().toLowerCase();
        listModel.clear();
        if (text.isEmpty()) return;

        List<String> result = isEnglishMode ?
                controller.getEnglishTrie().searchByPrefix(text)
                : controller.getVietnameseTrie().searchByPrefix(text);

        result.forEach(listModel::addElement);
    }

    //  WORD DISPLAY 
    private void showEnglishWord(String word) {
        WordEnglish w = controller.getDictionaryData().get(word);
        if (w == null) return;

        typeLabel.setText("Type: " + w.getType());
        phoneticLabel.setText("Phonetic: " + w.getTranscription());
        meaningArea.setText(w.getTextVietnamese());
        exampleArea.setText(w.getExample());
    }

    private void showVietnameseWord(String vn) {
        clearDetail();

        var list = controller.getVietnameseToEnglishMap().get(vn);
        if (list == null) return;

        StringBuilder sb = new StringBuilder();
        for (String eng : list) {
            WordEnglish w = controller.getDictionaryData().get(eng);
            sb.append("• ").append(eng).append("\n");
            sb.append("   Type: ").append(w.getType()).append("\n");
            sb.append("   Phonetic: ").append(w.getTranscription()).append("\n");
            sb.append("   Example: ").append(w.getExample()).append("\n\n");
        }

        meaningArea.setText("Vietnamese: " + vn);
        exampleArea.setText(sb.toString());
    }

    private void clearDetail() {
        typeLabel.setText("Type: ");
        phoneticLabel.setText("Phonetic: ");
        meaningArea.setText("");
        exampleArea.setText("");
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
       SwingUtilities.invokeLater(() -> new DictionaryUI().setVisible(true));
    }
}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

