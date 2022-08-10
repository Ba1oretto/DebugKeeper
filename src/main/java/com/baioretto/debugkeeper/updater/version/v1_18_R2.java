package com.baioretto.debugkeeper.updater.version;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public class v1_18_R2 implements BaseVersion {
    @Override
    public Field getKeepAliveTime() {
        try {
            return Class.forName("net.minecraft.server.network.PlayerConnection").getDeclaredField("g");
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
