package com.fighting.core;

import com.fighting.system.UserManager;
import com.fighting.ui.MainFrame;
import com.fighting.util.Constants;

/**
 * 游戏引擎 - 主循环控制
 */
public class GameEngine implements Runnable {

    private final MainFrame mainFrame;
    private final InputHandler inputHandler;
    private final UserManager userManager;
    private GameState currentState;
    private boolean running;
    private Thread gameThread;

    // 帧率计算
    private int fps;
    private long fpsTimer;

    public GameEngine() {
        this.inputHandler = new InputHandler();
        this.userManager = new UserManager();
        this.mainFrame = new MainFrame(this, inputHandler, userManager);
        this.currentState = GameState.LOGIN;
        this.running = false;
    }

    /**
     * 启动游戏
     */
    public void start() {
        if (running) return;
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
        mainFrame.setVisible(true);
    }

    /**
     * 停止游戏
     */
    public void stop() {
        running = false;
        try {
            if (gameThread != null) {
                gameThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double delta = 0;
        int frames = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / (1_000_000_000.0 / Constants.FPS);
            lastTime = now;

            // 更新游戏逻辑
            while (delta >= 1) {
                update();
                delta--;
            }

            // 渲染
            render();
            frames++;

            // 计算FPS
            if (System.currentTimeMillis() - timer >= 1000) {
                fps = frames;
                frames = 0;
                timer += 1000;
            }

            // 控制帧率
            long sleepTime = Constants.FRAME_TIME - (System.nanoTime() - lastTime) / 1_000_000;
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 更新游戏逻辑
     */
    private void update() {
        switch (currentState) {
            case MENU:
                // 菜单状态由UI处理
                break;
            case SELECT:
                // 角色选择状态
                break;
            case BATTLE:
                // 战斗状态 - 由BattlePanel处理
                mainFrame.updateBattle();
                break;
            case ROUND_END:
            case GAME_OVER:
                // 结算状态
                break;
            default:
                break;
        }
        inputHandler.update();
    }

    /**
     * 渲染画面
     */
    private void render() {
        mainFrame.repaint();
    }

    /**
     * 切换游戏状态
     */
    public void changeState(GameState newState) {
        this.currentState = newState;
        mainFrame.showPanel(newState);
    }

    /**
     * 获取当前状态
     */
    public GameState getCurrentState() {
        return currentState;
    }

    /**
     * 获取FPS
     */
    public int getFps() {
        return fps;
    }

    /**
     * 获取主窗口
     */
    public MainFrame getMainFrame() {
        return mainFrame;
    }
}
