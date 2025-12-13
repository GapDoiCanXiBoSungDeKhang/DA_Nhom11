package org.example.view;

/* ===================== IMPORT ===================== */

import org.example.controller.DataLoader;
import org.example.model.WordEnglish;
import org.example.logic.MergerSortKeyMap;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.Set;

/* ================================================== */
/* ================== MAIN CLASS ==================== */
/* ================================================== */

public class DictionaryUI extends JFrame {
    
    /* ================= BIẾN XỬ LÝ DATA ================= */
    
    // MergeSort dùng để sắp xếp Map
    private final MergerSortKeyMap sorter = new MergerSortKeyMap();

    // Controller quản lý dữ liệu (JSON + Trie)
    private final DataLoader controller;
    
    
    // true = EN → VN | false = VN → EN
    private boolean isEnglishMode = true;
    
     // dùng để detect click lại
    private String lastSelectedWord = null;
    
     /* ================= HISTORY ================= */
    
     private static java.util.List<String> historyWords = new java.util.ArrayList<>();
     
     /* ================= COMPONENT UI ================= */
     
    private JTextField searchField;
    private JList<String> suggestionList;
    private DefaultListModel<String> listModel;

    private JLabel wordLabel, typeLabel, phoneticLabel;
    private JTextArea meaningArea, exampleArea;

    private JButton favButton, soundButton;
    private RoundedToggleButton modeSwitch;
    
    /* ================= MÀU GIAO DIỆN ================= */
    
    private final Color macBg = new Color(235, 247, 255);
    private final Color macBlue = new Color(0, 122, 255);
    private final Color macLightBlue = new Color(180, 215, 255);
    
    /* ==================================================
     * ================= INNER CLASSES ==================
     * ================================================== */
    
      /* ---------- NÚT QUẢN LÝ ---------- */
    
    class ManagerButton extends JButton {
        
        private final int arc = 40;
        private Color normal = new Color(180, 215, 255);     // pastel blue
        private Color hover = new Color(160, 200, 250);
        private boolean isHover = false;

        public ManagerButton() {
            super("⚙");  // icon bánh răng
            setFont(new Font("SansSerif", Font.BOLD, 20));
            setForeground(Color.BLACK);

            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            setToolTipText("Go to Word Manager");

            // hover
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                    isHover = true; repaint();
                }
                @Override public void mouseExited(java.awt.event.MouseEvent e) {
                    isHover = false; repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(isHover ? hover : normal);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            super.paintComponent(g2);
            g2.dispose();
        }
    }
    
     /* ---------- SEARCH BAR ---------- */
    
    class SearchBar extends JPanel {
    
        public JTextField field;
    
    public SearchBar() {
        setLayout(new BorderLayout());
        setOpaque(false);

        // ICON SEARCH
        JLabel icon = new JLabel("🔍");
        icon.setFont(new Font("SansSerif", Font.PLAIN, 23));
        icon.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 8));

        // ROUNDED TEXT FIELD
        field = new RoundedTextField(10);
        field.setPreferredSize(new Dimension(420, 40));
        field.setFont(new Font("SansSerif", Font.PLAIN, 16));

        // ADD
        add(icon, BorderLayout.WEST);
        add(field, BorderLayout.CENTER);

