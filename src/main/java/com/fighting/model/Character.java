package com.fighting.model;

import com.fighting.util.Constants;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 角色基类
 */
public abstract class Character {

    // 基本属性
    protected String name;
    protected int maxHp;
    protected int currentHp;
    protected Position position;
    protected int width;
    protected int height;
    protected int facing;  // 1=右, -1=左

    // 移动属性
    protected double velocityX;
    protected double velocityY;
    protected boolean onGround;
    protected boolean isMoving;

    // 技能列表
    protected List<Skill> skills;
    protected Skill currentSkill;
    protected int skillFrameCounter;
    protected boolean isUsingSkill;

    // 状态
    protected boolean isDefending;
    protected boolean isDodging;
    protected boolean isHit;
    protected boolean isDead;
    protected int hitStunFrames;
    protected int dodgeFrames;
    protected int invincibleFrames;  // 无敌帧

    // 动画
    protected int animFrame;
    protected int animTimer;

    public Character(String name, int maxHp) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.position = new Position();
        this.width = Constants.CHARACTER_WIDTH;
        this.height = Constants.CHARACTER_HEIGHT;
        this.facing = 1;
        this.skills = new ArrayList<>();
        this.onGround = true;
        initSkills();
    }

    /**
     * 初始化角色技能（子类实现）
     */
    protected abstract void initSkills();

    /**
     * 获取角色颜色（用于简单渲染）
     */
    public abstract Color getColor();

    /**
     * 更新角色状态
     */
    public void update() {
        // 更新冷却时间
        for (Skill skill : skills) {
            skill.updateCooldown();
        }

        // 更新受击硬直
        if (isHit) {
            hitStunFrames--;
            if (hitStunFrames <= 0) {
                isHit = false;
            }
        }

        // 更新闪避
        if (isDodging) {
            dodgeFrames--;
            if (dodgeFrames <= 0) {
                isDodging = false;
                invincibleFrames = 0;
            }
        }

        // 更新无敌帧
        if (invincibleFrames > 0) {
            invincibleFrames--;
        }

        // 更新技能使用
        if (isUsingSkill) {
            skillFrameCounter--;
            if (skillFrameCounter <= 0) {
                isUsingSkill = false;
                currentSkill = null;
            }
        }

        // 应用重力
        if (!onGround) {
            velocityY += Constants.GRAVITY;
        }

        // 应用移动
        position.add(velocityX, velocityY);

        // 地面检测
        if (position.getY() >= Constants.GROUND_Y - height) {
            position.setY(Constants.GROUND_Y - height);
            velocityY = 0;
            onGround = true;
        }

        // 边界检测
        if (position.getX() < Constants.STAGE_LEFT) {
            position.setX(Constants.STAGE_LEFT);
        }
        if (position.getX() + width > Constants.STAGE_RIGHT) {
            position.setX(Constants.STAGE_RIGHT - width);
        }

        // 摩擦力
        velocityX *= 0.8;

        // 动画更新
        animTimer++;
        if (animTimer >= 8) {
            animFrame++;
            animTimer = 0;
        }
    }

    /**
     * 向左移动
     */
    public void moveLeft() {
        if (!isHit && !isUsingSkill) {
            velocityX = -Constants.MOVE_SPEED;
            facing = -1;
            isMoving = true;
        }
    }

    /**
     * 向右移动
     */
    public void moveRight() {
        if (!isHit && !isUsingSkill) {
            velocityX = Constants.MOVE_SPEED;
            facing = 1;
            isMoving = true;
        }
    }

    /**
     * 停止移动
     */
    public void stopMoving() {
        isMoving = false;
    }

    /**
     * 跳跃
     */
    public void jump() {
        if (onGround && !isHit && !isUsingSkill) {
            velocityY = Constants.JUMP_FORCE;
            onGround = false;
        }
    }

    /**
     * 防御
     */
    public void defend(boolean defending) {
        if (!isHit && !isUsingSkill) {
            this.isDefending = defending;
        }
    }

    /**
     * 闪避
     */
    public void dodge() {
        if (!isHit && !isDodging && !isUsingSkill) {
            isDodging = true;
            dodgeFrames = 15;
            invincibleFrames = 15;
            // 闪避位移
            velocityX = facing * 10;
        }
    }

    /**
     * 使用技能
     * @param skillIndex 技能索引
     * @return 是否成功使用
     */
    public boolean useSkill(int skillIndex) {
        if (isHit || isUsingSkill || skillIndex >= skills.size()) {
            return false;
        }

        Skill skill = skills.get(skillIndex);
        if (skill.tryUse()) {
            currentSkill = skill;
            isUsingSkill = true;
            skillFrameCounter = skill.getStartupFrames() + skill.getActiveFrames() + skill.getRecoveryFrames();
            return true;
        }
        return false;
    }

    /**
     * 受到伤害
     */
    public void takeDamage(int damage) {
        if (invincibleFrames > 0) return;

        // 防御减伤
        if (isDefending) {
            damage = damage * (100 - Constants.DEFENSE_DAMAGE_REDUCTION) / 100;
        }

        currentHp -= damage;
        if (currentHp <= 0) {
            currentHp = 0;
            isDead = true;
        }

        isHit = true;
        hitStunFrames = 10;
    }

    /**
     * 重置角色状态
     */
    public void reset(double startX) {
        this.currentHp = maxHp;
        this.position.setX(startX);
        this.position.setY(Constants.GROUND_Y - height);
        this.velocityX = 0;
        this.velocityY = 0;
        this.isDead = false;
        this.isHit = false;
        this.isUsingSkill = false;
        this.isDefending = false;
        this.isDodging = false;
        this.onGround = true;
        for (Skill skill : skills) {
            skill.resetCooldown();
        }
    }

    /**
     * 绘制角色（简单矩形版本）
     */
    public void draw(Graphics2D g) {
        // 计算绘制位置
        int drawX = (int) position.getX();
        int drawY = (int) position.getY();

        // 闪烁效果（受击/无敌）
        if (isHit && hitStunFrames % 2 == 0) return;
        if (invincibleFrames > 0 && invincibleFrames % 3 == 0) return;

        // 绘制角色身体
        g.setColor(getColor());
        g.fillRect(drawX, drawY, width, height);

        // 绘制方向指示（眼睛）
        g.setColor(Color.WHITE);
        int eyeX = facing == 1 ? drawX + width - 20 : drawX + 10;
        g.fillOval(eyeX, drawY + 15, 10, 10);

        // 防御状态指示
        if (isDefending) {
            g.setColor(new Color(100, 100, 255, 100));
            g.fillRect(drawX - 5, drawY - 5, width + 10, height + 10);
        }

        // 技能使用指示
        if (isUsingSkill && currentSkill != null) {
            g.setColor(new Color(255, 255, 0, 150));
            g.fillOval(drawX - 10, drawY - 10, width + 20, height + 20);
        }

        // 闪避状态指示
        if (isDodging) {
            g.setColor(new Color(200, 200, 200, 100));
            g.fillRect(drawX, drawY, width, height);
        }
    }

    /**
     * 获取攻击判定框
     */
    public Rectangle getAttackHitbox() {
        if (currentSkill == null) return null;

        int hitboxX = facing == 1 ?
            (int) position.getX() + width :
            (int) position.getX() - currentSkill.getRange();

        return new Rectangle(hitboxX, (int) position.getY(),
            currentSkill.getRange(), height);
    }

    /**
     * 获取身体判定框
     */
    public Rectangle getBodyHitbox() {
        return new Rectangle((int) position.getX(), (int) position.getY(), width, height);
    }

    // Getters and Setters
    public String getName() { return name; }
    public int getMaxHp() { return maxHp; }
    public int getCurrentHp() { return currentHp; }
    public Position getPosition() { return position; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getFacing() { return facing; }
    public boolean isDead() { return isDead; }
    public boolean isDefending() { return isDefending; }
    public boolean isUsingSkill() { return isUsingSkill; }
    public boolean isHit() { return isHit; }
    public boolean isDodging() { return isDodging; }
    public List<Skill> getSkills() { return skills; }
    public Skill getCurrentSkill() { return currentSkill; }
    public boolean isOnGround() { return onGround; }

    public void setFacing(int facing) { this.facing = facing; }
}
