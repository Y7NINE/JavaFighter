package com.fighting.characters;

import com.fighting.model.Character;
import com.fighting.model.Skill;
import java.awt.Color;

/**
 * 武圣 - 隐藏角色，全能型战士
 * 解锁条件：玩家选择任意角色累计战败3次
 *
 * 技能1：刺 - 5s CD, 30伤害
 * 技能2：劈 - 6s CD, 40伤害
 * 技能3：格挡 - 8s CD
 * 技能4：大招 - 真伤1000
 */
public class Wusheng extends Character {

    public Wusheng() {
        super("武圣", 1000);
    }

    @Override
    protected void initSkills() {
        // 技能1：刺
        skills.add(new Skill("刺", "快速刺击", 30, 300, 80, 3, 5, 8, false, false));

        // 技能2：劈
        skills.add(new Skill("劈", "重劈攻击", 40, 360, 100, 5, 5, 12, false, false));

        // 技能3：格挡
        skills.add(new Skill("格挡", "格挡攻击", 0, 480, 0, 3, 20, 5, false, false));

        // 技能4：大招 - 真伤1000
        skills.add(new Skill("武圣降临", "真实伤害1000", 1000, 3600, 200, 15, 20, 25, true, true));
    }

    @Override
    public Color getColor() {
        return new Color(255, 215, 0); // 金色
    }
}
