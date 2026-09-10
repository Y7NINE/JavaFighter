package com.fighting.ui;

import com.fighting.core.GameState;
import com.fighting.model.Character;
import com.fighting.system.CharacterFactory;
import com.fighting.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 角色选择面板
 */
public class CharacterSelectPanel extends JPanel {
    private final MainFrame mainFrame;
    private String player1Character;
    private String player2Character;
    private int currentPlayer; // 当前选择的玩家 1 或 2

    private JLabel p1CharLabel;
    private JLabel p2CharLabel;
    private JButton[] characterButtons;
    private JLabel messageLabel;

    public CharacterSelectPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(Constants.COLOR_BACKGROUND);
        initUI();
    }

    private void initUI() {
        // 标题
        JLabel titleLabel = new JLabel("选择角色", JLabel.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 48));
        titleLabel.setForeground(Constants.COLOR_TEXT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // 中心区域
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        // 当前玩家提示
        JPanel turnPanel = new JPanel();
        turnPanel.setOpaque(false);
        turnPanel.setLayout(new BoxLayout(turnPanel, BoxLayout.Y_AXIS));

        // 角色按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        buttonPanel.setOpaque(false);

        List<String> characters = CharacterFactory.getAvailableCharacters();
        characterButtons = new JButton[characters.size()];

        for (int i = 0; i < characters.size(); i++) {
            String charName = characters.get(i);
            characterButtons[i] = createCharacterButton(charName, i);
            buttonPanel.add(characterButtons[i]);
        }

        centerPanel.add(buttonPanel);
        centerPanel.add(Box.createVerticalStrut(30));

        // 玩家选择状态 - 水平排列
        JPanel statusPanel = new JPanel(new GridLayout(1, 2, 100, 0));
        statusPanel.setOpaque(false);
        statusPanel.setMaximumSize(new Dimension(600, 80));

        // Player 1 状态
        JPanel p1Panel = createPlayerPanel("Player 1");
        p1CharLabel = (JLabel) p1Panel.getComponent(1);
        statusPanel.add(p1Panel);

        // VS 标签
        JLabel vsLabel = new JLabel("VS", JLabel.CENTER);
        vsLabel.setFont(new Font("Arial", Font.BOLD, 32));
        vsLabel.setForeground(new Color(255, 215, 0));
        statusPanel.add(vsLabel, BorderLayout.CENTER);

        // Player 2 状态
        JPanel p2Panel = createPlayerPanel("Player 2");
        p2CharLabel = (JLabel) p2Panel.getComponent(1);
        statusPanel.add(p2Panel);

        centerPanel.add(statusPanel);
        add(centerPanel, BorderLayout.CENTER);

        // 底部提示信息
        messageLabel = new JLabel("Player 1 先选择角色", JLabel.CENTER);
        messageLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
        messageLabel.setForeground(new Color(255, 215, 0));
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        add(messageLabel, BorderLayout.SOUTH);
    }

    private JPanel createPlayerPanel(String playerName) {
        JPanel panel = new JPanel(new GridLayout(2, 1, 0, 10));
        panel.setOpaque(false);

        JLabel nameLabel = new JLabel(playerName, JLabel.CENTER);
        nameLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        nameLabel.setForeground(Constants.COLOR_TEXT);

        JLabel charLabel = new JLabel("未选择", JLabel.CENTER);
        charLabel.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        charLabel.setForeground(new Color(200, 200, 200));

        panel.add(nameLabel);
        panel.add(charLabel);

        return panel;
    }

    private JButton createCharacterButton(String charName, int index) {
        JButton button = new JButton(charName);
        button.setFont(new Font("微软雅黑", Font.BOLD, 24));
        button.setPreferredSize(new Dimension(150, 100));

        // 根据角色设置颜色
        Character tempChar = CharacterFactory.createCharacter(charName);
        button.setBackground(tempChar.getColor());
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 悬停效果
        final Color originalColor = tempChar.getColor();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        // 点击事件
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectCharacter(currentPlayer, charName);
            }
        });

        return button;
    }

    /**
     * 初始化选择界面
     */
    public void initSelection() {
        player1Character = null;
        player2Character = null;
        currentPlayer = 1;
        p1CharLabel.setText("未选择");
        p2CharLabel.setText("未选择");
        messageLabel.setText("Player 1 先选择角色");

        // 启用所有按钮
        for (JButton btn : characterButtons) {
            btn.setEnabled(true);
            btn.setBackground(btn.getBackground()); // 恢复原色
        }
    }

    /**
     * 选择角色
     */
    public void selectCharacter(int player, String charName) {
        if (player == 1 && player1Character == null) {
            // Player 1 选择
            player1Character = charName;
            p1CharLabel.setText(charName);
            p1CharLabel.setForeground(new Color(100, 200, 255));
            currentPlayer = 2;
            messageLabel.setText("Player 2 现在选择角色");

            // 更新提示
            messageLabel.setText("Player 1 选择 " + charName + "，Player 2 请选择");

        } else if (player == 2 && player2Character == null) {
            // Player 2 选择（不能和 Player 1 相同）
            if (charName.equals(player1Character)) {
                messageLabel.setText("不能选择相同角色，请选择其他角色！");
                return;
            }
            player2Character = charName;
            p2CharLabel.setText(charName);
            p2CharLabel.setForeground(new Color(255, 100, 100));
            currentPlayer = 0;
            messageLabel.setText("双方选择完毕，即将开始战斗...");

            // 禁用按钮
            for (JButton btn : characterButtons) {
                btn.setEnabled(false);
            }

            // 延迟后开始战斗
            Timer timer = new Timer(1000, e -> startBattle());
            timer.setRepeats(false);
            timer.start();
        }
    }

    /**
     * 开始战斗
     */
    private void startBattle() {
        BattlePanel battlePanel = mainFrame.getBattlePanel();
        battlePanel.initBattle(player1Character, player2Character);
        mainFrame.getGameEngine().changeState(GameState.BATTLE);
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
