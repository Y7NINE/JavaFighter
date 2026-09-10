package com.fighting.characters;

import com.fighting.model.Character;
import com.fighting.model.Skill;
import java.awt.Color;

/**
 * 剑豪 - 均衡型战士
 * 技能1：刺 - 5s CD, 30伤害
 * 技能2：劈 - 7s CD, 40伤害
 * 技能3：闪 - 8s CD
 * 技能4：大招 - 随机真伤150-300
 */
public class Swordsman extends Character {

    public Swordsman() {
        super("剑豪", 1000);
    }

    @Override
    protected void initSkills() {
        // 技能1：刺
        skills.add(new Skill("刺", "快速刺击", 30, 300, 150, 3, 5, 8, false, false));

        // 技能2：劈
        skills.add(new Skill("劈", "重劈攻击", 40, 420, 180, 5, 5, 12, false, false));

        // 技能3：闪
        skills.add(new Skill("闪", "快速位移", 0, 480, 0, 2, 10, 5, false, false));

        // 技能4：大招
        skills.add(new Skill("剑气纵横", "随机真伤150-300", 0, 1800, 250, 10, 15, 20, true, true));
    }

    @Override
    public Color getColor() {
        return new Color(70, 130, 180); // 钢蓝色
    }

    /**
     * 重写大招伤害计算（随机真伤）
     */
    public int getUltimateDamage() {
        return 150 + (int)(Math.random() * 151); // 150-300
    }
}
