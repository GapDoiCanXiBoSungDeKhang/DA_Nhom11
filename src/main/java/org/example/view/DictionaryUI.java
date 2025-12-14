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

        // DÙNG HÀM loadDataFavourite TỪ DATALOADER
        List<String> favWords =
                controller.loadDataFavourite(controller.getDictionaryData());

        favWords.forEach(model::addElement);

        JList<String> list = new JList<>(model);
        list.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JScrollPane scroll = new JScrollPane(list);
        add(scroll);

        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String word = list.getSelectedValue();
                if (word != null) {
                    mainUI.showEnglishWord(word);
                    dispose();
                }
            }
        });
    }
}
    
    /* ---------- PANEL BO GÓC ---------- */
    
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
    
    /* ---------- CARD ---------- */
    
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
    
    /* =====================================================
    * ============== PUBLIC METHOD (REFRESH UI) ===========
    * ===================================================== */

   /**
    * Được gọi khi quay lại từ WordManagerUI
    * → reset giao diện và load lại danh sách từ
    */

       public void refreshAfterManager() {
           
       searchField.setText("");   // Xóa nội dung ô search
       clearDetail();             // Xóa phần chi tiết
       wordLabel.setText("");     // Xóa từ đang hiển thị

       favButton.setVisible(false);    // Ẩn nút yêu thích
       soundButton.setVisible(false);  // Ẩn nút loa

       suggestionList.clearSelection(); // Bỏ chọn danh sách
       loadAllWords();                  // Load lại toàn bộ từ
    }

    /* =====================================================
    * ============== CUSTOM TOGGLE BUTTON =================
    * ===================================================== */

   /**
    * Nút toggle bo tròn (English ↔ Vietnamese)
    * Có hover / pressed / selected effect
    */
       
    public class RoundedToggleButton extends JToggleButton {
        
        private int arc = 40;                // bán kính bo tròn góc
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

            initListeners();  // gắn hover / press
        }

    /**
    * Listener để bắt hover và press
    */
        private void initListeners() {
            addMouseListener(new java.awt.event.MouseAdapter() {
                
                public void mouseEntered(java.awt.event.MouseEvent e) { hover = true; repaint(); }
                public void mouseExited(java.awt.event.MouseEvent e)  { hover = false; repaint(); }
                public void mousePressed(java.awt.event.MouseEvent e) { pressed = true; repaint(); }
                public void mouseReleased(java.awt.event.MouseEvent e){ pressed = false; repaint(); }
            });
        }

    /**
    * Vẽ nút bo tròn + màu theo trạng thái
    */
        @Override
        protected void paintComponent(Graphics g) {
            
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // choose color depending on state
            Color fill;
            // màu khi bật (selected)
            
            Color green = new Color(52, 199, 89);           // xanh lá
            Color greenHover = new Color(48, 180, 80);
            Color greenPressed = new Color(40, 160, 70);

            // màu khi tắt (normal)
            
            Color blue = new Color(0, 122, 255); // xanh dương
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
            
            // Vẽ nền bo tròn
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            // Viền nhẹ
            g2.setColor(new Color(200,200,200,80));
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, arc, arc);

            // Vẽ text giữa nút
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
    /* =====================================================
    * ================== BORDER BO TRÒN ===================
    * ===================================================== */

   /**
    * Border bo góc dùng cho component
    */
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
    
    /* =====================================================
    * ================= TEXT FIELD BO GÓC =================
    * ===================================================== */
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
    
    /* =====================================================
    * ================= CUSTOM SCROLL BAR =================
    * ===================================================== */
    
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
    
   /* =====================================================
    * ================= UPDATE HISTORY ====================
    * ===================================================== */

   /**
    * Cập nhật danh sách lịch sử (tối đa 50 từ)
    */
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
        
        /* ================= LOAD DATA ================= */
        
        // Load dữ liệu từ JSON 
        controller = new DataLoader("data/Vietnamese_english.json");
        
        /* ================= SETUP FRAME ================= */
    
        setTitle("English ↔ Vietnamese Dictionary"); // Tiêu đề cửa sổ
        setSize(1000, 650);                          // Kích thước cửa sổ
        setLocationRelativeTo(null);                 // Canh giữa màn hình
        setDefaultCloseOperation(EXIT_ON_CLOSE);     // Đóng app khi tắt cửa sổ
        setLayout(new BorderLayout());               // Layout chính
        getContentPane().setBackground(macBg);       // Màu nền

        /* ================= LOAD HISTORY ================= */
        
        // load lịch sử
        reloadHistoryFromJson();  
        
       
        /* =================================================
         * =============== TOP PANEL =======================
         * ================================================= */

        // Nút phụ dùng cho Favorites / History
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
        
        /* ================= TOP PANEL ================= */
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        topPanel.setBackground(macBg);
        
        /* ================= TITLE ================= */
        
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
                    0, getHeight(), new Color(52, 199, 89) // xanh lá 
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
    
        /* =================================================
        * =============== ROW 1 ============================
        * ================================================= */

        // ==== ROW 1: Favorites | History | (Glue) | ModeSwitch | Manager Button ====
        JPanel row1 = new JPanel();
        row1.setLayout(new BoxLayout(row1, BoxLayout.X_AXIS));
        row1.setOpaque(false);

        // Favorites + History
        JPanel leftGroup = new JPanel();
        leftGroup.setLayout(new BoxLayout(leftGroup, BoxLayout.X_AXIS));
        leftGroup.setOpaque(false);

        JButton favListButton = new SideButton("❤ Favorites", new Color(255, 230, 120));
        favListButton.addActionListener(e -> 
                new FavoriteWordsWindow(this).setVisible(true)
            );

        JButton historyBtn = new SideButton("⏱ History", new Color(220, 220, 220));
        historyBtn.addActionListener(e -> 
                new HistoryWindow(historyWords, this).setVisible(true)
            );

        leftGroup.add(favListButton);
        leftGroup.add(Box.createRigidArea(new Dimension(10, 0)));
        leftGroup.add(historyBtn);

        // Add to row1
        row1.add(leftGroup);
        row1.add(Box.createHorizontalGlue());  // đẩy nhóm còn lại sang phải

        /* ================= MODE SWITCH ================= */
        
        modeSwitch = new RoundedToggleButton("English → Vietnamese");
        modeSwitch.addActionListener(e -> {
            
            // Đảo chế độ EN ↔ VN
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

        /* ================= MANAGER BUTTON ================= */
        
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

       /* ================= ROW 2 – SEARCH ================= */
       
        JPanel row2 = new JPanel();
        row2.setLayout(new BoxLayout(row2, BoxLayout.X_AXIS));
        row2.setOpaque(false);
        row2.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        // dùng SearchBar (có icon)
        SearchBar searchBar = new SearchBar();
        searchField = searchBar.field;   // lấy ô nhập trong SearchBar

        row2.add(Box.createHorizontalGlue());
        row2.add(searchBar);             // add searchBar có icon
        row2.add(Box.createHorizontalGlue());

        // Add rows to top panel
        topPanel.add(row1);
        topPanel.add(row2);

        // Add to frame
        add(topPanel, BorderLayout.NORTH);
        
        /* ==================================================
         * =============== SUGGESTION LIST ==================
         * ================================================== */
        
        // Model lưu danh sách từ
        listModel = new DefaultListModel<>();
        
        // JList hiển thị danh sách gợi ý
        suggestionList = new JList<>(listModel);
        suggestionList.setFont(new Font("SansSerif", Font.PLAIN, 16));   // font chữ
        suggestionList.setSelectionBackground(macLightBlue);             // màu khi chọn
        suggestionList.setFixedCellHeight(32);                           // chiều cao mỗi dòng
        suggestionList.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // padding trong list
        
        /* ================= SCROLL PANE ================= */
        
        // Scroll cho list (bo góc + custom scrollbar)
        JScrollPane scrollList = new JScrollPane(suggestionList);
        scrollList.getVerticalScrollBar().setUI(new CustomScrollBarUI());
        scrollList.getHorizontalScrollBar().setUI(new CustomScrollBarUI());
        
        // Làm scrollbar + viewport trong suốt
        scrollList.getVerticalScrollBar().setOpaque(false);
        scrollList.getHorizontalScrollBar().setOpaque(false);
        scrollList.getViewport().setOpaque(false);
        scrollList.setOpaque(false);
        
        // Margin phía trên
        scrollList.setBorder(BorderFactory.createEmptyBorder(8,0,0,0));
        
        /* ================= CARD BỌC LIST ================= */
        
        // Panel bo góc cho danh sách
        RoundedListPanel listCard = new RoundedListPanel();
        listCard.add(scrollList, BorderLayout.CENTER);
        listCard.setPreferredSize(new Dimension(260, 0));
        
        /* ================= WRAPPER BÊN TRÁI ================= */

        // Panel bọc để tạo khoảng cách với mép trái
        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setBackground(macBg);
        leftWrapper.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 0));
        leftWrapper.add(listCard, BorderLayout.CENTER);
        
        // Gắn vào giao diện chính
        add(leftWrapper, BorderLayout.WEST);

        /* ==================================================
        * =============== DETAILS PANEL ====================
        * ================================================== */
        
        JPanel detail = new JPanel();
        detail.setLayout(new BoxLayout(detail, BoxLayout.Y_AXIS));
        detail.setBackground(macBg);
        detail.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        detail.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        
        /* ================= WORD LABEL ================= */

        // Nhãn hiển thị từ vựng lớn
        wordLabel = new JLabel("");
        wordLabel.setFont(new Font("SansSerif", Font.BOLD, 28)); // Font to (size 24)
        wordLabel.setForeground(macBlue); // Màu xanh
        wordLabel.setAlignmentX(Component.LEFT_ALIGNMENT); // Căn trái
  
        /* ================= FAVORITE BUTTON ================= */
        
        favButton = new JButton("♡");
        favButton.setFont(new Font("SansSerif", Font.BOLD, 26));
        favButton.setForeground(Color.GRAY);
        favButton.setContentAreaFilled(false);
        favButton.setBorderPainted(false);
        favButton.setFocusPainted(false);
        favButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        favButton.setVisible(false);
        
        /* ================= FAVORITE EVENT ================= */
        
        favButton.addActionListener(e -> {
        String current = wordLabel.getText();
        if (current == null || current.isEmpty()) return;
        
        // ===== ENGLISH MODE =====
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
            // VIETNAMESE MODE
            var engList = controller.getVietnameseToEnglishMap().get(current);
            if (engList == null) return;

            // Tìm xem có từ tiếng Anh nào favorite chưa
            boolean currentlyFavorite = engList.stream()
                    .map(eng -> controller.getDictionaryData().get(eng))
                    .anyMatch(w -> w != null && w.isFavourite());

            // Đảo trạng thái cho toàn bộ từ tiếng Anh
            boolean newValue = !currentlyFavorite;
            
            // Cập nhật favorite cho toàn bộ từ tiếng Anh
            for (String eng : engList) {
                WordEnglish w = controller.getDictionaryData().get(eng);
                if (w != null) w.setFavourite(newValue);
            }

            favButton.setText(newValue ? "❤" : "♡");
            favButton.setForeground(newValue ? Color.RED : Color.GRAY);
        }
        
            // Lưu lại JSON
            controller.saveDataToJson("Vietnamese_english.json");
        });
        
        /* ================= TITLE PANEL ================= */
   
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setOpaque(false);
        
        // Ngăn panel bị giãn cao
        titlePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        // Thêm từ + nút tim
        titlePanel.add(wordLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        titlePanel.add(favButton);
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Thêm vào detail 
        detail.add(titlePanel);
        
        /* ================= TYPE LABEL ================= */
        
        typeLabel = new JLabel("Type: ");
        typeLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        /* ================= PHONETIC LABEL ================= */

        phoneticLabel = new JLabel("Phonetic: ");
        phoneticLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        phoneticLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        /* ================= SOUND BUTTON ================= */
        
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
        
        /* ==================================================
         * =============== MEANING & EXAMPLE CARD ===========
         * ================================================== */

        // Tạo card hiển thị nghĩa
        CardComponent meaningCard = createCard("Meaning");
        
        // Tạo card hiển thị ví dụ
        CardComponent exampleCard = createCard("Example");
        
        // Lấy JTextArea bên trong card
        meaningArea = meaningCard.area;
        exampleArea = exampleCard.area;
        
        // Canh trái toàn bộ card
        meaningCard.panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        exampleCard.panel.setAlignmentX(Component.LEFT_ALIGNMENT);

       /* ==================================================
        * =============== TYPE + PHONETIC ==================
        * ================================================== */
       
        // Thêm Type label
        detail.add(typeLabel);
        
        // Khoảng cách nhỏ dưới Type
        detail.add(Box.createVerticalStrut(5));
        
        /* ================= PHONETIC PANEL ================= */
        
        // Panel chứa phonetic + nút loa (nằm ngang)
        JPanel phoneticPanel = new JPanel();
        phoneticPanel.setLayout(new BoxLayout(phoneticPanel, BoxLayout.X_AXIS));
        phoneticPanel.setOpaque(false);
        phoneticPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Thêm label phiên âm
        phoneticPanel.add(phoneticLabel);
        
        // Khoảng cách giữa phiên âm và nút loa
        phoneticPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        
        // Thêm nút loa
        phoneticPanel.add(soundButton);

        // Gắn panel phiên âm vào detail
        detail.add(phoneticPanel);
        
        // Khoảng cách dưới phonetic
        detail.add(Box.createVerticalStrut(10));
        
        /* ==================================================
        * =============== ADD CONTENT CARDS ================
        * ================================================== */
        
        // Thêm card Meaning
        detail.add(meaningCard.panel);
        
        // Khoảng cách giữa 2 card
        detail.add(Box.createVerticalStrut(15));
        
        // Thêm card Example
        detail.add(exampleCard.panel);
        
        // Gắn detail panel vào CENTER
        add(detail, BorderLayout.CENTER);
        
        /* ==================================================
        * =============== INITIAL LOAD =====================
        * ================================================== */
        
        // Load danh sách từ
        loadAllWords();
        
        // Load lịch sử đã xem
        reloadHistoryFromJson();

        
        /* =================================================
         * =============== EVENTS ===========================
         * ================================================= */
        
        // Gõ phím trong ô search → cập nhật danh sách gợi ý
        searchField.addKeyListener(new KeyAdapter() {
            @Override 
            public void keyReleased(KeyEvent e) { 
                updateSuggestions(); 
            }
        });

        // Click chọn từ trong danh sách
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

        /* ==================================================
      * =============== CREATE CARD ======================
      * ================================================== */
    private CardComponent createCard(String title) {
        
        // Panel bo góc
        RoundedCardPanel card = new RoundedCardPanel();
        
        // Tiêu đề card
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
          /* ================= TEXT AREA ================= */

        // JTextArea phải opaque, có margin nội bộ để text không sát mép
        JTextArea area = new JTextArea();
        area.setEditable(false);        // không cho sửa
        area.setFont(new Font("SansSerif", Font.PLAIN, 16));
        area.setLineWrap(true);          // xuống dòng tự động
        area.setWrapStyleWord(true);     // ngắt theo từ
        area.setOpaque(true);                          // cho nền trắng
        area.setBackground(new Color(255,255,255));    // màu nền
        area.setMargin(new Insets(10, 12, 10, 12));    // padding trong
        area.setBorder(BorderFactory.createEmptyBorder()); // không cần border của textArea

         /* ================= SCROLL ================= */
         
        // Bọc vào JScrollPane (dễ scroll nếu nội dung nhiều)
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createEmptyBorder()); // xóa border của scrollpane
        
        // đảm bảo scrollpane cũng trong suốt để thấy rounded card nền
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        // Gắn title + text vào card
        card.add(lblTitle, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        
        // Trả về card + text area
        return new CardComponent(card, area);
    }
    
    /* ==================================================
     * ================== LOGIC CHÍNH ===================
     * ================================================== */
    
    /* ================= SEARCH UPDATE ================= */
    
    // Hàm cập nhật danh sách gợi ý khi người dùng gõ search
    private void updateSuggestions() {

    // STOP — nếu sự kiện KHÔNG phải do người dùng gõ bàn phím
    if (!searchField.isFocusOwner()) return;
    
    // Lấy nội dung search, chuẩn hóa về chữ thường
    String text = searchField.getText().trim().toLowerCase();
    
      // Xóa danh sách cũ
    listModel.clear();

    /* ===== SEARCH RỖNG → RESET GIAO DIỆN ===== */
    if (text.isEmpty()) {

        // Xóa phần hiển thị chi tiết bên phải
        clearDetail();
        wordLabel.setText("");
        favButton.setVisible(false);
        soundButton.setVisible(false);

        // Load lại toàn bộ danh sách theo mode
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

    /* ===== CÓ NỘI DUNG → SEARCH PREFIX ===== */
    List<String> result = isEnglishMode
            ? controller.getEnglishTrie().searchByPrefix(text)
            : controller.getVietnameseTrie().searchByPrefix(text);
    
    // Đổ kết quả vào list
    result.forEach(listModel::addElement);
}
    /* ================= LOAD ALL WORDS ================= */
    
    public void loadAllWords() {
        
    // Xóa danh sách hiện tại
    listModel.clear();

    if (isEnglishMode) {
        
        // Sắp xếp English → Vietnamese
        Map<String, WordEnglish> sorted =
                sorter.sortEnToVn(controller.getDictionaryData());

        sorted.keySet().forEach(listModel::addElement);

    } else {
        
        // Sắp xếp Vietnamese → English
        Map<String, List<String>> sorted =
                sorter.sortVnToEn(controller.getVietnameseToEnglishMap());

        sorted.keySet().forEach(listModel::addElement);
    }
}
    
    /* ================= LOAD HISTORY ================= */
    
    private void reloadHistoryFromJson() {
        
    // Xóa lịch sử cũ trong bộ nhớ
    historyWords.clear();

    // Lấy toàn bộ dữ liệu
    Map<String, WordEnglish> data = controller.getDictionaryData();

    // Sắp xếp theo lastViewedAt (mới → cũ)
    Map<String, WordEnglish> sorted =
            sorter.sortByLastViewedAt(data);
    
    // Chỉ thêm các từ có lastViewedAt
    sorted.forEach((eng, w) -> {
        if (w.getLastViewedAt() != null && !w.getLastViewedAt().isEmpty()) {
            historyWords.add(eng);
        }
    });
}

   /* ================= DISPLAY ENGLISH WORD ================= */
    
    private void showEnglishWord(String word) {
        
        // Lấy WordEnglish từ Map
        WordEnglish w = controller.getDictionaryData().get(word);   
        if (w == null) return;
        
        // Cập nhật thời gian xem gần nhất
        w.updateLastViewedNow();
        
        // Lưu JSON
        controller.saveDataToJson("Vietnamese_english.json");
        
        // Cập nhật lịch sử
        updateLastViewed(word);
        
        // Hiển thị dữ liệu
        wordLabel.setText(word);
        typeLabel.setText("Type: " + w.getType());
        phoneticLabel.setText("Phonetic: " + w.getTranscription());
        meaningArea.setText(w.getTextVietnamese());
        exampleArea.setText(w.getExample());
        
        // Hiện nút chức năng
        favButton.setVisible(true);
        soundButton.setVisible(true);
        
        // Cập nhật trạng thái favorite
        favButton.setText(w.isFavourite() ? "❤" : "♡");
        favButton.setForeground(w.isFavourite() ? Color.RED : Color.GRAY);
    }

    /* ================= DISPLAY VIETNAMESE WORD ================= */
    
    private void showVietnameseWord(String vn) {
        
        // Xóa dữ liệu cũ
        clearDetail();
        wordLabel.setText(vn);
        
        // Lấy danh sách từ tiếng Anh tương ứng
        var list = controller.getVietnameseToEnglishMap().get(vn);
        if (list == null) return;
         
        /* ===== CẬP NHẬT LAST VIEWED ===== */
        String firstEng = list.get(0);
        WordEnglish w0 = controller.getDictionaryData().get(firstEng);

        if (w0 != null) 
            w0.updateLastViewedNow();
            controller.saveDataToJson("Vietnamese_english.json");
            updateLastViewed(firstEng);

         /* ===== GOM TYPE & PHONETIC ===== */
         
        Set<String> uniqueTypes = new LinkedHashSet<>();
        Set<String> uniquePhonetics = new LinkedHashSet<>(); 

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

        // Cập nhật Type Label theo định dạng DẤU PHẨY
        String aggregatedTypes = String.join(", ", uniqueTypes);
        typeLabel.setText("Type: " + aggregatedTypes);

        // Cập nhật Phonetic Label theo định dạng DẤU PHẨY
        String aggregatedPhonetics = String.join(", ", uniquePhonetics);
        phoneticLabel.setText("Phonetic: " + aggregatedPhonetics);

        // 6. Đổ dữ liệu vào 2 ô Text Area
        meaningArea.setText(meaningBuilder.toString());
        exampleArea.setText(exampleBuilder.toString());
        
        // Hiển thị Meaning & Example
        meaningArea.setCaretPosition(0);
        exampleArea.setCaretPosition(0);
        
        // Lấy English words ứng với nghĩa tiếng Việt
        var engList = controller.getVietnameseToEnglishMap().get(vn);
        
        /* ===== XỬ LÝ FAVORITE ===== */
        
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
    
    // Xóa dữ liệu hiển thị chi tiết
    private void clearDetail() {
        if (typeLabel != null) typeLabel.setText("Type: ");
        if (phoneticLabel != null) phoneticLabel.setText("Phonetic: ");
        if (meaningArea != null) meaningArea.setText("");
        if (exampleArea != null) exampleArea.setText("");
    }
    
    // Reload toàn bộ dữ liệu từ JSON
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
    // Getter controller
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