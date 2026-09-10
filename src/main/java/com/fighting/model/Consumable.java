package com.fighting.model;

/**
 * 消耗品/道具类
 */
public class Consumable {

    private String name;  // 道具名称
    private int num;      // 恢复血量的数值

    public Consumable(String name, int num) {
        this.name = name;
        this.num = num;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public String toString() {
        return name + "(+" + num + "HP)";
    }
}
