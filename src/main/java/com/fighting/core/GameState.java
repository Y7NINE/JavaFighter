package com.fighting.core;

/**
 * 游戏状态枚举
 */
public enum GameState {
    LOGIN,          // 登录
    REGISTER,       // 注册
    MENU,           // 主菜单
    SELECT,         // 角色选择
    BATTLE,         // 战斗中
    ROUND_END,      // 回合结束
    GAME_OVER,      // 游戏结束
    CHARACTER_UNLOCK, // 角色解锁（武圣）
    CREATE_CHARACTER, // 创建角色（文字格斗）
    TEXT_BATTLE       // 文字战斗（回合制）
}
