package com.fighting.system;

import com.fighting.characters.Assassin;
import com.fighting.characters.Boxer;
import com.fighting.characters.Swordsman;
import com.fighting.model.Character;
import com.fighting.model.Player;
import com.fighting.model.Skill;
import com.fighting.util.Constants;

/**
 * 战斗系统 - 管理战斗逻辑
 */
public class BattleSystem {
    private final Player player1;
    private final Player player2;
    private int roundNumber;
    private int roundTimer;
    private BattleState battleState;
    private String lastHitSkillName; // 最后命中技能名称

    // 战斗状态
    public enum BattleState {
        ROUND_START,    // 回合开始
        FIGHTING,       // 战斗中
        ROUND_END,      // 回合结束
        GAME_OVER       // 游戏结束
    }

    public BattleSystem(Player player1, Player player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.roundNumber = 0;
        this.battleState = BattleState.ROUND_START;
    }

    /**
     * 初始化新回合
     */
    public void initRound() {
        roundNumber++;
        roundTimer = 0;
        battleState = BattleState.ROUND_START;

        // 重置角色位置
        Character char1 = player1.getCharacter();
        Character char2 = player2.getCharacter();

        if (char1 != null) char1.reset(200);
        if (char2 != null) char2.reset(900);
    }

    /**
     * 更新战斗逻辑
     */
    public void update() {
        Character char1 = player1.getCharacter();
        Character char2 = player2.getCharacter();

        if (char1 == null || char2 == null) return;

        switch (battleState) {
            case ROUND_START:
                roundTimer++;
                if (roundTimer >= Constants.ROUND_START_DELAY) {
                    battleState = BattleState.FIGHTING;
                    roundTimer = 0;
                }
                break;

            case FIGHTING:
                // 更新角色
                char1.update();
                char2.update();

                // 自动面向对手
                autoFaceOpponent(char1, char2);

                // 检测攻击命中
                checkAttackHit(char1, char2);
                checkAttackHit(char2, char1);

                // 检查回合结束
                if (char1.isDead() || char2.isDead()) {
                    endRound();
                }
                break;

            case ROUND_END:
                roundTimer++;
                if (roundTimer >= Constants.ROUND_END_DELAY) {
                    // 检查游戏是否结束
                    if (player1.hasWon() || player2.hasWon()) {
                        battleState = BattleState.GAME_OVER;
                    } else {
                        // 开始下一回合
                        initRound();
                    }
                }
                break;

            case GAME_OVER:
                // 游戏结束，等待UI处理
                break;
        }
    }

    /**
     * 自动面向对手
     */
    private void autoFaceOpponent(Character c1, Character c2) {
        if (c1.getPosition().getX() < c2.getPosition().getX()) {
            c1.setFacing(1);
            c2.setFacing(-1);
        } else {
            c1.setFacing(-1);
            c2.setFacing(1);
        }
    }

    /**
     * 检测攻击命中
     */
    private void checkAttackHit(Character attacker, Character defender) {
        if (!attacker.isUsingSkill() || attacker.getCurrentSkill() == null) {
            return;
        }

        Skill currentSkill = attacker.getCurrentSkill();

        // 检查是否在生效帧
        int totalFrames = currentSkill.getStartupFrames() + currentSkill.getActiveFrames();
        int frameInSkill = currentSkill.getStartupFrames() + currentSkill.getActiveFrames() +
                          currentSkill.getRecoveryFrames() - attacker.getCurrentSkill().getCurrentCooldown();

        // 只在生效帧检测碰撞
        if (frameInSkill >= currentSkill.getStartupFrames() &&
            frameInSkill < totalFrames) {

            if (CollisionDetector.checkAttackHit(attacker, defender)) {
                // 计算伤害
                int damage = currentSkill.getDamage();

                // 特殊技能处理
                if (attacker instanceof Swordsman && currentSkill.isUltimate()) {
                    damage = ((Swordsman) attacker).getUltimateDamage();
                }

                // 造成伤害
                defender.takeDamage(damage);
                lastHitSkillName = currentSkill.getName();

                // 拳师大招回血
                if (attacker instanceof Boxer && currentSkill.isUltimate()) {
                    ((Boxer) attacker).ultimateHeal();
                }

                // 刺客大招闪现
                if (attacker instanceof Assassin && currentSkill.isUltimate()) {
                    ((Assassin) attacker).backstab(defender);
                }
            }
        }
    }

    /**
     * 结束回合
     */
    private void endRound() {
        battleState = BattleState.ROUND_END;
        roundTimer = 0;

        Character char1 = player1.getCharacter();
        Character char2 = player2.getCharacter();

        if (char1.isDead()) {
            player2.addWin();
            player1.addLoss();
        } else if (char2.isDead()) {
            player1.addWin();
            player2.addLoss();
        }
    }

    /**
     * 获取获胜者
     */
    public Player getWinner() {
        if (player1.hasWon()) return player1;
        if (player2.hasWon()) return player2;
        return null;
    }

    /**
     * 获取当前状态
     */
    public BattleState getBattleState() {
        return battleState;
    }

    /**
     * 获取回合号
     */
    public int getRoundNumber() {
        return roundNumber;
    }

    /**
     * 获取最后命中技能名称
     */
    public String getLastHitSkillName() {
        return lastHitSkillName;
    }

    /**
     * 重置战斗系统
     */
    public void reset() {
        roundNumber = 0;
        player1.resetWins();
        player2.resetWins();
        battleState = BattleState.ROUND_START;
    }
}
