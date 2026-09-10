package com.fighting.model;

/**
 * 技能类 - 支持动作游戏和回合制文字格斗系统
 */
public class Skill {

    // 基本属性
    private final String name;
    private final String description;

    // 动作游戏属性（原系统）
    private final int damage;
    private final int cooldown;
    private final int range;
    private final int startupFrames;
    private final int activeFrames;
    private final int recoveryFrames;
    private final boolean isUltimate;
    private final boolean isProjectile;

    // 文字格斗属性（新系统）
    private final SkillType skillType;
    private final int damagePercent;    // 攻击力百分比（如180表示180%）
    private final int hitCount;         // 攻击次数（如快速攻击2次）
    private final int hpCost;           // HP消耗
    private final int healMin;          // 最小恢复量
    private final int healMax;          // 最大恢复量
    private final boolean isDefenseStance; // 是否为防御姿态

    private int currentCooldown;
    private boolean isOnCooldown;

    /**
     * 技能类型枚举
     */
    public enum SkillType {
        NORMAL_ATTACK,   // 普通攻击
        POWER_STRIKE,    // 强力一击
        LIFE_DRAIN,      // 生命汲取
        SMASH,           // 猛击（初级战士）
        QUICK_ATTACK,    // 快速攻击（敏捷刺客）
        DEFENSE_STANCE,  // 防御姿态（重装坦克）
        FIREBALL         // 火球术（神秘法师）
    }

    /**
     * 完整构造函数（动作游戏用）
     */
    public Skill(String name, String description, int damage, int cooldown, int range,
                 int startupFrames, int activeFrames, int recoveryFrames,
                 boolean isUltimate, boolean isProjectile) {
        this.name = name;
        this.description = description;
        this.damage = damage;
        this.cooldown = cooldown;
        this.range = range;
        this.startupFrames = startupFrames;
        this.activeFrames = activeFrames;
        this.recoveryFrames = recoveryFrames;
        this.isUltimate = isUltimate;
        this.isProjectile = isProjectile;
        this.currentCooldown = 0;
        this.isOnCooldown = false;
        // 文字格斗属性默认值
        this.skillType = SkillType.NORMAL_ATTACK;
        this.damagePercent = 100;
        this.hitCount = 1;
        this.hpCost = 0;
        this.healMin = 0;
        this.healMax = 0;
        this.isDefenseStance = false;
    }

    /**
     * 简化构造函数（动作游戏用）
     */
    public Skill(String name, int damage, int cooldown, int range) {
        this(name, "", damage, cooldown, range, 5, 3, 10, false, false);
    }

    /**
     * 文字格斗技能构造函数
     */
    public Skill(String name, String description, SkillType skillType, int damagePercent,
                 int hitCount, int hpCost, int healMin, int healMax, boolean isDefenseStance) {
        this.name = name;
        this.description = description;
        this.skillType = skillType;
        this.damagePercent = damagePercent;
        this.hitCount = hitCount;
        this.hpCost = hpCost;
        this.healMin = healMin;
        this.healMax = healMax;
        this.isDefenseStance = isDefenseStance;
        // 动作游戏属性默认值
        this.damage = 0;
        this.cooldown = 0;
        this.range = 0;
        this.startupFrames = 0;
        this.activeFrames = 0;
        this.recoveryFrames = 0;
        this.isUltimate = false;
        this.isProjectile = false;
        this.currentCooldown = 0;
        this.isOnCooldown = false;
    }

    // ========== 玩家技能工厂方法 ==========

    /**
     * 普通攻击：造成基础伤害
     */
    public static Skill createNormalAttack() {
        return new Skill("普通攻击", "造成100%攻击力伤害",
                SkillType.NORMAL_ATTACK, 100, 1, 0, 0, 0, false);
    }

    /**
     * 强力一击：消耗10HP，造成180%攻击力伤害
     */
    public static Skill createPowerStrike() {
        return new Skill("强力一击", "消耗10HP，造成180%攻击力伤害",
                SkillType.POWER_STRIKE, 180, 1, 10, 0, 0, false);
    }

    /**
     * 生命汲取：消耗10HP，恢复0-20点生命值
     */
    public static Skill createLifeDrain() {
        return new Skill("生命汲取", "消耗10HP，恢复0-20点生命值",
                SkillType.LIFE_DRAIN, 0, 0, 10, 0, 20, false);
    }

    // ========== 敌人技能工厂方法 ==========

    /**
     * 猛击（初级战士）：150%伤害
     */
    public static Skill createSmash() {
        return new Skill("猛击", "造成150%攻击力伤害",
                SkillType.SMASH, 150, 1, 0, 0, 0, false);
    }

    /**
     * 快速攻击（敏捷刺客）：2次50%伤害
     */
    public static Skill createQuickAttack() {
        return new Skill("快速攻击", "造成2次50%攻击力伤害",
                SkillType.QUICK_ATTACK, 50, 2, 0, 0, 0, false);
    }

    /**
     * 防御姿态（重装坦克）：下回合伤害减半
     */
    public static Skill createDefenseStance() {
        return new Skill("防御姿态", "下回合受到的伤害减半",
                SkillType.DEFENSE_STANCE, 0, 0, 0, 0, 0, true);
    }

    /**
     * 火球术（神秘法师）：180%伤害
     */
    public static Skill createFireball() {
        return new Skill("火球术", "造成180%攻击力伤害",
                SkillType.FIREBALL, 180, 1, 0, 0, 0, false);
    }

    // ========== 动作游戏方法 ==========

    public boolean tryUse() {
        if (isOnCooldown) return false;
        currentCooldown = cooldown;
        isOnCooldown = true;
        return true;
    }

    public void updateCooldown() {
        if (isOnCooldown) {
            currentCooldown--;
            if (currentCooldown <= 0) {
                isOnCooldown = false;
                currentCooldown = 0;
            }
        }
    }

    public void resetCooldown() {
        currentCooldown = 0;
        isOnCooldown = false;
    }

    // ========== Getters ==========

    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getDamage() { return damage; }
    public int getCooldown() { return cooldown; }
    public int getRange() { return range; }
    public int getStartupFrames() { return startupFrames; }
    public int getActiveFrames() { return activeFrames; }
    public int getRecoveryFrames() { return recoveryFrames; }
    public boolean isUltimate() { return isUltimate; }
    public boolean isProjectile() { return isProjectile; }
    public int getCurrentCooldown() { return currentCooldown; }
    public boolean isOnCooldown() { return isOnCooldown; }

    public SkillType getSkillType() { return skillType; }
    public int getDamagePercent() { return damagePercent; }
    public int getHitCount() { return hitCount; }
    public int getHpCost() { return hpCost; }
    public int getHealMin() { return healMin; }
    public int getHealMax() { return healMax; }
    public boolean isDefenseStance() { return isDefenseStance; }

    public double getCooldownPercent() {
        if (cooldown == 0) return 0;
        return (double) currentCooldown / cooldown;
    }
}
