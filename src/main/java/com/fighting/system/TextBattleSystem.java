package com.fighting.system;

import com.fighting.model.Enemy;
import com.fighting.model.Enemy.EnemyType;
import com.fighting.model.GameCharacter;
import com.fighting.model.Skill;

import java.util.ArrayList;
import java.util.List;

/**
 * 文字战斗系统 - 回合制格斗逻辑（GUI版本）
 */
public class TextBattleSystem {

    private GameCharacter player;
    private Enemy enemy;
    private boolean battleOver;
    private boolean playerWon;
    private int consecutiveWins;
    private int turnNumber;

    public TextBattleSystem() {
        this.consecutiveWins = 0;
        this.battleOver = false;
        this.playerWon = false;
        this.turnNumber = 0;
    }

    public void setPlayer(GameCharacter player) {
        this.player = player;
    }

    public void spawnNewEnemy() {
        EnemyType[] types = EnemyType.values();
        EnemyType randomType = types[(int) (Math.random() * types.length)];
        this.enemy = new Enemy(randomType, consecutiveWins);
        this.battleOver = false;
        this.playerWon = false;
        this.turnNumber = 0;
    }

    public List<String> executePlayerSkill(int skillIndex) {
        List<String> log = new ArrayList<>();
        turnNumber++;

        if (player.isDead() || enemy.isDead()) {
            return log;
        }

        Skill skill = player.getSkills().get(skillIndex);
        log.add("=== 第 " + turnNumber + " 回合 ===");
        log.add("【" + player.getName() + "】使用了【" + skill.getName() + "】！");

        if (skill.getHpCost() > 0) {
            if (!player.consumeHp(skill.getHpCost())) {
                log.add("HP不足，无法使用该技能！");
                return log;
            }
            log.add("消耗了 " + skill.getHpCost() + " 点HP。");
        }

        switch (skill.getSkillType()) {
            case NORMAL_ATTACK:
            case POWER_STRIKE:
            case SMASH:
            case FIREBALL: {
                int rawDamage = player.getAtk() * skill.getDamagePercent() / 100;
                int damage = Math.max(1, rawDamage - enemy.getDef());
                enemy.takeDamage(damage);
                log.add("对【" + enemy.getName() + "】造成了 " + damage + " 点伤害！");
                break;
            }
            case QUICK_ATTACK: {
                int totalDamage = 0;
                for (int i = 0; i < skill.getHitCount(); i++) {
                    int rawDamage = player.getAtk() * skill.getDamagePercent() / 100;
                    int damage = Math.max(1, rawDamage - enemy.getDef());
                    enemy.takeDamage(damage);
                    totalDamage += damage;
                }
                log.add("对【" + enemy.getName() + "】造成了 " + skill.getHitCount()
                        + " 次攻击，共 " + totalDamage + " 点伤害！");
                break;
            }
            case LIFE_DRAIN: {
                int heal = skill.getHealMin()
                        + (int) (Math.random() * (skill.getHealMax() - skill.getHealMin() + 1));
                player.heal(heal);
                log.add("恢复了 " + heal + " 点生命值！");
                break;
            }
            case DEFENSE_STANCE:
                player.setHalfDamageNextTurn(true);
                log.add("进入防御姿态，下回合受到的伤害减半！");
                break;
            default:
                break;
        }

        log.add(player.getName() + " HP: " + player.getCurrentHp() + "/" + player.getMaxHp());
        log.add(enemy.getName() + " HP: " + enemy.getCurrentHp() + "/" + enemy.getMaxHp());

        if (enemy.isDead()) {
            log.add("");
            log.add("*** 【" + enemy.getName() + "】被击败了！***");
            player.onVictory();
            consecutiveWins++;
            log.add(player.getName() + " 获胜！恢复了部分生命值。");
            log.add("当前连胜: " + consecutiveWins + " 场");
            if (player.getWinCount() % 3 == 0) {
                log.add("*** 恭喜！每3胜奖励：HP+30, ATK+5, DEF+3 ***");
            }
            battleOver = true;
            playerWon = true;
            return log;
        }

        log.add("");
        List<String> enemyLog = executeEnemyAction();
        log.addAll(enemyLog);

        if (player.isDead()) {
            log.add("");
            log.add("*** 【" + player.getName() + "】倒下了！***");
            log.add("*** 游戏结束！最终连胜: " + consecutiveWins + " 场 ***");
            battleOver = true;
            playerWon = false;
        }

        return log;
    }

    private List<String> executeEnemyAction() {
        List<String> log = new ArrayList<>();
        Enemy.EnemyAction action = enemy.chooseAction();

        if (action.isUseSkill() && action.getSkill() != null) {
            Skill skill = action.getSkill();
            log.add("【" + enemy.getName() + "】使用了【" + skill.getName() + "】！");

            switch (skill.getSkillType()) {
                case SMASH:
                case FIREBALL: {
                    int rawDamage = enemy.getAtk() * skill.getDamagePercent() / 100;
                    int damage = Math.max(1, rawDamage - player.getDef());
                    player.takeDamage(damage);
                    log.add("对【" + player.getName() + "】造成了 " + damage + " 点伤害！");
                    break;
                }
                case QUICK_ATTACK: {
                    int totalDamage = 0;
                    for (int i = 0; i < skill.getHitCount(); i++) {
                        int rawDamage = enemy.getAtk() * skill.getDamagePercent() / 100;
                        int damage = Math.max(1, rawDamage - player.getDef());
                        player.takeDamage(damage);
                        totalDamage += damage;
                    }
                    log.add("对【" + player.getName() + "】造成了 " + skill.getHitCount()
                            + " 次攻击，共 " + totalDamage + " 点伤害！");
                    break;
                }
                case DEFENSE_STANCE:
                    enemy.setHalfDamageNextTurn(true);
                    log.add(enemy.getName() + " 进入防御姿态，下回合受到的伤害减半！");
                    break;
                default:
                    break;
            }
        } else {
            log.add("【" + enemy.getName() + "】进行了普通攻击！");
            int damage = Math.max(1, enemy.getAtk() - player.getDef());
            player.takeDamage(damage);
            log.add("对【" + player.getName() + "】造成了 " + damage + " 点伤害！");
        }

        return log;
    }

    /**
     * 执行敌人回合（用于道具使用后）
     */
    public List<String> executeEnemyTurn() {
        List<String> log = new ArrayList<>();
        turnNumber++;

        if (player == null || enemy == null || battleOver || player.isDead()) {
            return log;
        }

        log.add("=== 第 " + turnNumber + " 回合（敌人行动）===");
        List<String> enemyLog = executeEnemyAction();
        log.addAll(enemyLog);

        if (player.isDead()) {
            log.add("");
            log.add("*** 【" + player.getName() + "】倒下了！***");
            log.add("*** 游戏结束！最终连胜: " + consecutiveWins + " 场 ***");
            battleOver = true;
            playerWon = false;
        }

        return log;
    }

    public void reset() {
        this.consecutiveWins = 0;
        this.battleOver = false;
        this.playerWon = false;
        this.turnNumber = 0;
    }

    public GameCharacter getPlayer() { return player; }
    public Enemy getEnemy() { return enemy; }
    public boolean isBattleOver() { return battleOver; }
    public boolean isPlayerWon() { return playerWon; }
    public int getConsecutiveWins() { return consecutiveWins; }
    public int getTurnNumber() { return turnNumber; }
}
