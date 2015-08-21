package com.andr0day.xposed.hooker;

import java.util.HashSet;
import java.util.Set;

/**
 * @author wangwanchun
 * @date 2015/8/12 14:28
 * @description
 */
public class Hookers {

    public static Hookers instance = new Hookers();

    private Hookers() {
        register(ContextHooker.instance);
    }

    public Set<Hooker> hookers = new HashSet<Hooker>();

    public void startHook() {
        for (Hooker it : hookers) {
            if (it.needHook()) {
                it.startHook();
            }
        }
    }

    public void register(Hooker hooker) {
        hookers.add(hooker);
    }

    public void unregister(Hooker hooker) {
        hookers.remove(hooker);
    }
}
