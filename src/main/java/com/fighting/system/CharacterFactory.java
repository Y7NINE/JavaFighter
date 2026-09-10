package com.fighting.system;

import com.fighting.characters.*;
import com.fighting.model.Character;
import com.fighting.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色工厂 - 创建和管理角色
 */
public class CharacterFactory {

    // 可选角色列表（不含武圣）
    private static final List<String> AVAILABLE_CHARACTERS = new ArrayList<>();

    static {
        AVAILABLE_CHARACTERS.add(Constants.CHARACTER_SWORDSMAN);
        AVAILABLE_CHARACTERS.add(Constants.CHARACTER_BOXER);
        AVAILABLE_CHARACTERS.add(Constants.CHARACTER_ASSASSIN);
    }

    /**
     * 根据名称创建角色
     */
    public static Character createCharacter(String name) {
        switch (name) {
            case Constants.CHARACTER_SWORDSMAN:
                return new Swordsman();
            case Constants.CHARACTER_BOXER:
                return new Boxer();
            case Constants.CHARACTER_ASSASSIN:
                return new Assassin();
            case Constants.CHARACTER_WUSHENG:
                return new Wusheng();
            default:
                throw new IllegalArgumentException("未知角色: " + name);
        }
    }

    /**
     * 随机选择一个角色（不包括武圣）
     */
    public static String getRandomCharacter() {
        int index = (int) (Math.random() * AVAILABLE_CHARACTERS.size());
        return AVAILABLE_CHARACTERS.get(index);
    }

    /**
     * 为两个玩家随机分配不同角色
     */
    public static String[] assignCharacters() {
        String char1 = getRandomCharacter();
        String char2;

        do {
            char2 = getRandomCharacter();
        } while (char2.equals(char1));

        return new String[]{char1, char2};
    }

    /**
     * 获取可选角色列表
     */
    public static List<String> getAvailableCharacters() {
        return new ArrayList<>(AVAILABLE_CHARACTERS);
    }

    /**
     * 检查是否可以解锁武圣
     */
    public static boolean canUnlockWusheng(int consecutiveLosses) {
        return consecutiveLosses >= Constants.CONSECUTIVE_LOSSES_TO_UNLOCK;
    }
}
