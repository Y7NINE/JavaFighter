package com.fighting.characters;

import com.fighting.model.Character;
import com.fighting.model.Skill;
import java.awt.Color;

/**
 * 刺客 - 敏捷型战士
 * 技能1：劈 - 5s CD, 40伤害
 * 技能2：跳劈 - 7s CD, 50伤害
 * 技能3：影袭 - 8s CD
 * 技能4：大招 - 背刺，闪现到对手背后造成100伤害
 */
public class Assassin extends Character {

    public Assassin() {
        super("刺客", 1000);
    }

    @Override
    protected void initSkills() {
        // 技能1：劈
        skills.add(new Skill("劈", "快速劈砍", 40, 300, 70, 3, 5, 8, false, false));

        // 技能2：跳劈
        skills.add(new Skill("跳劈", "跳跃劈砍", 50, 420, 90, 5, 8, 12, false, false));

        // 技能3：影袭
        skills.add(new Skill("影袭", "快速位移", 0, 480, 0, 2, 12, 5, false, false));

        // 技能4：大招
        skills.add(new Skill("背刺", "闪现到对手背后造成100伤害", 100, 1800, 0, 5, 3, 15, true, false));
    }

    @Override
    public Color getColor() {
        return new Color(100, 50, 150); // 紫色
    }

    /**
     * 大招：闪现到对手背后
     */
    public void backstab(Character opponent) {
        // 计算对手背后的坐标
        double targetX;
        if (opponent.getFacing() == 1) {
            // 对手面朝右，闪现到其左边
            targetX = opponent.getPosition().getX() - width - 10;
        } else {
            // 对手面朝左，闪现到其右边
            targetX = opponent.getPosition().getX() + opponent.getWidth() + 10;
        }
        position.setX(targetX);
        position.setY(opponent.getPosition().getY());
    }
}
