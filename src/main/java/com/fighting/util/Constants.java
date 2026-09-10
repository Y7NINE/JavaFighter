package com.fighting.util;

/**
 * 游戏常量定义
 */
public final class Constants {
    private Constants() {}

    // 窗口设置
    public static final String GAME_TITLE = "格斗游戏";
    public static final int WINDOW_WIDTH = 1200;
    public static final int WINDOW_HEIGHT = 700;
    public static final int FPS = 60;
    public static final long FRAME_TIME = 1000 / FPS;

    // 游戏区域
    public static final int GROUND_Y = 500;           // 地面Y坐标
    public static final int STAGE_LEFT = 50;           // 舞台左边界
    public static final int STAGE_RIGHT = 1150;        // 舞台右边界

    // 角色设置
    public static final int CHARACTER_WIDTH = 80;
    public static final int CHARACTER_HEIGHT = 120;
    public static final int MAX_HP = 1000;
    public static final int MOVE_SPEED = 5;
    public static final int JUMP_FORCE = -15;
    public static final int GRAVITY = 1;

    // 战斗设置
    public static final int ROUNDS_TO_WIN = 2;         // 几局几胜
    public static final int ROUND_START_DELAY = 180;   // 回合开始延迟（帧数）
    public static final int ROUND_END_DELAY = 120;     // 回合结束延迟（帧数）
    public static final int DEFENSE_DAMAGE_REDUCTION = 20; // 防御减伤百分比

    // 角色类型
    public static final String CHARACTER_SWORDSMAN = "剑豪";
    public static final String CHARACTER_BOXER = "拳师";
    public static final String CHARACTER_ASSASSIN = "刺客";
    public static final String CHARACTER_WUSHENG = "武圣";

    // 武圣解锁条件
    public static final int CONSECUTIVE_LOSSES_TO_UNLOCK = 3; // 连败次数解锁

    // 操作按键
    public static final int KEY_LEFT = 65;      // A
    public static final int KEY_RIGHT = 68;     // D
    public static final int KEY_UP = 87;        // W
    public static final int KEY_ATTACK1 = 74;   // J
    public static final int KEY_ATTACK2 = 75;   // K
    public static final int KEY_DODGE = 76;     // L
    public static final int KEY_ULTIMATE = 32;  // 空格

    // 颜色
    public static final java.awt.Color COLOR_BACKGROUND = new java.awt.Color(30, 30, 50);
    public static final java.awt.Color COLOR_GROUND = new java.awt.Color(80, 60, 40);
    public static final java.awt.Color COLOR_HP_BAR_P1 = new java.awt.Color(0, 200, 255);
    public static final java.awt.Color COLOR_HP_BAR_P2 = new java.awt.Color(255, 50, 50);
    public static final java.awt.Color COLOR_HP_BACKGROUND = new java.awt.Color(50, 50, 50);
    public static final java.awt.Color COLOR_TEXT = new java.awt.Color(255, 255, 255);
}
