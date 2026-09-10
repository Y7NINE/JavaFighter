package com.fighting.characters;

import com.fighting.model.Character;
import com.fighting.model.Skill;
import java.awt.Color;

/**
 * 拳师 - 爆发型近战
 * 技能1：刺拳 - 4s CD, 30伤害
 * 技能2：重拳 - 7s CD, 50伤害
 * 技能3：格挡 - 8s CD
 * 技能4：大招 - 暴风连拳180伤害+自身回血60
 */
public class Boxer extends Character {

    public Boxer() {
        super("拳师", 1000);
    }

    @Override
    protected void initSkills() {
        // 技能1：刺拳
        skills.add(new Skill("刺拳", "快速刺拳", 30, 240, 120, 2, 4, 6, false, false));

        // 技能2：重拳
        skills.add(new Skill("重拳", "重拳出击", 50, 420, 150, 6, 5, 14, false, false));

        // 技能3：格挡
        skills.add(new Skill("格挡", "格挡攻击", 0, 480, 0, 3, 20, 5, false, false));

        // 技能4：大招
        skills.add(new Skill("暴风连拳", "180伤害+回血60", 180, 1800, 180, 8, 20, 15, true, false));
    }

    @Override
    public Color getColor() {
        return new Color(200, 50, 50); // 红色
    }

    /**
     * 使用大招后回血
     */
    public void ultimateHeal() {
        currentHp = Math.min(currentHp + 60, maxHp);
    }
}
