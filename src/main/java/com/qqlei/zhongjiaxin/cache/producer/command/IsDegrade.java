package com.qqlei.zhongjiaxin.cache.producer.command;

/**
 * Created by 李雷 on 2017/11/6.
 */
public class IsDegrade {

    private static boolean degrade = false;

    public static boolean isDegrade() {
        return degrade;
    }

    public static void setDegrade(boolean degrade) {
        IsDegrade.degrade = degrade;
    }

}
