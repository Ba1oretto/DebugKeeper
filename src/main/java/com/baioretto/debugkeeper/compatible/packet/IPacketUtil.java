package com.baioretto.debugkeeper.compatible.packet;

import com.baioretto.debugkeeper.compatible.VersionCompatible;
import com.baioretto.debugkeeper.util.Reflections;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;

import static com.baioretto.debugkeeper.util.Reflections.*;

public abstract class IPacketUtil {
    private final Constructor<?> clientboundKeepAlivePacketConstructor;
    private final Class<?> serverboundKeepAlivePacketClass;

    protected IPacketUtil() {
        clientboundKeepAlivePacketConstructor = Reflections.getConstructor(getClientboundKeepAlivePacketClassName(), long.class);
        serverboundKeepAlivePacketClass = Reflections.getClass(getServerboundKeepAlivePacketClassName());
    }

    protected abstract @NotNull String getClientboundKeepAlivePacketClassName();

    protected abstract @NotNull String getServerboundKeepAlivePacketClassName();

    public Object getClientboundKeepAlivePacket(long id) {
        return newInstance(clientboundKeepAlivePacketConstructor, id);
    }

    public Class<?> getServerboundKeepAlivePacketClass() {
        return serverboundKeepAlivePacketClass;
    }

    public static IPacketUtil impl() {
        switch (VersionCompatible.version) {
            case v1_18, v1_18_R1, v1_18_R2, v1_19, v1_19_R1, v1_19_R2 -> {
                return VersionCompatible.get(IPacketUtil.class, VersionCompatible.SupportVersion.v1_18_R2);
            }
            default -> throw new UnsupportedOperationException();
        }
    }
}
