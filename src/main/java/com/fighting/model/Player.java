package com.fighting.model;

import com.fighting.core.InputHandler;
import com.fighting.util.Constants;

/**
 * 玩家类 - 将输入映射到角色动作
 */
public class Player {
    private final int playerNumber; // 1 或 2
    private Character character;
    private int wins;
    private int consecutiveLosses;  // 连败次数（用于武圣解锁）

    // 按键映射
    private int keyLeft;
    private int keyRight;
    private int keyUp;
    private int keyAttack1;
    private int keyAttack2;
    private int keyDodge;
    private int keyUltimate;

    public Player(int playerNumber) {
        this.playerNumber = playerNumber;
        this.wins = 0;
        this.consecutiveLosses = 0;
        setupKeyBindings();
    }

    /**
     * 设置按键绑定
     */
    private void setupKeyBindings() {
        if (playerNumber == 1) {
            // Player 1: WASD + JKL + 空格
            keyLeft = Constants.KEY_LEFT;      // A
            keyRight = Constants.KEY_RIGHT;     // D
            keyUp = Constants.KEY_UP;           // W
            keyAttack1 = Constants.KEY_ATTACK1; // J
            keyAttack2 = Constants.KEY_ATTACK2; // K
            keyDodge = Constants.KEY_DODGE;     // L
            keyUltimate = Constants.KEY_ULTIMATE; // 空格
        } else {
            // Player 2: 方向键 + 小键盘数字键（或其他映射）
            // 暂时使用相同按键，后续可配置
            keyLeft = 37;   // 左箭头
            keyRight = 39;  // 右箭头
            keyUp = 38;     // 上箭头
            keyAttack1 = 97; // 小键盘1
            keyAttack2 = 98; // 小键盘2
            keyDodge = 99;   // 小键盘3
            keyUltimate = 96; // 小键盘0
        }
    }

    /**
     * 处理输入
     */
    public void handleInput(InputHandler input) {
        if (character == null || character.isDead()) return;

        // 移动
        if (input.isKeyDown(keyLeft)) {
            character.moveLeft();
        } else if (input.isKeyDown(keyRight)) {
            character.moveRight();
        } else {
            character.stopMoving();
        }

        // 跳跃
        if (input.isKeyJustPressed(keyUp)) {
            character.jump();
        }

        // 攻击1（普攻）
        if (input.isKeyJustPressed(keyAttack1)) {
            character.useSkill(0);
        }

        // 攻击2（技能）
        if (input.isKeyJustPressed(keyAttack2)) {
            character.useSkill(1);
        }

        // 闪避
        if (input.isKeyJustPressed(keyDodge)) {
            character.dodge();
        }

        // 大招
        if (input.isKeyJustPressed(keyUltimate) && character.getSkills().size() > 3) {
            character.useSkill(3);
        }
    }

    /**
     * 设置角色
     */
    public void setCharacter(Character character) {
        this.character = character;
    }

    /**
     * 获取角色
     */
    public Character getCharacter() {
        return character;
    }

    /**
     * 增加胜场
     */
    public void addWin() {
        wins++;
        consecutiveLosses = 0;
    }

    /**
     * 增加败场
     */
    public void addLoss() {
        consecutiveLosses++;
    }

    /**
     * 重置胜场
     */
    public void resetWins() {
        wins = 0;
    }

    /**
     * 检查是否获胜
     */
    public boolean hasWon() {
        return wins >= Constants.ROUNDS_TO_WIN;
    }

    // Getters
    public int getPlayerNumber() { return playerNumber; }
    public int getWins() { return wins; }
    public int getConsecutiveLosses() { return consecutiveLosses; }
}
