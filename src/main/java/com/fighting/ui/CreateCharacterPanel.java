package com.fighting.ui;

import com.fighting.model.GameCharacter;
import com.fighting.util.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * 创建角色面板 - 属性分配
 */
public class CreateCharacterPanel extends JPanel {

    private final MainFrame mainFrame;
    private JTextField nameField;
    private JSlider hpSlider;
    private JSlider atkSlider;
    private JSlider defSlider;
    private JLabel remainingLabel;
    private JLabel hpPreview;
    private JLabel atkPreview;
    private JLabel defPreview;

    private static final int TOTAL_POINTS = GameCharacter.TOTAL_POINTS;

    public CreateCharacterPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Constants.COLOR_BACKGROUND);
        initUI();
    }

    private void initUI() {
        // 标题
        JLabel title = new JLabel("创建角色", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 48));
        title.setForeground(Constants.COLOR_TEXT);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // 中间：属性分配面板
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 角色名
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel nameLabel = createLabel("角色名:");
        centerPanel.add(nameLabel, gbc);

        gbc.gridx = 1; gbc.gridwidth = 3;
        nameField = new JTextField("勇者", 20);
        nameField.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        nameField.setBackground(new Color(50, 50, 70));
        nameField.setForeground(Constants.COLOR_TEXT);
        nameField.setCaretColor(Constants.COLOR_TEXT);
        nameField.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 140), 2));
        centerPanel.add(nameField, gbc);

        // 可分配点数
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4;
        remainingLabel = createLabel("可分配点数: " + TOTAL_POINTS);
        remainingLabel.setFont(new Font("微软雅黑", Font.BOLD, 22));
        remainingLabel.setForeground(new Color(255, 215, 0));
        centerPanel.add(remainingLabel, gbc);

        // HP分配
        gbc.gridy = 2; gbc.gridwidth = 1;
        gbc.gridx = 0;
        centerPanel.add(createLabel("生命(HP):"), gbc);

        gbc.gridx = 1;
        hpSlider = createSlider();
        centerPanel.add(hpSlider, gbc);

        gbc.gridx = 2;
        hpPreview = createLabel("100");
        centerPanel.add(hpPreview, gbc);

        gbc.gridx = 3;
        centerPanel.add(createLabel("(基础100 + 点数x10)"), gbc);

        // ATK分配
        gbc.gridy = 3;
        gbc.gridx = 0;
        centerPanel.add(createLabel("攻击(ATK):"), gbc);

        gbc.gridx = 1;
        atkSlider = createSlider();
        centerPanel.add(atkSlider, gbc);

        gbc.gridx = 2;
        atkPreview = createLabel("10");
        centerPanel.add(atkPreview, gbc);

        gbc.gridx = 3;
        centerPanel.add(createLabel("(基础10 + 点数x2)"), gbc);

        // DEF分配
        gbc.gridy = 4;
        gbc.gridx = 0;
        centerPanel.add(createLabel("防御(DEF):"), gbc);

        gbc.gridx = 1;
        defSlider = createSlider();
        centerPanel.add(defSlider, gbc);

        gbc.gridx = 2;
        defPreview = createLabel("0");
        centerPanel.add(defPreview, gbc);

        gbc.gridx = 3;
        centerPanel.add(createLabel("(基础0 + 点数x1)"), gbc);

        // 监听滑块变化
        javax.swing.event.ChangeListener listener = e -> updatePreviews();
        hpSlider.addChangeListener(listener);
        atkSlider.addChangeListener(listener);
        defSlider.addChangeListener(listener);

        add(centerPanel, BorderLayout.CENTER);

        // 底部：按钮
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        bottomPanel.setOpaque(false);

        JButton confirmBtn = createButton("确认创建");
        confirmBtn.addActionListener(e -> onCreateCharacter());

        JButton backBtn = createButton("返回菜单");
        backBtn.addActionListener(e -> mainFrame.getGameEngine().changeState(com.fighting.core.GameState.MENU));

        bottomPanel.add(confirmBtn);
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        updatePreviews();
    }

    /**
     * 更新预览
     */
    private void updatePreviews() {
        int hpPts = hpSlider.getValue();
        int atkPts = atkSlider.getValue();
        int defPts = defSlider.getValue();
        int total = hpPts + atkPts + defPts;
        int remaining = TOTAL_POINTS - total;

        remainingLabel.setText("可分配点数: " + remaining);

        if (remaining < 0) {
            remainingLabel.setForeground(Color.RED);
        } else {
            remainingLabel.setForeground(new Color(255, 215, 0));
        }

        hpPreview.setText(String.valueOf(GameCharacter.BASE_HP + hpPts * GameCharacter.HP_PER_POINT));
        atkPreview.setText(String.valueOf(GameCharacter.BASE_ATK + atkPts * GameCharacter.ATK_PER_POINT));
        defPreview.setText(String.valueOf(GameCharacter.BASE_DEF + defPts * GameCharacter.DEF_PER_POINT));
    }

    /**
     * 创建角色
     */
    private void onCreateCharacter() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入角色名！", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int hpPts = hpSlider.getValue();
        int atkPts = atkSlider.getValue();
        int defPts = defSlider.getValue();
        int total = hpPts + atkPts + defPts;

        if (total > TOTAL_POINTS) {
            JOptionPane.showMessageDialog(this,
                    "属性点超出限制！已分配 " + total + " 点，最多 " + TOTAL_POINTS + " 点。",
                    "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        GameCharacter character = new GameCharacter(name, hpPts, atkPts, defPts);
        mainFrame.startTextBattle(character);
    }

    /**
     * 重置面板
     */
    public void resetPanel() {
        nameField.setText("勇者");
        hpSlider.setValue(0);
        atkSlider.setValue(0);
        defSlider.setValue(0);
        updatePreviews();
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        label.setForeground(Constants.COLOR_TEXT);
        return label;
    }

    private JSlider createSlider() {
        JSlider slider = new JSlider(0, TOTAL_POINTS, 0);
        slider.setBackground(new Color(40, 40, 60));
        slider.setForeground(new Color(100, 200, 255));
        slider.setMajorTickSpacing(5);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        return slider;
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 22));
        button.setPreferredSize(new Dimension(200, 50));
        button.setBackground(new Color(60, 60, 80));
        button.setForeground(Constants.COLOR_TEXT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(80, 80, 120));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(60, 60, 80));
            }
        });
        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        GradientPaint gradient = new GradientPaint(0, 0, new Color(30, 30, 50),
                0, getHeight(), new Color(20, 20, 40));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
}
