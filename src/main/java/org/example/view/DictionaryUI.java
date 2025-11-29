package org.example.view;

import org.example.controller.DataLoader;
import org.example.model.WordEnglish;
import javax.swing.border.TitledBorder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import javax.swing.border.Border;

public class DictionaryUI extends JFrame {
    class RoundedListPanel extends JPanel {
        private int arc = 25;

        public RoundedListPanel() {
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding trong
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // nền trắng
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            // border
            g2.setColor(new Color(220,220,225));
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, arc, arc);

            g2.dispose();
        }
    }
    class CardComponent {
        RoundedCardPanel panel;
        JTextArea area;

        CardComponent(RoundedCardPanel panel, JTextArea area) {
            this.panel = panel;
            this.area = area;
        }
    }
    class RoundedCardPanel extends JPanel {
        private int arc = 25;  // bo tròn đẹp

        public RoundedCardPanel() {
            setOpaque(false);
            setLayout(new BorderLayout());
            setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Color.WHITE); // nền card
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            g2.setColor(new Color(220, 220, 225)); // border mảnh
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, arc, arc);

            g2.dispose();
        }
    }
    public class RoundedToggleButton extends JToggleButton {
        private int arc = 40;                // bán kính bo tròn
        private Color bgColor = new Color(0,122,255);
        private Color hoverColor = new Color(10,142,255);
        private Color pressedColor = new Color(0,100,210);
        private boolean hover = false;
        private boolean pressed = false;

        public RoundedToggleButton(String text) {
            super(text);
            setContentAreaFilled(false);   // KHÔNG cho LAF vẽ nền mặc định
            setBorderPainted(false);       // KHÔNG cho LAF vẽ border
            setFocusPainted(false);        // tắt focus rectangle
            setOpaque(false);              // cho phép background trong suốt
            setForeground(Color.WHITE);
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setMargin(new Insets(8, 18, 8, 18));

            // hover/press listeners
            initListeners();
        }

        private void initListeners() {
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(java.awt.event.MouseEvent e)  { hover = false; repaint(); }
                public void mousePressed(java.awt.event.MouseEvent e) { pressed = true; repaint(); }
                public void mouseReleased(java.awt.event.MouseEvent e){ pressed = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // choose color depending on state
            Color fill;
            // màu khi bật (selected)
            Color green = new Color(52, 199, 89);           // xanh lá Apple style
            Color greenHover = new Color(48, 180, 80);
            Color greenPressed = new Color(40, 160, 70);

            // màu khi tắt (normal)
            Color blue = new Color(0, 122, 255);
            Color blueHover = new Color(10, 142, 255);
            Color bluePressed = new Color(0, 100, 210);
            // chế độ đang bật = xanh lá
            if (isSelected()) {
                if (pressed)      fill = greenPressed;
                else if (hover)   fill = greenHover;
                else              fill = green;
            }
            // chế độ đang tắt = xanh dương
            else {
                if (pressed)      fill = bluePressed;
                else if (hover)   fill = blueHover;
                else              fill = blue;
            }
            // draw rounded background
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            // optional subtle border
            g2.setColor(new Color(200,200,200,80));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);

            // paint text (center)
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int tw = fm.stringWidth(text);
            int th = fm.getAscent();
            int tx = (getWidth() - tw) / 2;
            int ty = (getHeight() + th) / 2 - 2;

            g2.setColor(getForeground());
            g2.setFont(getFont());
            g2.drawString(text, tx, ty);

            g2.dispose();
        }
    }
    class RoundedBorder implements Border {
        private int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(8, 15, 8, 15);
        }

        @Override
        public boolean isBorderOpaque() {
            return false;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(200, 200, 200)); // màu viền
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }
    class RoundedTextField extends JTextField {

        private int arcWidth = 40;
        private int arcHeight = 40;

        public RoundedTextField(int padding) {
            super();
            setOpaque(false);  // Quan trọng để bo góc
            setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
            setFont(new Font("SansSerif", Font.PLAIN, 16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Nền màu trắng
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(1, 1, getWidth() - 3, getHeight() - 3, arcWidth, arcHeight);

            super.paintComponent(g2);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(new Color(200, 200, 200)); // viền xám nhẹ
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arcWidth, arcHeight);
            g2.dispose();
        }
    }
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

        controller = new DataLoader("data/Vietnamese_english.json"); // LOAD FROM RESOURCES

        setTitle("English ↔ Vietnamese Dictionary");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(macBg);

        // SEARCH BAR
        searchField = new RoundedTextField(10);
        searchField.setPreferredSize(new Dimension(320, 40));
        // WRAPPER để tạo khoảng trống
        JPanel searchWrapper = new JPanel(new BorderLayout());
        searchWrapper.setOpaque(false);
        searchWrapper.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));  // thêm khoảng cách top & bottom
        searchWrapper.add(searchField, BorderLayout.CENTER);

        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(macBg);
        searchPanel.add(searchWrapper, BorderLayout.CENTER);
        // MODE SWITCH
        RoundedToggleButton modeSwitch = new RoundedToggleButton("English → Vietnamese");
        modeSwitch.addActionListener(e -> {
            isEnglishMode = !isEnglishMode;
            modeSwitch.setText(isEnglishMode ? "English → Vietnamese" : "Vietnamese → English");
            listModel.clear();
            clearDetail();
        });

        JPanel topPanel = new JPanel(new BorderLayout(20, 20));
        topPanel.setBorder(BorderFactory.createEmptyBorder(25, 15, 10, 15));
        topPanel.setBackground(macBg);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        topPanel.add(modeSwitch, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // SUGGESTION LIST
        listModel = new DefaultListModel<>();
        suggestionList = new JList<>(listModel);
        suggestionList.setFont(new Font("SansSerif", Font.PLAIN, 16));
        suggestionList.setSelectionBackground(macLightBlue);
        suggestionList.setFixedCellHeight(32);
        suggestionList.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // scroll bên trong bo góc
        JScrollPane scrollList = new JScrollPane(suggestionList);
        scrollList.setBorder(BorderFactory.createEmptyBorder(8,0,0,0));
        scrollList.getViewport().setOpaque(false);
        scrollList.setOpaque(false);

        // panel bo góc đẹp
        RoundedListPanel listCard = new RoundedListPanel();
        listCard.add(scrollList, BorderLayout.CENTER);

        listCard.setPreferredSize(new Dimension(260, 0));

        // cách lề trái
        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setBackground(macBg);
        leftWrapper.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));

        leftWrapper.add(listCard, BorderLayout.CENTER);
        add(leftWrapper, BorderLayout.WEST);

        // DETAILS PANEL
        JPanel detail = new JPanel();
        detail.setLayout(new BoxLayout(detail, BoxLayout.Y_AXIS));
        detail.setBackground(macBg);
        detail.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        detail.setAlignmentX(Component.LEFT_ALIGNMENT);

        typeLabel = new JLabel("Type: ");
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        phoneticLabel = new JLabel("Phonetic: ");
        phoneticLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        phoneticLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        CardComponent meaningCard = createCard("Meaning");
        CardComponent exampleCard = createCard("Example");

        meaningArea = meaningCard.area;
        exampleArea = exampleCard.area;

        meaningCard.panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        exampleCard.panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        detail.add(typeLabel);
        detail.add(Box.createVerticalStrut(5));
        detail.add(phoneticLabel);
        detail.add(Box.createVerticalStrut(10));
        detail.add(meaningCard.panel);
        detail.add(Box.createVerticalStrut(15));
        detail.add(exampleCard.panel);

        add(detail, BorderLayout.CENTER);

        // EVENTS
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { updateSuggestions(); }
        });

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
    //  CARD TEXT AREA
    private CardComponent createCard(String title) {
        RoundedCardPanel card = new RoundedCardPanel();

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        // JTextArea phải opaque, có margin nội bộ để text không sát mép
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("SansSerif", Font.PLAIN, 16));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setOpaque(true);                          // IMPORTANT: cho nền trắng
        area.setBackground(new Color(255,255,255));    // nền trắng trên card
        area.setMargin(new Insets(10, 12, 10, 12));    // padding nội bộ
        area.setBorder(BorderFactory.createEmptyBorder()); // không cần border của textArea

        // Bọc vào JScrollPane (dễ scroll nếu nội dung nhiều)
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createEmptyBorder()); // xóa border của scrollpane
        // đảm bảo scrollpane cũng trong suốt để thấy rounded card nền
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        // thêm vào card
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);

        return new CardComponent(card, area);
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