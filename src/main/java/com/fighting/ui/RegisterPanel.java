package com.fighting.ui;

import com.fighting.core.GameState;
import com.fighting.model.User;
import com.fighting.system.UserManager;
import com.fighting.util.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * 注册面板
 */
public class RegisterPanel extends JPanel {

    private final MainFrame mainFrame;
    private final UserManager userManager;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField phoneField;
    private JLabel messageLabel;

    public RegisterPanel(MainFrame mainFrame, UserManager userManager) {
        this.mainFrame = mainFrame;
        this.userManager = userManager;
        setLayout(new GridBagLayout());
        setBackground(Constants.COLOR_BACKGROUND);
        initUI();
    }

    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(5, 0, 5, 0);

        // 标题
        JLabel titleLabel = new JLabel("注册");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 48));
        titleLabel.setForeground(Constants.COLOR_TEXT);
        gbc.gridy = 0;
        gbc.insets = new Insets(15, 0, 15, 0);
        add(titleLabel, gbc);

        // 用户名
        JLabel usernameLabel = new JLabel("用户名 (3-16位，字母数字，不能纯数字):");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        usernameLabel.setForeground(Constants.COLOR_TEXT);
        gbc.gridy = 1;
        add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(300, 28));
        gbc.gridy = 2;
        add(usernameField, gbc);

        // 密码
        JLabel passwordLabel = new JLabel("密码 (3-8位，字母+数字组合):");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        passwordLabel.setForeground(Constants.COLOR_TEXT);
        gbc.gridy = 3;
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(300, 28));
        gbc.gridy = 4;
        add(passwordField, gbc);

        // 确认密码
        JLabel confirmLabel = new JLabel("确认密码:");
        confirmLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        confirmLabel.setForeground(Constants.COLOR_TEXT);
        gbc.gridy = 5;
        add(confirmLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        confirmPasswordField.setPreferredSize(new Dimension(300, 28));
        gbc.gridy = 6;
        add(confirmPasswordField, gbc);

        // 手机号
        JLabel phoneLabel = new JLabel("手机号 (11位，以1开头):");
        phoneLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        phoneLabel.setForeground(Constants.COLOR_TEXT);
        gbc.gridy = 7;
        add(phoneLabel, gbc);

        phoneField = new JTextField(20);
        phoneField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        phoneField.setPreferredSize(new Dimension(300, 28));
        gbc.gridy = 8;
        add(phoneField, gbc);

        // 消息标签
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(255, 100, 100));
        gbc.gridy = 9;
        gbc.insets = new Insets(8, 0, 5, 0);
        add(messageLabel, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton registerButton = createButton("注册");
        registerButton.addActionListener(e -> doRegister());
        buttonPanel.add(registerButton);

        JButton backButton = createButton("返回");
        backButton.addActionListener(e -> {
            clearFields();
            mainFrame.showPanel(GameState.LOGIN);
        });
        buttonPanel.add(backButton);

        gbc.gridy = 10;
        gbc.insets = new Insets(5, 0, 0, 0);
        add(buttonPanel, gbc);
    }

    /**
     * 初始化面板
     */
    public void initPanel() {
        clearFields();
    }

    /**
     * 执行注册
     */
    private void doRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();
        String phone = phoneField.getText().trim();

        String result = userManager.register(username, password, confirmPassword, phone);
        if (result == null) {
            JOptionPane.showMessageDialog(this,
                    "注册成功！请登录。",
                    "注册成功", JOptionPane.INFORMATION_MESSAGE);
            clearFields();
            mainFrame.showPanel(GameState.LOGIN);
        } else {
            messageLabel.setText(result);
        }
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(120, 40));
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

    /**
     * 清空输入
     */
    public void clearFields() {
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        phoneField.setText("");
        messageLabel.setText(" ");
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
