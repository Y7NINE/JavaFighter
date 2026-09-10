package com.fighting.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 游戏角色类 - 用于回合制文字格斗系统
 */
public class GameCharacter {

    // 属性分配常量
    public static final int TOTAL_POINTS = 20;
    public static final int BASE_HP = 100;
    public static final int BASE_ATK = 10;
    public static final int BASE_DEF = 0;
    public static final int HP_PER_POINT = 10;
    public static final int ATK_PER_POINT = 2;
    public static final int DEF_PER_POINT = 1;

    private String name;
    private int maxHp;
    private int currentHp;
    private int maxMp;              // 最大蓝量
    private int currentMp;          // 当前蓝量
    private int atk;
    private int def;
    private int winCount;
    private boolean halfDamageNextTurn;
    private List<Skill> skills;
    private ArrayList<Consumable> packageList;  // 背包
    private Random random;

    /**
     * 构造函数 - 通过属性点分配创建（玩家用）
     */
    public GameCharacter(String name, int hpPoints, int atkPoints, int defPoints) {
        this.name = name;
        this.maxHp = BASE_HP + hpPoints * HP_PER_POINT;
        this.currentHp = maxHp;
        this.maxMp = 100;
        this.currentMp = 100;
        this.atk = BASE_ATK + atkPoints * ATK_PER_POINT;
        this.def = BASE_DEF + defPoints * DEF_PER_POINT;
        this.winCount = 0;
        this.halfDamageNextTurn = false;
        this.skills = new ArrayList<>();
        this.packageList = new ArrayList<>();
        this.random = new Random();
        initPlayerSkills();
    }

    /**
     * 初始化玩家技能
     */
    private void initPlayerSkills() {
        skills.add(Skill.createNormalAttack());
        skills.add(Skill.createPowerStrike());
        skills.add(Skill.createLifeDrain());
    }

    /**
     * 消耗HP
     */
    public boolean consumeHp(int cost) {
        if (currentHp <= cost) {
            return false;
        }
        currentHp -= cost;
        return true;
    }

    /**
     * 治疗
     */
    public void heal(int amount) {
        currentHp = Math.min(currentHp + amount, maxHp);
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

    // 道具池
    private static final Consumable[] ITEM_POOL = {
        new Consumable("桃子", 10),
        new Consumable("煎蛋", 20),
        new Consumable("花酿鸡", 30),
        new Consumable("黑背鲈鱼", 40),
        new Consumable("白玉汤", 50)
    };

    /**
     * 胜利时调用 - 恢复HP/MP，尝试获取道具，检查属性提升
     */
    public void onVictory() {
        winCount++;
        // 恢复生命值（20-40点）
        int healAmount = 20 + random.nextInt(21);
        heal(healAmount);
        // 恢复最大蓝量的30%
        int mpRecover = (int) (maxMp * 0.3);
        currentMp = Math.min(currentMp + mpRecover, maxMp);
        // 30%几率获取道具
        if (random.nextDouble() < 0.3) {
            Consumable item = ITEM_POOL[random.nextInt(ITEM_POOL.length)];
            packageList.add(new Consumable(item.getName(), item.getNum()));
        }
        // 每3胜提升属性
        if (winCount % 3 == 0) {
            maxHp += 30;
            currentHp += 30;
            atk += 5;
            def += 3;
        }
    }

    // Getters and Setters
    public String getName() { return name; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public int getAtk() { return atk; }
    public int getDef() { return def; }
    public int getWinCount() { return winCount; }
    public boolean isHalfDamageNextTurn() { return halfDamageNextTurn; }
    public List<Skill> getSkills() { return skills; }

    public void setCurrentHp(int currentHp) { this.currentHp = currentHp; }
    public void setMaxHp(int maxHp) { this.maxHp = maxHp; }
    public void setAtk(int atk) { this.atk = atk; }
    public void setDef(int def) { this.def = def; }
    public void setHalfDamageNextTurn(boolean halfDamageNextTurn) { this.halfDamageNextTurn = halfDamageNextTurn; }

    public int getMaxMp() { return maxMp; }
    public void setMaxMp(int maxMp) { this.maxMp = maxMp; }

    public int getCurrentMp() { return currentMp; }
    public void setCurrentMp(int currentMp) { this.currentMp = currentMp; }

    public ArrayList<Consumable> getPackageList() { return packageList; }
    public void setPackageList(ArrayList<Consumable> packageList) { this.packageList = packageList; }
}
