package com.baioretto.debugkeeper.compatible.util;

import com.baioretto.debugkeeper.compatible.VersionCompatible;
import com.baioretto.debugkeeper.util.Reflections;

import java.lang.reflect.Method;

public abstract class IGameUtil {
    private final Method getMillisMethod;

    protected IGameUtil() {
        getMillisMethod = Reflections.getMethod(getSystemUtilClassName(), getGetMillisMethodName());
    }

    protected String getGetMillisMethodName() {
        return "b";
    }

    protected String getSystemUtilClassName() {
        return "net.minecraft.SystemUtils";
    }

    public long getMillis() {
        return Long.parseLong(String.valueOf(Reflections.invoke(getMillisMethod, null)));
    }

    public static IGameUtil impl() {
        switch (VersionCompatible.version) {
            case v1_18_R1 -> {
                return VersionCompatible.get(IGameUtil.class, VersionCompatible.SupportVersion.v1_18_R1);
            }
            case v1_18, v1_18_R2, v1_19, v1_19_R1, v1_19_R2 -> {
                return VersionCompatible.get(IGameUtil.class, VersionCompatible.SupportVersion.v1_18);
            }
            default -> throw new UnsupportedOperationException();
        }
    }
}
