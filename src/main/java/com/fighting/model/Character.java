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
     * 绘制角色（带动画效果）
     */
    public void draw(Graphics2D g) {
        int drawX = (int) position.getX();
        int drawY = (int) position.getY();

        // 闪烁效果（受击/无敌）
        if (isHit && hitStunFrames % 2 == 0) return;
        if (invincibleFrames > 0 && invincibleFrames % 3 == 0) return;

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 保存原始颜色
        Color bodyColor = getColor();

        // 绘制阴影
        g.setColor(new Color(0, 0, 0, 50));
        g.fillOval(drawX + 10, Constants.GROUND_Y - 10, width - 20, 15);

        // 根据状态绘制不同动画
        if (isUsingSkill) {
            drawAttackAnimation(g, drawX, drawY, bodyColor);
        } else if (!onGround) {
            drawJumpAnimation(g, drawX, drawY, bodyColor);
        } else if (isMoving) {
            drawMoveAnimation(g, drawX, drawY, bodyColor);
        } else if (isDefending) {
            drawDefendAnimation(g, drawX, drawY, bodyColor);
        } else if (isDodging) {
            drawDodgeAnimation(g, drawX, drawY, bodyColor);
        } else {
            drawIdleAnimation(g, drawX, drawY, bodyColor);
        }
    }

    /**
     * 待机动画
     */
    private void drawIdleAnimation(Graphics2D g, int x, int y, Color color) {
        int bobOffset = (int) (Math.sin(animFrame * 0.2) * 3);

        // 身体
        g.setColor(color);
        g.fillRoundRect(x + 15, y + 30 + bobOffset, 50, 55, 10, 10);

        // 头
        g.setColor(color.brighter());
        g.fillOval(x + 20, y + bobOffset, 40, 35);

        // 眼睛
        g.setColor(Color.WHITE);
        int eyeX = facing == 1 ? x + 42 : x + 25;
        g.fillOval(eyeX, y + 12 + bobOffset, 12, 12);
        g.setColor(Color.BLACK);
        g.fillOval(eyeX + 3, y + 15 + bobOffset, 6, 6);

        // 腿
        g.setColor(color.darker());
        int legOffset = (int) (Math.sin(animFrame * 0.1) * 2);
        g.fillRect(x + 20, y + 85, 15, 35 + legOffset);
        g.fillRect(x + 45, y + 85, 15, 35 - legOffset);

        // 手臂
        g.setColor(color);
        g.fillRect(x - 5, y + 40, 20, 12);
        g.fillRect(x + 65, y + 40, 20, 12);
    }

    /**
     * 移动动画
     */
    private void drawMoveAnimation(Graphics2D g, int x, int y, Color color) {
        int legAnim = (animFrame % 4) * 5;

        // 身体（略微前倾）
        g.setColor(color);
        g.fillRoundRect(x + 15, y + 30, 50, 55, 10, 10);

        // 头
        g.setColor(color.brighter());
        g.fillOval(x + 20, y, 40, 35);

        // 眼睛（看向前方）
        g.setColor(Color.WHITE);
        int eyeX = facing == 1 ? x + 45 : x + 22;
        g.fillOval(eyeX, y + 12, 12, 12);
        g.setColor(Color.BLACK);
        int pupilX = facing == 1 ? eyeX + 4 : eyeX + 2;
        g.fillOval(pupilX, y + 15, 5, 5);

        // 腿（跑步动画）
        g.setColor(color.darker());
        g.fillRect(x + 20 - legAnim, y + 85, 15, 30);
        g.fillRect(x + 45 + legAnim, y + 85, 15, 30);

        // 手臂（摆动）
        g.setColor(color);
        g.fillRect(x - 10 + legAnim, y + 40, 25, 12);
        g.fillRect(x + 65 - legAnim, y + 40, 25, 12);

        // 移动粒子效果
        g.setColor(new Color(255, 255, 255, 100));
        for (int i = 0; i < 3; i++) {
            int px = x + (facing == 1 ? -10 - i * 8 : width + 10 + i * 8);
            int py = y + 50 + i * 15;
            g.fillOval(px, py, 5 - i, 5 - i);
        }
    }

    /**
     * 跳跃动画
     */
    private void drawJumpAnimation(Graphics2D g, int x, int y, Color color) {
        // 身体
        g.setColor(color);
        g.fillRoundRect(x + 15, y + 30, 50, 50, 10, 10);

        // 头
        g.setColor(color.brighter());
        g.fillOval(x + 20, y, 40, 35);

        // 眼睛（向下看）
        g.setColor(Color.WHITE);
        g.fillOval(x + 25, y + 18, 12, 12);
        g.fillOval(x + 43, y + 18, 12, 12);
        g.setColor(Color.BLACK);
        g.fillOval(x + 28, y + 22, 6, 6);
        g.fillOval(x + 46, y + 22, 6, 6);

        // 腿（收缩）
        g.setColor(color.darker());
        g.fillRect(x + 25, y + 80, 12, 25);
        g.fillRect(x + 43, y + 80, 12, 25);

        // 手臂（上举）
        g.setColor(color);
        g.fillRect(x + 5, y + 20, 12, 25);
        g.fillRect(x + 63, y + 20, 12, 25);
    }

    /**
     * 攻击动画
     */
    private void drawAttackAnimation(Graphics2D g, int x, int y, Color color) {
        int attackPhase = skillFrameCounter % 20;

        // 身体（后仰）
        g.setColor(color);
        g.fillRoundRect(x + 15, y + 30, 50, 55, 10, 10);

        // 头
        g.setColor(color.brighter());
        g.fillOval(x + 20, y, 40, 35);

        // 眼睛（专注）
        g.setColor(Color.WHITE);
        int eyeX = facing == 1 ? x + 45 : x + 22;
        g.fillOval(eyeX, y + 12, 12, 12);
        g.setColor(new Color(255, 50, 50));
        g.fillOval(eyeX + 3, y + 15, 6, 6);

        // 腿（稳定）
        g.setColor(color.darker());
        g.fillRect(x + 20, y + 85, 15, 35);
        g.fillRect(x + 45, y + 85, 15, 35);

        // 攻击手臂（伸出）
        g.setColor(color);
        int armExtend = facing == 1 ? 30 + attackPhase : -30 - attackPhase;
        g.fillRect(x + 60, y + 40, 30 + armExtend, 15);

        // 攻击特效
        if (currentSkill != null && attackPhase < 10) {
            g.setColor(new Color(255, 255, 0, 200 - attackPhase * 20));
            int effectX = facing == 1 ? x + width + attackPhase * 5 : x - 30 - attackPhase * 5;
            g.fillOval(effectX, y + 35, 25, 25);

            // 技能名称显示
            g.setColor(new Color(255, 255, 255, 255 - attackPhase * 25));
            g.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            g.drawString(currentSkill.getName(), x + 20, y - 20);
        }
    }

    /**
     * 防御动画
     */
    private void drawDefendAnimation(Graphics2D g, int x, int y, Color color) {
        // 身体（下蹲）
        g.setColor(color);
        g.fillRoundRect(x + 10, y + 40, 60, 50, 10, 10);

        // 头
        g.setColor(color.brighter());
        g.fillOval(x + 20, y + 10, 40, 35);

        // 眼睛（紧闭）
        g.setColor(Color.WHITE);
        g.fillRect(x + 25, y + 22, 10, 3);
        g.fillRect(x + 45, y + 22, 10, 3);

        // 腿（弯曲）
        g.setColor(color.darker());
        g.fillRect(x + 15, y + 90, 20, 25);
        g.fillRect(x + 45, y + 90, 20, 25);

        // 手臂（交叉防御）
        g.setColor(color);
        g.fillRect(x + 5, y + 45, 30, 15);
        g.fillRect(x + 45, y + 45, 30, 15);

        // 防御护盾
        g.setColor(new Color(100, 150, 255, 150));
        g.setStroke(new BasicStroke(3));
        g.drawOval(x - 10, y - 10, width + 20, height + 20);
    }

    /**
     * 闪避动画
     */
    private void drawDodgeAnimation(Graphics2D g, int x, int y, Color color) {
        // 身体（残影效果）
        g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 100));
        g.fillRoundRect(x - facing * 20, y + 30, 50, 55, 10, 10);

        // 当前身体
        g.setColor(color);
        g.fillRoundRect(x + 15, y + 30, 50, 55, 10, 10);

        // 头
        g.setColor(color.brighter());
        g.fillOval(x + 20, y, 40, 35);

        // 眼睛
        g.setColor(Color.WHITE);
        g.fillOval(x + 30, y + 12, 8, 8);

        // 腿（快速移动）
        g.setColor(color.darker());
        g.fillRect(x + 20, y + 85, 15, 30);
        g.fillRect(x + 45, y + 85, 15, 30);

        // 闪避轨迹
        g.setColor(new Color(200, 200, 255, 150));
        for (int i = 0; i < 5; i++) {
            int trailX = x - facing * (i * 15);
            g.fillOval(trailX + 30, y + 50, 20 - i * 3, 20 - i * 3);
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
    public int getSkillFrameCounter() { return skillFrameCounter; }
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
