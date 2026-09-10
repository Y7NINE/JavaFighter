package com.fighting.ui;

import com.fighting.core.GameState;
import com.fighting.model.Enemy;
import com.fighting.model.GameCharacter;
import com.fighting.system.TextBattleSystem;
import com.fighting.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 文字战斗面板 - 控制台风格
 */
public class TextBattlePanel extends JPanel {

    private final MainFrame mainFrame;
    private final TextBattleSystem battleSystem;

    private JTextArea battleLog;
    private JLabel playerHpLabel;
    private JLabel playerStatLabel;
    private JLabel enemyHpLabel;
    private JLabel enemyStatLabel;
    private JLabel turnLabel;
    private JProgressBar playerHpBar;
    private JProgressBar enemyHpBar;
    private JButton[] skillButtons;

    public TextBattlePanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.battleSystem = new TextBattleSystem();
        setLayout(new BorderLayout());
        setBackground(Constants.COLOR_BACKGROUND);
        initUI();
    }

    private void initUI() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        turnLabel = new JLabel("准备战斗", SwingConstants.CENTER);
        turnLabel.setFont(new Font("微软雅黑", Font.BOLD, 24));
        turnLabel.setForeground(new Color(255, 215, 0));
        topPanel.add(turnLabel, BorderLayout.NORTH);

        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        statusPanel.setOpaque(false);

        JPanel playerStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        playerStatus.setOpaque(false);
        playerHpLabel = new JLabel("玩家 HP:");
        playerHpLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        playerHpLabel.setForeground(new Color(0, 200, 255));
        playerHpBar = new JProgressBar(0, 100);
        playerHpBar.setPreferredSize(new Dimension(300, 20));
        playerHpBar.setForeground(new Color(0, 200, 255));
        playerHpBar.setBackground(new Color(50, 50, 50));
        playerHpBar.setStringPainted(true);
        playerStatLabel = new JLabel("ATK: 0  DEF: 0");
        playerStatLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        playerStatLabel.setForeground(new Color(180, 180, 200));
        playerStatus.add(playerHpLabel);
        playerStatus.add(playerHpBar);
        playerStatus.add(playerStatLabel);
        statusPanel.add(playerStatus);

        JPanel enemyStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        enemyStatus.setOpaque(false);
        enemyHpLabel = new JLabel("敌人 HP:");
        enemyHpLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
        enemyHpLabel.setForeground(new Color(255, 50, 50));
        enemyHpBar = new JProgressBar(0, 100);
        enemyHpBar.setPreferredSize(new Dimension(300, 20));
        enemyHpBar.setForeground(new Color(255, 50, 50));
        enemyHpBar.setBackground(new Color(50, 50, 50));
        enemyHpBar.setStringPainted(true);
        enemyStatLabel = new JLabel("ATK: 0  DEF: 0");
        enemyStatLabel.setFont(new Font("微软雅黑", Font.PLAIN, 14));
        enemyStatLabel.setForeground(new Color(180, 180, 200));
        enemyStatus.add(enemyHpLabel);
        enemyStatus.add(enemyHpBar);
        enemyStatus.add(enemyStatLabel);
        statusPanel.add(enemyStatus);

        topPanel.add(statusPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        battleLog = new JTextArea();
        battleLog.setEditable(false);
        battleLog.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        battleLog.setBackground(new Color(15, 15, 25));
        battleLog.setForeground(new Color(200, 220, 200));
        battleLog.setCaretColor(new Color(200, 220, 200));
        battleLog.setLineWrap(true);
        battleLog.setWrapStyleWord(true);
        battleLog.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JScrollPane logScrollPane = new JScrollPane(battleLog);
        logScrollPane.setBorder(BorderFactory.createLineBorder(new Color(60, 60, 80), 2));
        logScrollPane.getVerticalScrollBar().setBackground(new Color(30, 30, 50));
        add(logScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 15, 20));

        JPanel skillPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        skillPanel.setOpaque(false);

        skillButtons = new JButton[3];
        String[] skillNames = {"1.普通攻击", "2.强力一击", "3.生命汲取"};
        String[] skillDescs = {"100%攻击力伤害", "消耗10HP，180%伤害", "消耗10HP，恢复0-20HP"};

        for (int i = 0; i < 3; i++) {
            final int index = i;
            skillButtons[i] = new JButton("<html><center>" + skillNames[i] + "<br><small>" + skillDescs[i] + "</small></center></html>");
            skillButtons[i].setFont(new Font("微软雅黑", Font.BOLD, 14));
            skillButtons[i].setPreferredSize(new Dimension(200, 60));
            skillButtons[i].setBackground(new Color(60, 60, 100));
            skillButtons[i].setForeground(Constants.COLOR_TEXT);
            skillButtons[i].setFocusPainted(false);
            skillButtons[i].setBorderPainted(false);
            skillButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            skillButtons[i].setEnabled(false);
            skillButtons[i].addActionListener(e -> onSkillSelected(index));
            skillButtons[i].addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    if (skillButtons[index].isEnabled())
                        skillButtons[index].setBackground(new Color(80, 80, 140));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    skillButtons[index].setBackground(new Color(60, 60, 100));
                }
            });
            skillPanel.add(skillButtons[i]);
        }

        bottomPanel.add(skillPanel, BorderLayout.CENTER);

        JLabel hintLabel = new JLabel("选择技能开始战斗 | 连胜3场获得属性加成 | 游戏结束可重新开始", SwingConstants.CENTER);
        hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 13));
        hintLabel.setForeground(new Color(140, 140, 160));
        bottomPanel.add(hintLabel, BorderLayout.SOUTH);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void startBattle(GameCharacter player) {
        battleSystem.setPlayer(player);
        battleSystem.reset();
        battleSystem.spawnNewEnemy();

        battleLog.setText("");

        appendLog("====================================");
        appendLog("  回合制文字格斗游戏");
        appendLog("====================================");
        appendLog("");
        appendLog("欢迎，【" + player.getName() + "】！");
        appendLog("你遇到了【" + battleSystem.getEnemy().getName() + "】！");
        appendLog("");
        appendLog("--- 你的属性 ---");
        appendLog("HP: " + player.getCurrentHp() + "/" + player.getMaxHp()
                + "  ATK: " + player.getAtk() + "  DEF: " + player.getDef());
        appendLog("");
        appendLog("--- 敌人属性 ---");
        Enemy enemy = battleSystem.getEnemy();
        appendLog("HP: " + enemy.getCurrentHp() + "/" + enemy.getMaxHp()
                + "  ATK: " + enemy.getAtk() + "  DEF: " + enemy.getDef());
        appendLog("");
        appendLog("请选择你的行动！");
        appendLog("");

        updateStatusDisplay();
        enableSkillButtons(true);
    }

    private void onSkillSelected(int skillIndex) {
        if (battleSystem.isBattleOver()) return;

        enableSkillButtons(false);

        List<String> log = battleSystem.executePlayerSkill(skillIndex);
        for (String line : log) {
            appendLog(line);
        }
        appendLog("");

        updateStatusDisplay();

        if (battleSystem.isBattleOver()) {
            if (battleSystem.isPlayerWon()) {
                SwingUtilities.invokeLater(() -> {
                    int choice = JOptionPane.showConfirmDialog(this,
                            "胜利！是否继续下一场？\n当前连胜: " + battleSystem.getConsecutiveWins(),
                            "战斗胜利", JOptionPane.YES_NO_OPTION);
                    if (choice == JOptionPane.YES_OPTION) {
                        nextBattle();
                    } else {
                        returnToMenu();
                    }
                });
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "游戏结束！\n最终连胜: " + battleSystem.getConsecutiveWins() + " 场",
                            "战斗失败", JOptionPane.INFORMATION_MESSAGE);
                    returnToMenu();
                });
            }
        } else {
            enableSkillButtons(true);
        }
    }

    private void nextBattle() {
        battleSystem.spawnNewEnemy();
        Enemy enemy = battleSystem.getEnemy();

        appendLog("");
        appendLog("====================================");
        appendLog("  新的战斗开始！");
        appendLog("====================================");
        appendLog("");
        appendLog("你遇到了【" + enemy.getName() + "】！");
        appendLog("HP: " + enemy.getCurrentHp() + "/" + enemy.getMaxHp()
                + "  ATK: " + enemy.getAtk() + "  DEF: " + enemy.getDef());
        appendLog("");

        GameCharacter player = battleSystem.getPlayer();
        appendLog("你的状态 - HP: " + player.getCurrentHp() + "/" + player.getMaxHp()
                + "  ATK: " + player.getAtk() + "  DEF: " + player.getDef());
        appendLog("");

        updateStatusDisplay();
        enableSkillButtons(true);
    }

    private void returnToMenu() {
        battleSystem.reset();
        mainFrame.getGameEngine().changeState(GameState.MENU);
    }

    private void updateStatusDisplay() {
        GameCharacter player = battleSystem.getPlayer();
        Enemy enemy = battleSystem.getEnemy();

        if (player != null) {
            playerHpLabel.setText(player.getName() + " HP:");
            playerHpBar.setMaximum(player.getMaxHp());
            playerHpBar.setValue(player.getCurrentHp());
            playerHpBar.setString(player.getCurrentHp() + " / " + player.getMaxHp());
            playerStatLabel.setText("ATK: " + player.getAtk() + "  DEF: " + player.getDef());
        }

        if (enemy != null) {
            enemyHpLabel.setText(enemy.getName() + " HP:");
            enemyHpBar.setMaximum(enemy.getMaxHp());
            enemyHpBar.setValue(enemy.getCurrentHp());
            enemyHpBar.setString(enemy.getCurrentHp() + " / " + enemy.getMaxHp());
            enemyStatLabel.setText("ATK: " + enemy.getAtk() + "  DEF: " + enemy.getDef());
        }

        turnLabel.setText("连胜: " + battleSystem.getConsecutiveWins() + "  |  第 " + battleSystem.getTurnNumber() + " 回合");
    }

    private void appendLog(String text) {
        battleLog.append(text + "\n");
        SwingUtilities.invokeLater(() -> {
            battleLog.setCaretPosition(battleLog.getDocument().getLength());
        });
    }

    private void enableSkillButtons(boolean enabled) {
        for (JButton btn : skillButtons) {
            btn.setEnabled(enabled);
        }
    }
}
