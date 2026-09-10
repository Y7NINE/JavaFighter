package com.fighting.ui;

import com.fighting.core.GameEngine;
import com.fighting.core.GameState;
import com.fighting.core.InputHandler;
import com.fighting.model.GameCharacter;
import com.fighting.model.User;
import com.fighting.system.UserManager;
import com.fighting.util.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * 主窗口框架
 */
public class MainFrame extends JFrame {
    private final GameEngine gameEngine;
    private final InputHandler inputHandler;
    private final UserManager userManager;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private LoginPanel loginPanel;
    private RegisterPanel registerPanel;
    private MenuPanel menuPanel;
    private CharacterSelectPanel selectPanel;
    private BattlePanel battlePanel;
    private CreateCharacterPanel createCharacterPanel;
    private TextBattlePanel textBattlePanel;

    public MainFrame(GameEngine gameEngine, InputHandler inputHandler, UserManager userManager) {
        this.gameEngine = gameEngine;
        this.inputHandler = inputHandler;
        this.userManager = userManager;

        // 窗口设置
        setTitle(Constants.GAME_TITLE);
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // 添加输入处理器
        addKeyListener(inputHandler);

        // 使用CardLayout切换面板
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // 初始化面板
        initPanels();

        add(mainPanel);
    }

    /**
     * 初始化所有面板
     */
    private void initPanels() {
        loginPanel = new LoginPanel(this, userManager);
        registerPanel = new RegisterPanel(this, userManager);
        menuPanel = new MenuPanel(this);
        selectPanel = new CharacterSelectPanel(this);
        battlePanel = new BattlePanel(this);
        createCharacterPanel = new CreateCharacterPanel(this);
        textBattlePanel = new TextBattlePanel(this);

        mainPanel.add(loginPanel, GameState.LOGIN.name());
        mainPanel.add(registerPanel, GameState.REGISTER.name());
        mainPanel.add(menuPanel, GameState.MENU.name());
        mainPanel.add(selectPanel, GameState.SELECT.name());
        mainPanel.add(battlePanel, GameState.BATTLE.name());
        mainPanel.add(createCharacterPanel, GameState.CREATE_CHARACTER.name());
        mainPanel.add(textBattlePanel, GameState.TEXT_BATTLE.name());

        // 默认显示登录
        cardLayout.show(mainPanel, GameState.LOGIN.name());
    }

    /**
     * 显示指定面板
     */
    public void showPanel(GameState state) {
        switch (state) {
            case LOGIN:
                loginPanel.initPanel();
                cardLayout.show(mainPanel, GameState.LOGIN.name());
                loginPanel.requestFocus();
                break;
            case REGISTER:
                registerPanel.initPanel();
                cardLayout.show(mainPanel, GameState.REGISTER.name());
                registerPanel.requestFocus();
                break;
            case MENU:
                cardLayout.show(mainPanel, GameState.MENU.name());
                menuPanel.requestFocus();
                break;
            case SELECT:
                selectPanel.initSelection();
                cardLayout.show(mainPanel, GameState.SELECT.name());
                selectPanel.requestFocus();
                break;
            case BATTLE:
                cardLayout.show(mainPanel, GameState.BATTLE.name());
                battlePanel.requestFocus();
                break;
            case CREATE_CHARACTER:
                createCharacterPanel.resetPanel();
                cardLayout.show(mainPanel, GameState.CREATE_CHARACTER.name());
                createCharacterPanel.requestFocus();
                break;
            case TEXT_BATTLE:
                cardLayout.show(mainPanel, GameState.TEXT_BATTLE.name());
                textBattlePanel.requestFocus();
                break;
            default:
                break;
        }
    }

    /**
     * 更新战斗逻辑
     */
    public void updateBattle() {
        battlePanel.updateBattle();
    }

    /**
     * 获取游戏引擎
     */
    public GameEngine getGameEngine() {
        return gameEngine;
    }

    /**
     * 获取战斗面板
     */
    public BattlePanel getBattlePanel() {
        return battlePanel;
    }

    /**
     * 获取输入处理器
     */
    public InputHandler getInputHandler() {
        return inputHandler;
    }

    /**
     * 开始文字战斗
     */
    public void startTextBattle(GameCharacter character) {
        gameEngine.changeState(GameState.TEXT_BATTLE);
        textBattlePanel.startBattle(character);
    }

    /**
     * 获取用户名
     */
    public String getUserName() {
        if (userManager != null && userManager.getCurrentUser() != null) {
            return userManager.getCurrentUser().getUsername();
        }
        return "勇者";
    }

    /**
     * 查找用户
     */
    public User findUser(String username) {
        return userManager.findUser(username);
    }

    /**
     * 设置当前用户
     */
    public void setCurrentUser(User user) {
        // 通过UserManager管理当前用户
        // 直接调用login方法来设置currentUser
    }

    /**
     * 添加用户
     */
    public void addUser(User user) {
        userManager.getUsers().add(user);
    }

    /**
     * 显示忘记密码对话框
     */
    public void showForgotPasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 5, 5));
        panel.add(new JLabel("用户名:"));
        JTextField usernameField = new JTextField();
        panel.add(usernameField);
        panel.add(new JLabel("手机号:"));
        JTextField phoneField = new JTextField();
        panel.add(phoneField);
        panel.add(new JLabel("新密码:"));
        JPasswordField newPasswordField = new JPasswordField();
        panel.add(newPasswordField);
        panel.add(new JLabel("确认密码:"));
        JPasswordField confirmPasswordField = new JPasswordField();
        panel.add(confirmPasswordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "忘记密码",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String phone = phoneField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "两次输入的密码不一致！", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String msg = userManager.forgotPassword(username, phone, newPassword);
            if (msg == null) {
                JOptionPane.showMessageDialog(this, "密码修改成功！", "成功", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, msg, "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 获取用户管理器
     */
    public UserManager getUserManager() {
        return userManager;
    }
}