        setMaximumSize(new Dimension(600, 40));
    }
}
    
    /* ---------- CỬA SỔ HISTORY ---------- */
    
    class HistoryWindow extends JFrame {
        
        public HistoryWindow(java.util.List<String> history, DictionaryUI mainUI) {
            setTitle("Search History");
            setSize(400, 500);
            setLocationRelativeTo(null);

            DefaultListModel<String> model = new DefaultListModel<>();
            history.forEach(model::addElement);

            JList<String> list = new JList<>(model);
            list.setFont(new Font("SansSerif", Font.PLAIN, 18));

            JScrollPane scroll = new JScrollPane(list);
            add(scroll);

            list.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String word = list.getSelectedValue();
                    if (word != null) {
                        if (mainUI.isEnglishMode) mainUI.showEnglishWord(word);
                        else mainUI.showVietnameseWord(word);
                        this.dispose();
                    }
                }
            });
        }
    }
    
       /* ---------- CỬA SỔ FAVORITE ---------- */
    
    class FavoriteWordsWindow extends JFrame {

        public FavoriteWordsWindow(DictionaryUI mainUI) {
            setTitle("Favorite Words");
            setSize(400, 500);
            setLocationRelativeTo(null);

            DefaultListModel<String> model = new DefaultListModel<>();

            // Lấy các từ có favourite = true từ JSON
            controller.getDictionaryData().forEach((word, wObj) -> {
                if (wObj.isFavourite()) model.addElement(word);
            });

            JList<String> list = new JList<>(model);
            list.setFont(new Font("SansSerif", Font.PLAIN, 18));

            JScrollPane scroll = new JScrollPane(list);
            add(scroll);

            list.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String word = list.getSelectedValue();
                    if (word != null) {
                        mainUI.showEnglishWord(word);
                        this.dispose();
                    }
                }
            });
        }
    }
   
    public void refreshAfterManager() {
    searchField.setText("");
    clearDetail();
    wordLabel.setText("");

    favButton.setVisible(false);
    soundButton.setVisible(false);

    suggestionList.clearSelection();
    loadAllWords();
}

    
    
    
   
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
            g2.setColor(new Color(180, 210, 255));
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

            g2.setColor(new Color(180, 210, 255)); // border mảnh
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

            g2.setColor(new Color(180, 210, 255)); // viền xám nhẹ
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arcWidth, arcHeight);
            g2.dispose();
        }
    }
    class CustomScrollBarUI extends javax.swing.plaf.basic.BasicScrollBarUI {

    private final int THUMB_SIZE = 8;  // độ dày thanh kéo
    private final int ARC = 10;        // độ bo tròn

    @Override
    protected void configureScrollBarColors() {
        thumbColor = new Color(0, 0, 0, 40); // màu mờ mờ
        trackColor = new Color(0, 0, 0, 0);  // trong suốt
    }

    @Override
    protected Dimension getMaximumThumbSize() {
        return new Dimension(THUMB_SIZE, THUMB_SIZE);
    }

    @Override
    protected Dimension getMinimumThumbSize() {
        return new Dimension(THUMB_SIZE, THUMB_SIZE);
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle r) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color thumb = new Color(0, 0, 0, 80); // thumb đậm hơn chút khi vẽ
        g2.setColor(thumb);

        g2.fillRoundRect(r.x, r.y, r.width, r.height, ARC, ARC);

        g2.dispose();
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle r) {
        // track trong suốt hoàn toàn
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
    // ================= UPDATE LAST VIEWED =================
    private void updateLastViewed(String word) {
    if (word == null || word.isEmpty()) return;

    if (!historyWords.contains(word)) {
        historyWords.add(0, word);
    } else {
        historyWords.remove(word);
        historyWords.add(0, word);
    }

    // giới hạn 50
    if (historyWords.size() > 50) {
        historyWords.remove(historyWords.size() - 1);
    }
}


    /* =========================================================
     * ==================== CONSTRUCTOR ========================
     * ========================================================= */
    
    public DictionaryUI() {
        
         // Load dữ liệu từ JSON
         
       controller = new DataLoader("data/Vietnamese_english.json");
    
        setTitle("English ↔ Vietnamese Dictionary");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(macBg);
       
        
        
        // load lịch sử
        
        reloadHistoryFromJson();  
        
       // ---------------- TOP PANEL CLEAN VERSION ----------------

        class SideButton extends JButton {
        private final Color bgColor;
        private final int arc = 40;

        public SideButton(String text, Color bgColor) {
            super(text);
            this.bgColor = bgColor;

            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setOpaque(false);

            setFont(new Font("SansSerif", Font.BOLD, 13));
            setForeground(Color.BLACK);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            setMargin(new Insets(10, 18, 10, 18));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // draw rounded background
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            super.paintComponent(g2);
            g2.dispose();
        }
    }
        
        // =================== NEW TOP PANEL (STYLE 2) ======================
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.setBackground(macBg);
        
        // ====== TITLE LABEL (TOP CENTER) ======
        
        JLabel titleLabel = new JLabel("DICTIONARY") {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Gradient xanh Apple
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(0, 122, 255),      // xanh dương
                    0, getHeight(), new Color(52, 199, 89) // xanh lá Apple
            );

            g2.setPaint(gradient);
            g2.setFont(getFont());

            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent()) / 2 - 4;

            g2.drawString(getText(), x, y);
        }
    };

        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 38));
        titleLabel.setOpaque(false);
        titleLabel.setPreferredSize(new Dimension(1000, 50));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Add title vào topPanel
        topPanel.add(titleLabel);
        topPanel.add(Box.createVerticalStrut(10));

        // ==== ROW 1: Favorites | History | (Glue) | ModeSwitch | Manager Button ====
        JPanel row1 = new JPanel();
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        row1.setOpaque(false);

        // Favorites + History
        JPanel leftGroup = new JPanel();
        leftGroup.setLayout(new BoxLayout(leftGroup, BoxLayout.X_AXIS));
        leftGroup.setOpaque(false);

        JButton favListButton = new SideButton("❤ Favorites", new Color(255, 230, 120));
        favListButton.addActionListener(e -> new FavoriteWordsWindow(this).setVisible(true));

        JButton historyBtn = new SideButton("⏱ History", new Color(220, 220, 220));
        historyBtn.addActionListener(e -> new HistoryWindow(historyWords, this).setVisible(true));

        leftGroup.add(favListButton);
        leftGroup.add(Box.createRigidArea(new Dimension(10, 0)));
        leftGroup.add(historyBtn);

        // Add to row1
        row1.add(leftGroup);
        row1.add(Box.createHorizontalGlue());  // đẩy nhóm còn lại sang phải

        // Mode switch
        modeSwitch = new RoundedToggleButton("English → Vietnamese");
        modeSwitch.addActionListener(e -> {
            isEnglishMode = !isEnglishMode;
            modeSwitch.setText(isEnglishMode ? "English → Vietnamese" : "Vietnamese → English");

            // 1. Xóa chữ trong ô search
            searchField.setText("");

            // 2. Xóa thông tin đang hiển thị
            clearDetail();
            wordLabel.setText("");

            // 3. Ẩn các nút trái tim + nút loa
            favButton.setVisible(false);
            soundButton.setVisible(false);

            // 4. Xóa chọn trong danh sách suggestion
            suggestionList.clearSelection();

            // 5. Load lại danh sách phù hợp với mode mới
            loadAllWords();
        });

        // Manager button (right arrow)
        ManagerButton switchToManager = new ManagerButton();
        switchToManager.setPreferredSize(new Dimension(45, 40));
        switchToManager.addActionListener(e -> {
            new WordManagerUI(this).setVisible(true);
            this.setVisible(false);
        });
        switchToManager.setPreferredSize(new Dimension(40, 30));
        switchToManager.setMaximumSize(new Dimension(40, 30));
        switchToManager.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true));
        switchToManager.setBackground(new Color(230, 230, 230));
        switchToManager.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        switchToManager.addActionListener(e -> {
            new WordManagerUI(this).setVisible(true);
            this.setVisible(false);
        });

        // Add mode switch + manager btn
        row1.add(modeSwitch);
        row1.add(Box.createRigidArea(new Dimension(10, 0)));
        row1.add(switchToManager);

        // ==== ROW 2: SEARCH BAR CENTERED ====
        JPanel row2 = new JPanel();
        row2.setLayout(new BoxLayout(row2, BoxLayout.X_AXIS));
        row2.setOpaque(false);
        row2.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        // dùng SearchBar (có icon)
        SearchBar searchBar = new SearchBar();
        searchField = searchBar.field;   // lấy ô nhập trong SearchBar

        row2.add(Box.createHorizontalGlue());
        row2.add(searchBar);             // ✔ add searchBar có icon
        row2.add(Box.createHorizontalGlue());

        // Add rows to top panel
        topPanel.add(row1);
        topPanel.add(row2);

        // Add to frame
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
        scrollList.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollList.getHorizontalScrollBar().setUI(new CustomScrollBarUI());
        scrollList.getVerticalScrollBar().setOpaque(false);
        scrollList.getHorizontalScrollBar().setOpaque(false);
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

        // 1. TẠO WORD LABEL (TỪ VỰNG TO Ở TRÊN CÙNG)
        wordLabel = new JLabel("");
        wordLabel.setFont(new Font("SansSerif", Font.BOLD, 28)); // Font to (size 24)
        wordLabel.setForeground(macBlue); // Màu xanh
        wordLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn trái
  
        // --- tạo panel chứa từ + nút trái tim ---
        favButton = new JButton("♡");
        favButton.setFont(new Font("SansSerif", Font.BOLD, 26));
        favButton.setForeground(Color.GRAY);
        favButton.setContentAreaFilled(false);
        favButton.setBorderPainted(false);
        favButton.setFocusPainted(false);
        favButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        favButton.setVisible(false);
        
        // gắn listener để nút hoạt động
        favButton.addActionListener(e -> {
    String current = wordLabel.getText();
    if (current == null || current.isEmpty()) return;

    if (isEnglishMode) {
        // ENGLISH MODE — current là từ tiếng Anh
        WordEnglish w = controller.getDictionaryData().get(current);
        if (w == null) return;

        boolean newValue = !w.isFavourite();
        w.setFavourite(newValue);

        favButton.setText(newValue ? "❤" : "♡");
        favButton.setForeground(newValue ? Color.RED : Color.GRAY);
    } 
    else {
        // VIETNAMESE MODE — current là tiếng Việt
        var engList = controller.getVietnameseToEnglishMap().get(current);
        if (engList == null) return;

        // Tìm xem có từ tiếng Anh nào favorite chưa
        boolean currentlyFavorite = engList.stream()
                .map(eng -> controller.getDictionaryData().get(eng))
                .anyMatch(w -> w != null && w.isFavourite());

        // Đảo trạng thái cho toàn bộ từ tiếng Anh
        boolean newValue = !currentlyFavorite;

        for (String eng : engList) {
            WordEnglish w = controller.getDictionaryData().get(eng);
            if (w != null) w.setFavourite(newValue);
        }

        favButton.setText(newValue ? "❤" : "♡");
        favButton.setForeground(newValue ? Color.RED : Color.GRAY);
    }
    
        controller.saveDataToJson("Vietnamese_english.json");
});

        // PANEL GỘP
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setOpaque(false);
        
        // NGĂN PANEL BỊ GIÃN → KHÔNG CHE NÚT TRÁI TIM
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Chỉ add MỘT LẦN và add trật tự đúng
        titlePanel.add(wordLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        titlePanel.add(favButton);
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Thêm vào detail đúng chỗ và đúng 1 lần
        detail.add(titlePanel);
        typeLabel = new JLabel("Type: ");
        typeLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        phoneticLabel = new JLabel("Phonetic: ");
        phoneticLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        phoneticLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        // NÚT LOA — ban đầu ẩn
        soundButton = new JButton("🔊");
        soundButton.setFont(new Font("SansSerif", Font.PLAIN, 20));
        soundButton.setFocusPainted(false);
        soundButton.setContentAreaFilled(false);
        soundButton.setBorderPainted(false);
        soundButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        soundButton.setVisible(false);

        // Khi nhấn nút loa → đọc từ
        soundButton.addActionListener(e -> {
            String word = wordLabel.getText();
            if (word != null && !word.isEmpty()) {
                org.example.ulti.TTS.init();
                org.example.ulti.TTS.speak(word);
            }
        });

        CardComponent meaningCard = createCard("Meaning");
        CardComponent exampleCard = createCard("Example");

        meaningArea = meaningCard.area;
        exampleArea = exampleCard.area;

        meaningCard.panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        exampleCard.panel.setAlignmentX(Component.LEFT_ALIGNMENT);

       
        detail.add(typeLabel);
        detail.add(Box.createVerticalStrut(5));
        JPanel phoneticPanel = new JPanel();
        phoneticPanel.setLayout(new BoxLayout(phoneticPanel, BoxLayout.X_AXIS));
        phoneticPanel.setOpaque(false);
        phoneticPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        phoneticPanel.add(phoneticLabel);
        phoneticPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        phoneticPanel.add(soundButton);

        // Thêm vào detail
        detail.add(phoneticPanel);
        detail.add(Box.createVerticalStrut(10));
        detail.add(meaningCard.panel);
        detail.add(Box.createVerticalStrut(15));
        detail.add(exampleCard.panel);
        
        add(detail, BorderLayout.CENTER);
        // Load list ngay khi mở app
        loadAllWords();
        reloadHistoryFromJson();

        // EVENTS
        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) { updateSuggestions(); }
        });

        suggestionList.addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {

        String word = suggestionList.getSelectedValue();
        if (word == null) return;

        // Nếu đang KHÔNG search và click lại đúng từ đó → reset giao diện
        if (searchField.getText().trim().isEmpty()) {

            if (word.equals(lastSelectedWord)) {
                clearDetail();
                wordLabel.setText("");
                favButton.setVisible(false);
                soundButton.setVisible(false);
                lastSelectedWord = null;
                return;
            }
        }

        // Ngược lại → load từ như bình thường
        lastSelectedWord = word;

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

    // ⛔ STOP — nếu event KHÔNG phải do bàn phím
    if (!searchField.isFocusOwner()) return;

    String text = searchField.getText().trim().toLowerCase();
    listModel.clear();

    // Khi Ô SEARCH RỖNG → reset giao diện
    if (text.isEmpty()) {

        // ➤ XÓA PHẦN CHI TIẾT BÊN PHẢI
        clearDetail();
        wordLabel.setText("");
        favButton.setVisible(false);
        soundButton.setVisible(false);

        // ➤ LOAD LẠI DANH SÁCH THEO MODE
        if (isEnglishMode) {
            controller.getEnglishTrie()
                    .getAllWords()
                    .forEach(listModel::addElement);
        } else {
            controller.getVietnameseTrie()
                    .getAllWords()
                    .forEach(listModel::addElement);
        }

        return;
    }

    // Khi có chữ → search theo prefix
    List<String> result = isEnglishMode
            ? controller.getEnglishTrie().searchByPrefix(text)
            : controller.getVietnameseTrie().searchByPrefix(text);

    result.forEach(listModel::addElement);
}

    public void loadAllWords() {
    listModel.clear();

    if (isEnglishMode) {
        // sort English → Vietnamese
        Map<String, WordEnglish> sorted =
                sorter.sortEnToVn(controller.getDictionaryData());

        sorted.keySet().forEach(listModel::addElement);

    } else {
        // sort Vietnamese → English
        Map<String, List<String>> sorted =
                sorter.sortVnToEn(controller.getVietnameseToEnglishMap());

        sorted.keySet().forEach(listModel::addElement);
    }
}
    private void reloadHistoryFromJson() {
    historyWords.clear();

    Map<String, WordEnglish> data = controller.getDictionaryData();

    // dùng MergeSortKeyMap
    Map<String, WordEnglish> sorted =
            sorter.sortByLastViewedAt(data);

    sorted.forEach((eng, w) -> {
        if (w.getLastViewedAt() != null && !w.getLastViewedAt().isEmpty()) {
            historyWords.add(eng);
        }
    });
}

    //  WORD DISPLAY
    private void showEnglishWord(String word) {
        WordEnglish w = controller.getDictionaryData().get(word);   
        if (w == null) return;
        
        w.updateLastViewedNow();
        controller.saveDataToJson("Vietnamese_english.json");  // 🔥 lưu file
        updateLastViewed(word);

        wordLabel.setText(word);
        typeLabel.setText("Type: " + w.getType());
        phoneticLabel.setText("Phonetic: " + w.getTranscription());
        meaningArea.setText(w.getTextVietnamese());
        exampleArea.setText(w.getExample());
        favButton.setVisible(true);
        soundButton.setVisible(true);
        favButton.setText(w.isFavourite() ? "❤" : "♡");
        favButton.setForeground(w.isFavourite() ? Color.RED : Color.GRAY);
    }

    private void showVietnameseWord(String vn) {
        clearDetail();
        wordLabel.setText(vn);
        var list = controller.getVietnameseToEnglishMap().get(vn);
        if (list == null) return;
         
// ⭐ CẬP NHẬT LAST VIEWED CHO MỌI TỪ TIẾNG ANH ⭐
        String firstEng = list.get(0);
        WordEnglish w0 = controller.getDictionaryData().get(firstEng);

        if (w0 != null) 
            w0.updateLastViewedNow();
            controller.saveDataToJson("Vietnamese_english.json"); // 🔥 SAVE
            updateLastViewed(firstEng);

        // Dùng Set để lưu trữ các Loại từ (Type) và Phiên âm (Phonetic) duy nhất
        Set<String> uniqueTypes = new LinkedHashSet<>();
        Set<String> uniquePhonetics = new LinkedHashSet<>(); // <--- THÊM SET NÀY

        StringBuilder meaningBuilder = new StringBuilder();
        StringBuilder exampleBuilder = new StringBuilder();

        //  Duyệt danh sách để chia thông tin Types & Phonetics
        for (int i = 0; i < list.size(); i++) {
            String engWord = list.get(i);
            WordEnglish w = controller.getDictionaryData().get(engWord);

            if (w != null) {
                // Loại từ
                String type = w.getType().toUpperCase();
                if (!type.isEmpty())
                    uniqueTypes.add(type);

                // Phiên âm duy
                String phonetic = w.getTranscription();
                if (!phonetic.isEmpty())
                    uniquePhonetics.add(phonetic);
            }

            meaningBuilder.append("• ").append(engWord).append("\n");

            if (!w.getExample().isEmpty())
            {
                exampleBuilder.append("• ").append(engWord).append(": ");
                exampleBuilder.append(w.getExample()).append("\n");
            }
          
        }

        // 4. Cập nhật Type Label theo định dạng DẤU PHẨY
        String aggregatedTypes = String.join(", ", uniqueTypes);
        typeLabel.setText("Type: " + aggregatedTypes);

        // 5. Cập nhật Phonetic Label theo định dạng DẤU PHẨY
        String aggregatedPhonetics = String.join(", ", uniquePhonetics);
        phoneticLabel.setText("Phonetic: " + aggregatedPhonetics);

        // 6. Đổ dữ liệu vào 2 ô Text Area
        meaningArea.setText(meaningBuilder.toString());
        exampleArea.setText(exampleBuilder.toString());

        meaningArea.setCaretPosition(0);
        exampleArea.setCaretPosition(0);
        
        favButton.setVisible(true);
        // Lấy English words ứng với nghĩa tiếng Việt
        var engList = controller.getVietnameseToEnglishMap().get(vn);

        boolean isFav = false;
        if (engList != null) {
            for (String eng : engList) {
                WordEnglish w = controller.getDictionaryData().get(eng);
                if (w != null && w.isFavourite()) {
                    isFav = true;
                    break;
                }
            }
        }

        // Hiển thị nút trái tim
        favButton.setVisible(true);
        favButton.setText(isFav ? "❤" : "♡");
        favButton.setForeground(isFav ? Color.RED : Color.GRAY);
    }
    
     /* ================================================== */
    /* ================== UTILITY ======================= */
    /* ================================================== */
    
    private void clearDetail() {
        if (typeLabel != null) typeLabel.setText("Type: ");
        if (phoneticLabel != null) phoneticLabel.setText("Phonetic: ");
        if (meaningArea != null) meaningArea.setText("");
        if (exampleArea != null) exampleArea.setText("");
        }
    
    public void reloadDataFromJson() {
    controller.reloadFromJson();

    // Reset UI để dùng dữ liệu mới
    listModel.clear();
    loadAllWords();

    // Reset listeners nếu cần
    suggestionList.clearSelection();
    clearDetail();

    reloadHistoryFromJson();
}
    public DataLoader getController() {
    return controller;
}

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

}

// Variables declaration - do not modify                     
// End of variables declaration