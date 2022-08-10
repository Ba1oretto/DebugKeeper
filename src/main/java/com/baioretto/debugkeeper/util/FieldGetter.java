package com.baioretto.debugkeeper.util;

import com.baioretto.debugkeeper.exception.DebugHelperInternalException;
import com.baioretto.debugkeeper.updater.version.BaseVersion;
import org.bukkit.Bukkit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public final class FieldGetter {
    private static volatile BaseVersion impl;

    public static Field getKeepAliveTime() {
        return setAccessible(getImpl().getKeepAliveTime(), true);
    }

    public static Field setAccessible(Field field, boolean flag) {
        field.setAccessible(flag);
        return field;
    }

    public static BaseVersion getImpl() {
        if (FieldGetter.impl == null) {
            try {
                Class<?> impl = Class.forName(String.format("%s.%s", "com.baioretto.debugkeeper.updater.version", version));
                Constructor<?> constructor = impl.getDeclaredConstructor();
                constructor.setAccessible(true);
                FieldGetter.impl = (BaseVersion) constructor.newInstance();
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                     IllegalAccessException e) {
                throw new DebugHelperInternalException(e);
            }
        }
        return FieldGetter.impl;
    }

    private final static String version;
    static {
        String[] packageNameArray = Bukkit.getServer().getClass().getPackageName().split("\\.");
        version = packageNameArray[packageNameArray.length - 1];
    }
}
