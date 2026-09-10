package com.fighting.ui;

import com.fighting.core.GameState;
import com.fighting.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

/**
 * 主菜单面板
 */
public class MenuPanel extends JPanel {
    private final MainFrame mainFrame;

    public MenuPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new GridBagLayout());
        setBackground(Constants.COLOR_BACKGROUND);
        initUI();
    }

    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;

        // 游戏标题 - 艺术字体效果
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("格斗游戏");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 80));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("FIGHTING GAME");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        subtitleLabel.setForeground(new Color(180, 180, 200));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(10));
        titlePanel.add(subtitleLabel);

        gbc.gridy = 0;
        gbc.insets = new Insets(40, 0, 60, 0);
        add(titlePanel, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 开始游戏按钮
        JButton startButton = createMenuButton("开 始 游 戏", new Color(64, 128, 200));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.getGameEngine().changeState(GameState.SELECT);
            }
        });
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createVerticalStrut(15));

        // 文字格斗按钮
        JButton textButton = createMenuButton("文 字 格 斗", new Color(100, 160, 100));
        textButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mainFrame.getGameEngine().changeState(GameState.CREATE_CHARACTER);
            }
        });
        buttonPanel.add(textButton);
        buttonPanel.add(Box.createVerticalStrut(15));

        // 退出按钮
        JButton exitButton = createMenuButton("退 出 游 戏", new Color(160, 64, 64));
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        buttonPanel.add(exitButton);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 40, 0);
        add(buttonPanel, gbc);

        // 操作说明面板
        JPanel hintPanel = new JPanel();
        hintPanel.setOpaque(false);
        hintPanel.setLayout(new BoxLayout(hintPanel, BoxLayout.Y_AXIS));

        JLabel hintTitle = new JLabel("━━━ 操作说明 ━━━");
        hintTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 16));
        hintTitle.setForeground(new Color(255, 215, 0));
        hintTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hint1 = new JLabel("动作格斗: WASD移动 J攻击 K技能 L闪避 空格大招");
        hint1.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        hint1.setForeground(new Color(160, 160, 180));
        hint1.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel hint2 = new JLabel("文字格斗: 回合制战斗 选择技能攻击敌人");
        hint2.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        hint2.setForeground(new Color(160, 160, 180));
        hint2.setAlignmentX(Component.CENTER_ALIGNMENT);

        hintPanel.add(hintTitle);
        hintPanel.add(Box.createVerticalStrut(8));
        hintPanel.add(hint1);
        hintPanel.add(Box.createVerticalStrut(4));
        hintPanel.add(hint2);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(hintPanel, gbc);

        // 版本信息
        JLabel versionLabel = new JLabel("v1.0");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        versionLabel.setForeground(new Color(100, 100, 120));
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(versionLabel, gbc);
    }

    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // 绘制圆角矩形背景
                Shape shape = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(getBackground());
                g2.fill(shape);

                // 绘制边框
                g2.setColor(color.brighter());
                g2.setStroke(new BasicStroke(2));
                g2.draw(shape);

                // 绘制文字
                g2.setColor(getForeground());
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };

        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        button.setPreferredSize(new Dimension(320, 55));
        button.setMaximumSize(new Dimension(320, 55));
        button.setBackground(color.darker());
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        // 悬停效果
        final Color hoverColor = color;
        final Color normalColor = color.darker();
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
                button.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(normalColor);
                button.repaint();
            }
        });

        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 绘制渐变背景
        GradientPaint gradient = new GradientPaint(0, 0, new Color(25, 25, 45),
            0, getHeight(), new Color(15, 15, 35));
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // 绘制装饰线条
        g2d.setColor(new Color(255, 215, 0, 30));
        g2d.setStroke(new BasicStroke(1));
        for (int i = 0; i < 5; i++) {
            int y = 100 + i * 120;
            g2d.drawLine(0, y, getWidth(), y);
        }
    }
}
