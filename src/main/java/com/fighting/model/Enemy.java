package com.fighting.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 敌人类 - 回合制文字格斗系统
 */
public class Enemy {

    private String name;
    private int maxHp;
    private int currentHp;
    private int atk;
    private int def;
    private EnemyType type;

    private List<Skill> skills;
    private boolean halfDamageNextTurn; // 重装坦克防御姿态

    /**
     * 敌人类型枚举
     */
    public enum EnemyType {
        WARRIOR("初级战士", 80, 15, 10),
        ASSASSIN("敏捷刺客", 60, 20, 5),
        TANK("重装坦克", 120, 10, 20),
        MAGE("神秘法师", 70, 25, 8);

        private final String displayName;
        private final int baseHp;
        private final int baseAtk;
        private final int baseDef;

        EnemyType(String displayName, int baseHp, int baseAtk, int baseDef) {
            this.displayName = displayName;
            this.baseHp = baseHp;
            this.baseAtk = baseAtk;
            this.baseDef = baseDef;
        }

        public String getDisplayName() { return displayName; }
        public int getBaseHp() { return baseHp; }
        public int getBaseAtk() { return baseAtk; }
        public int getBaseDef() { return baseDef; }
    }

    public Enemy(EnemyType type, int consecutiveWins) {
        this.type = type;
        this.name = type.getDisplayName();
        // 基础属性 + 每连胜一场成长
        this.maxHp = type.getBaseHp() + consecutiveWins * 10;
        this.currentHp = maxHp;
        this.atk = type.getBaseAtk() + consecutiveWins * 3;
        this.def = type.getBaseDef() + consecutiveWins * 2;
        this.halfDamageNextTurn = false;
        this.skills = new ArrayList<>();
        initSkills();
    }

    /**
     * 根据类型初始化技能
     */
    private void initSkills() {
        switch (type) {
            case WARRIOR:
                // 猛击：150%伤害
                skills.add(Skill.createSmash());
                break;
            case ASSASSIN:
                // 快速攻击：2次50%伤害
                skills.add(Skill.createQuickAttack());
                break;
            case TANK:
                // 防御姿态：下回合伤害减半
                skills.add(Skill.createDefenseStance());
                break;
            case MAGE:
                // 火球术：180%伤害
                skills.add(Skill.createFireball());
                break;
        }
    }

    /**
     * 敌人AI选择行动
     * 简单AI：70%概率使用技能，30%概率普攻
     */
    public EnemyAction chooseAction() {
        double rand = Math.random();
        if (rand < 0.7 && !skills.isEmpty()) {
            return new EnemyAction(true, skills.get(0));
        } else {
            return new EnemyAction(false, null);
        }
    }

    /**
     * 受到伤害
     */
    public void takeDamage(int damage) {
        if (halfDamageNextTurn) {
            damage = Math.max(1, damage / 2);
            halfDamageNextTurn = false;
        }
        currentHp -= damage;
        if (currentHp < 0) currentHp = 0;
    }

    /**
     * 是否死亡
     */
    public boolean isDead() {
        return currentHp <= 0;
    }

    public void setHalfDamageNextTurn(boolean value) {
        this.halfDamageNextTurn = value;
    }

    public boolean isHalfDamageNextTurn() {
        return halfDamageNextTurn;
    }

    // Getters
    public String getName() { return name; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public EnemyType getType() { return type; }
    public List<Skill> getSkills() { return skills; }

    /**
     * 敌人行动结果
     */
    public static class EnemyAction {
        private final boolean useSkill;
        private final Skill skill;

        public EnemyAction(boolean useSkill, Skill skill) {
            this.useSkill = useSkill;
            this.skill = skill;
        }

        public boolean isUseSkill() { return useSkill; }
        public Skill getSkill() { return skill; }
    }
}
