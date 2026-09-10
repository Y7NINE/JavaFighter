package com.fighting.system;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 嘲讽系统 - 战斗失败时敌人嘲讽
 */
public class TauntSystem {

    // 嘲讽话术列表
    private static final List<String> TAUNTS = new ArrayList<>();
    private static final Random RANDOM = new Random();

    static {
        TAUNTS.add("就这？我还以为多厉害呢！");
        TAUNTS.add("回去再练练吧，菜鸟！");
        TAUNTS.add("你的操作让我想起了我的奶奶...");
        TAUNTS.add("这就是你的全部实力吗？太让我失望了！");
        TAUNTS.add("别灰心，毕竟不是人人都能成为格斗家的~");
        TAUNTS.add("你确定你是在玩游戏而不是被游戏玩？");
        TAUNTS.add("我已经在让着你了，结果你还是输了...");
        TAUNTS.add("加油，再练个十年应该就能打败我了！");
        TAUNTS.add("你的水平让我怀疑你是不是闭着眼睛打的~");
        TAUNTS.add("别哭了，下次我会轻一点的~");
    }

    /**
     * 随机获取一条嘲讽话术
     * @return 嘲讽话术
     */
    public static String getRandomTaunt() {
        int index = RANDOM.nextInt(TAUNTS.size());
        return TAUNTS.get(index);
    }

    /**
     * 获取所有嘲讽话术
     * @return 嘲讽话术列表
     */
    public static List<String> getAllTaunts() {
        return new ArrayList<>(TAUNTS);
    }
}
