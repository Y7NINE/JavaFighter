package com.fighting.ui;

import com.fighting.core.GameState;
import com.fighting.model.User;
import com.fighting.system.UserManager;
import com.fighting.util.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * 登录面板
 */
public class LoginPanel extends JPanel {

    private final MainFrame mainFrame;
    private final UserManager userManager;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel captchaLabel;
    private JTextField captchaField;
    private JLabel messageLabel;

    public LoginPanel(MainFrame mainFrame, UserManager userManager) {
        this.mainFrame = mainFrame;
        this.userManager = userManager;
        setLayout(new GridBagLayout());
        setBackground(Constants.COLOR_BACKGROUND);
        initUI();
    }

    private void initUI() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 0, 8, 0);

        // 标题
        JLabel titleLabel = new JLabel("登录");
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 48));
        titleLabel.setForeground(Constants.COLOR_TEXT);
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 0, 30, 0);
        add(titleLabel, gbc);

        // 用户名
        JLabel usernameLabel = new JLabel("用户名:");
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        usernameLabel.setForeground(Constants.COLOR_TEXT);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 3, 0);
        add(usernameLabel, gbc);

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        usernameField.setPreferredSize(new Dimension(300, 32));
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(usernameField, gbc);

        // 密码
        JLabel passwordLabel = new JLabel("密码:");
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 18));
        passwordLabel.setForeground(Constants.COLOR_TEXT);
        gbc.gridy = 3;
        gbc.insets = new Insets(5, 0, 3, 0);
        add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        passwordField.setPreferredSize(new Dimension(300, 32));
        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(passwordField, gbc);

        // 验证码
        String initialCaptcha = userManager.generateCaptcha();
        captchaLabel = new JLabel("验证码: " + initialCaptcha);
        captchaLabel.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        captchaLabel.setForeground(new Color(255, 215, 0));
        gbc.gridy = 5;
        gbc.insets = new Insets(5, 0, 3, 0);
        add(captchaLabel, gbc);

        captchaField = new JTextField(20);
        captchaField.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        captchaField.setPreferredSize(new Dimension(300, 32));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(captchaField, gbc);

        // 消息标签
        messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        messageLabel.setForeground(new Color(255, 100, 100));
        gbc.gridy = 7;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(messageLabel, gbc);

        // 按钮面板
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        JButton loginButton = createButton("登录");
        loginButton.addActionListener(e -> doLogin());
        buttonPanel.add(loginButton);

        JButton registerButton = createButton("注册");
        registerButton.addActionListener(e -> mainFrame.showPanel(GameState.REGISTER));
        buttonPanel.add(registerButton);

        JButton forgotButton = createButton("忘记密码");
        forgotButton.addActionListener(e -> mainFrame.showForgotPasswordDialog());
        buttonPanel.add(forgotButton);

        gbc.gridy = 8;
        gbc.insets = new Insets(5, 0, 0, 0);
        add(buttonPanel, gbc);
    }

    /**
     * 初始化面板
     */
    public void initPanel() {
        usernameField.setText("");
        passwordField.setText("");
        captchaField.setText("");
        messageLabel.setText(" ");
        // 保持当前验证码，不重新生成
    }

    /**
     * 执行登录
     */
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String captcha = captchaField.getText().trim();

        if (username.isEmpty() || password.isEmpty() || captcha.isEmpty()) {
            messageLabel.setText("请输入用户名、密码和验证码！");
            return;
        }

        String result = userManager.login(username, password, captcha);
        if (result == null) {
            // 登录成功
            messageLabel.setText("");
            JOptionPane.showMessageDialog(this,
                    "登录成功！欢迎回来，" + username + "！",
                    "登录成功", JOptionPane.INFORMATION_MESSAGE);
            mainFrame.getGameEngine().changeState(GameState.MENU);
        } else {
            messageLabel.setText(result);
            // 生成新验证码
            String newCaptcha = userManager.generateCaptcha();
            captchaLabel.setText("验证码: " + newCaptcha);
            captchaField.setText("");
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
