package com.fighting.core;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;
import java.util.Set;

/**
 * 输入处理器 - 处理键盘输入
 */
public class InputHandler implements KeyListener {

    // 当前按下的键
    private final Set<Integer> pressedKeys = new HashSet<>();
    // 刚按下的键（用于单次触发）
    private final Set<Integer> justPressedKeys = new HashSet<>();

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        if (!pressedKeys.contains(keyCode)) {
            justPressedKeys.add(keyCode);
        }
        pressedKeys.add(keyCode);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // 不使用
    }

    /**
     * 检查键是否正在被按下
     */
    public boolean isKeyDown(int keyCode) {
        return pressedKeys.contains(keyCode);
    }

    /**
     * 检查键是否刚被按下（单次触发）
     */
    public boolean isKeyJustPressed(int keyCode) {
        return justPressedKeys.contains(keyCode);
    }

    /**
     * 清除刚按下的键状态（每帧调用）
     */
    public void update() {
        justPressedKeys.clear();
    }

    /**
     * 清除所有按键状态
     */
    public void clearAll() {
        pressedKeys.clear();
        justPressedKeys.clear();
    }
}
