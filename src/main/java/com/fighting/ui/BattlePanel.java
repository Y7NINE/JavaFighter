package com.fighting.ui;

import com.fighting.core.GameState;
import com.fighting.core.InputHandler;
import com.fighting.model.Character;
import com.fighting.model.Player;
import com.fighting.system.BattleSystem;
import com.fighting.system.CharacterFactory;
import com.fighting.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * 战斗面板
 */
public class BattlePanel extends JPanel {
    private final MainFrame mainFrame;
    private Player player1;
    private Player player2;
    private BattleSystem battleSystem;

    // UI组件
    private JLabel roundLabel;
    private JLabel messageLabel;
    private JLabel p1WinLabel;
    private JLabel p2WinLabel;

    // 屏幕震动效果
    private int shakeX;
    private int shakeY;
    private int shakeTimer;

    public BattlePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(null); // 使用绝对布局
        setBackground(Constants.COLOR_BACKGROUND);
        setFocusable(true);
        // 添加键盘监听器
        addKeyListener(mainFrame.getInputHandler());
        initUI();
    }

    private void initUI() {
        // 回合显示
        roundLabel = new JLabel("", JLabel.CENTER);
        roundLabel.setFont(new Font("微软雅黑", Font.BOLD, 36));
        roundLabel.setForeground(Constants.COLOR_TEXT);
        roundLabel.setBounds(0, 20, Constants.WINDOW_WIDTH, 50);
        add(roundLabel);

        // 消息显示
        messageLabel = new JLabel("", JLabel.CENTER);
        messageLabel.setFont(new Font("微软雅黑", Font.BOLD, 48));
        messageLabel.setForeground(new Color(255, 215, 0));
        messageLabel.setBounds(0, 250, Constants.WINDOW_WIDTH, 60);
        add(messageLabel);

        // Player 1 胜利标记
        p1WinLabel = new JLabel("", JLabel.CENTER);
        p1WinLabel.setFont(new Font("微软雅黑", Font.PLAIN, 24));
        p1WinLabel.setForeground(Constants.COLOR_HP_BAR_P1);
        p1WinLabel.setBounds(50, 80, 200, 30);
        add(p1WinLabel);

        // Player 2 胜利标记
        p2WinLabel = new JLabel("", JLabel.CENTER);
        p2WinLabel.setFont(new Font("微软雅黑", Font.PLAIN, 24));
        p2WinLabel.setForeground(Constants.COLOR_HP_BAR_P2);
        p2WinLabel.setBounds(Constants.WINDOW_WIDTH - 250, 80, 200, 30);
        add(p2WinLabel);
    }

    /**
     * 初始化战斗
     */
    public void initBattle(String char1Name, String char2Name) {
        // 创建角色
        Character char1 = CharacterFactory.createCharacter(char1Name);
        Character char2 = CharacterFactory.createCharacter(char2Name);

        // 创建玩家
        player1 = new Player(1);
        player2 = new Player(2);
        player1.setCharacter(char1);
        player2.setCharacter(char2);

        // 创建战斗系统
        battleSystem = new BattleSystem(player1, player2);
        battleSystem.initRound();

        // 更新UI
        roundLabel.setText("第 " + battleSystem.getRoundNumber() + " 回合");
        messageLabel.setText("");
        updateWinDisplay();
    }

    /**
     * 更新战斗逻辑
     */
    public void updateBattle() {
        if (battleSystem == null) return;

        // 处理输入
        player1.handleInput(mainFrame.getInputHandler());
        player2.handleInput(mainFrame.getInputHandler());

        // 更新战斗系统
        battleSystem.update();

        // 更新UI
        updateUIState();

        // 更新屏幕震动
        if (shakeTimer > 0) {
            shakeTimer--;
            shakeX = (int) (Math.random() * 6 - 3);
            shakeY = (int) (Math.random() * 6 - 3);
        } else {
            shakeX = 0;
            shakeY = 0;
        }
    }

    /**
     * 更新UI状态
     */
    private void updateUIState() {
        BattleSystem.BattleState state = battleSystem.getBattleState();

        switch (state) {
            case ROUND_START:
                messageLabel.setText("ROUND " + battleSystem.getRoundNumber());
                break;
            case FIGHTING:
                messageLabel.setText("");
                break;
            case ROUND_END:
                Character char1 = player1.getCharacter();
                Character char2 = player2.getCharacter();
                if (char1.isDead()) {
                    messageLabel.setText("Player 2 获胜！");
                } else if (char2.isDead()) {
                    messageLabel.setText("Player 1 获胜！");
                }
                updateWinDisplay();
                break;
            case GAME_OVER:
                Player winner = battleSystem.getWinner();
                if (winner != null) {
                    messageLabel.setText("Player " + winner.getPlayerNumber() + " 获得最终胜利！");
                }
                // 3秒后返回菜单
                Timer timer = new Timer(3000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        mainFrame.getGameEngine().changeState(GameState.MENU);
                    }
                });
                timer.setRepeats(false);
                timer.start();
                break;
        }
    }

    /**
     * 更新胜场显示
     */
    private void updateWinDisplay() {
        StringBuilder p1Wins = new StringBuilder("胜场: ");
        StringBuilder p2Wins = new StringBuilder("胜场: ");

        for (int i = 0; i < Constants.ROUNDS_TO_WIN; i++) {
            p1Wins.append(i < player1.getWins() ? "♥" : "♡");
            p2Wins.append(i < player2.getWins() ? "♥" : "♡");
        }

        p1WinLabel.setText(p1Wins.toString());
        p2WinLabel.setText(p2Wins.toString());
    }

    /**
     * 触发屏幕震动
     */
    public void triggerShake(int duration) {
        this.shakeTimer = duration;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // 应用屏幕震动
        g2d.translate(shakeX, shakeY);

        // 绘制背景
        drawBackground(g2d);

        // 绘制地面
        drawGround(g2d);

        // 绘制角色
        if (player1 != null && player1.getCharacter() != null) {
            player1.getCharacter().draw(g2d);
        }
        if (player2 != null && player2.getCharacter() != null) {
            player2.getCharacter().draw(g2d);
        }

        // 绘制血条
        drawHealthBars(g2d);

        // 绘制技能冷却
        drawSkillCooldowns(g2d);

        // 绘制FPS
        g2d.setColor(Color.YELLOW);
        g2d.setFont(new Font("Arial", Font.PLAIN, 12));
        g2d.drawString("FPS: " + mainFrame.getGameEngine().getFps(), 10, 20);
    }

    private void drawBackground(Graphics2D g) {
        // 渐变背景
        GradientPaint gradient = new GradientPaint(0, 0, new Color(40, 40, 60),
            0, getHeight(), new Color(20, 20, 40));
        g.setPaint(gradient);
        g.fillRect(0, 0, getWidth(), getHeight());

        // 绘制背景装饰
        g.setColor(new Color(50, 50, 70));
        for (int i = 0; i < 5; i++) {
            int x = 100 + i * 250;
            g.fillRect(x, 350, 80, 150);
            g.fillRect(x + 20, 300, 40, 50);
        }
    }

    private void drawGround(Graphics2D g) {
        g.setColor(Constants.COLOR_GROUND);
        g.fillRect(0, Constants.GROUND_Y, getWidth(), getHeight() - Constants.GROUND_Y);

        // 地面纹理
        g.setColor(new Color(60, 45, 30));
        for (int x = 0; x < getWidth(); x += 50) {
            g.drawLine(x, Constants.GROUND_Y, x + 25, Constants.GROUND_Y + 20);
        }
    }

    private void drawHealthBars(Graphics2D g) {
        int barWidth = 400;
        int barHeight = 25;
        int barY = 60;

        // Player 1 血条
        drawHealthBar(g, 50, barY, barWidth, barHeight,
            player1, Constants.COLOR_HP_BAR_P1, true);

        // Player 2 血条
        drawHealthBar(g, Constants.WINDOW_WIDTH - 50 - barWidth, barY, barWidth, barHeight,
            player2, Constants.COLOR_HP_BAR_P2, false);
    }

    private void drawHealthBar(Graphics2D g, int x, int y, int width, int height,
                                Player player, Color color, boolean leftAligned) {
        if (player == null || player.getCharacter() == null) return;

        Character character = player.getCharacter();

        // 背景
        g.setColor(Constants.COLOR_HP_BACKGROUND);
        g.fillRect(x, y, width, height);

        // 血量
        double hpPercent = (double) character.getCurrentHp() / character.getMaxHp();
        int hpWidth = (int) (width * hpPercent);
        g.setColor(color);
        if (leftAligned) {
            g.fillRect(x, y, hpWidth, height);
        } else {
            g.fillRect(x + width - hpWidth, y, hpWidth, height);
        }

        // 边框
        g.setColor(Color.WHITE);
        g.drawRect(x, y, width, height);

        // 血量文字
        g.setFont(new Font("Arial", Font.BOLD, 14));
        String hpText = character.getCurrentHp() + "/" + character.getMaxHp();
        FontMetrics fm = g.getFontMetrics();
        int textX = leftAligned ? x + 10 : x + width - fm.stringWidth(hpText) - 10;
        g.drawString(hpText, textX, y + 18);

        // 角色名称
        g.setFont(new Font("微软雅黑", Font.BOLD, 16));
        String nameText = "P" + player.getPlayerNumber() + " " + character.getName();
        if (leftAligned) {
            g.drawString(nameText, x, y - 5);
        } else {
            int nameWidth = fm.stringWidth(nameText);
            g.drawString(nameText, x + width - nameWidth, y - 5);
        }
    }

    private void drawSkillCooldowns(Graphics2D g) {
        if (player1 == null || player2 == null) return;

        // Player 1 技能
        drawPlayerSkills(g, player1, 50, Constants.WINDOW_HEIGHT - 80);

        // Player 2 技能
        drawPlayerSkills(g, player2, Constants.WINDOW_WIDTH - 250, Constants.WINDOW_HEIGHT - 80);
    }

    private void drawPlayerSkills(Graphics2D g, Player player, int x, int y) {
        if (player.getCharacter() == null) return;

        g.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        int skillSize = 40;
        int gap = 10;

        for (int i = 0; i < player.getCharacter().getSkills().size() && i < 4; i++) {
            int sx = x + i * (skillSize + gap);
            com.fighting.model.Skill skill = player.getCharacter().getSkills().get(i);

            // 技能框背景
            g.setColor(skill.isOnCooldown() ? new Color(50, 50, 50) : new Color(80, 80, 100));
            g.fillRect(sx, y, skillSize, skillSize);

            // 冷却遮罩
            if (skill.isOnCooldown()) {
                g.setColor(new Color(0, 0, 0, 150));
                int cdHeight = (int) (skillSize * skill.getCooldownPercent());
                g.fillRect(sx, y + skillSize - cdHeight, skillSize, cdHeight);
            }

            // 边框
            g.setColor(Color.WHITE);
            g.drawRect(sx, y, skillSize, skillSize);

            // 技能名称
            g.setColor(Color.WHITE);
            String skillName = skill.getName().substring(0, Math.min(2, skill.getName().length()));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(skillName, sx + (skillSize - fm.stringWidth(skillName)) / 2, y + skillSize / 2 + 5);
        }
    }
}
