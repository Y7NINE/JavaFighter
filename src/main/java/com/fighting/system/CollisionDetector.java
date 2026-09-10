package com.fighting.system;

import com.fighting.model.Character;
import com.fighting.model.Skill;

import java.awt.*;

/**
 * 碰撞检测器
 */
public class CollisionDetector {

    /**
     * 检测两个矩形是否碰撞
     */
    public static boolean checkCollision(Rectangle r1, Rectangle r2) {
        return r1.intersects(r2);
    }

    /**
     * 检测攻击是否命中
     * @param attacker 攻击者
     * @param defender 防御者
     * @return 是否命中
     */
    public static boolean checkAttackHit(Character attacker, Character defender) {
        if (!attacker.isUsingSkill() || attacker.getCurrentSkill() == null) {
            return false;
        }

        // 获取攻击判定框
        Rectangle attackBox = attacker.getAttackHitbox();
        if (attackBox == null) return false;

        // 获取防御者身体判定框
        Rectangle defenderBox = defender.getBodyHitbox();

        // 检测碰撞
        return checkCollision(attackBox, defenderBox);
    }

    /**
     * 检测飞行道具是否命中
     * @param projectileX 道具X坐标
     * @param projectileY 道具Y坐标
     * @param projectileSize 道具大小
     * @param target 目标角色
     * @return 是否命中
     */
    public static boolean checkProjectileHit(int projectileX, int projectileY,
                                              int projectileSize, Character target) {
        Rectangle projectileBox = new Rectangle(projectileX, projectileY,
            projectileSize, projectileSize);
        Rectangle targetBox = target.getBodyHitbox();
        return checkCollision(projectileBox, targetBox);
    }

    /**
     * 计算两个角色之间的距离
     */
    public static double getDistance(Character c1, Character c2) {
        double dx = c1.getPosition().getX() - c2.getPosition().getX();
        double dy = c1.getPosition().getY() - c2.getPosition().getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * 检测技能是否在范围内
     */
    public static boolean isInSkillRange(Character attacker, Character defender, Skill skill) {
        double distance = getDistance(attacker, defender);
        return distance <= skill.getRange();
    }
}
