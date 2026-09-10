package com.fighting;

import com.fighting.core.GameEngine;

/**
 * 格斗游戏主入口
 */
public class GameApp {
    public static void main(String[] args) {
        // 在EDT线程中启动游戏
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GameEngine engine = new GameEngine();
                engine.start();
            }
        });
    }
}
