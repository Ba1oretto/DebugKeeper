package com.baioretto.debugkeeper.compatible.player;

import com.baioretto.debugkeeper.compatible.VersionCompatible;
import com.baioretto.debugkeeper.util.Reflections;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static com.baioretto.debugkeeper.util.Reflections.*;

public abstract class IPlayerUtil {
    private final Field playerConnectionField;
    private final Field keepAliveTimeField;
    private final Field channelField;

    private final Method sendMethod;
    private final Method getHandleMethod;
    private final Method getNetworkManagerMethod;

    protected IPlayerUtil() {
        Class<?> craftPlayerClass = Reflections.getClass(getCraftPlayerClassName());
        Class<?> entityPlayerClass = Reflections.getClass(getEntityPlayerClassName());
        Class<?> playerConnectionClass = Reflections.getClass(getPlayerConnectionClassName());
        Class<?> packetClass = Reflections.getClass(getPacketClassName());
        Class<?> networkManagerClass = Reflections.getClass(getNetworkManagerClassName());

        playerConnectionField = getField(entityPlayerClass, getPlayerConnectionFieldName());
        keepAliveTimeField = getField(playerConnectionClass, getKeepAliveTimeFieldName());
        channelField = getField(networkManagerClass, getChannelFieldName());

        getHandleMethod = getMethod(craftPlayerClass, getGetHandleMethodName());
        sendMethod = getMethod(playerConnectionClass, getSendMethodName(), packetClass);

        getNetworkManagerMethod = getMethod(playerConnectionClass, getNetworkManagerMethodName());
    }

    protected @NotNull String getCraftPlayerClassName() {
        String[] version = Bukkit.getServer().getClass().getPackageName().split("\\.");
        return "org.bukkit.craftbukkit." + version[version.length - 1] + ".entity.CraftPlayer";
    }

    protected @NotNull String getEntityPlayerClassName() {
        return "net.minecraft.server.level.EntityPlayer";
    }

    protected @NotNull String getPlayerConnectionClassName() {
        return "net.minecraft.server.network.PlayerConnection";
    }

    protected @NotNull String getNetworkManagerClassName() {
        return "net.minecraft.network.NetworkManager";
    }

    protected @NotNull String getPacketClassName() {
        return "net.minecraft.network.protocol.Packet";
    }

    protected @NotNull String getPlayerConnectionFieldName() {
        return "b";
    }

    protected abstract @NotNull String getKeepAliveTimeFieldName();

    protected @NotNull String getChannelFieldName() {
        return "m";
    }

    protected @NotNull String getGetHandleMethodName() {
        return "getHandle";
    }

    protected @NotNull String getSendMethodName() {
        return "a";
    }

    protected @NotNull String getNetworkManagerMethodName() {
        return "a";
    }

    public void sendPacket(Object playerConnection, Object packet) {
        invoke(sendMethod, playerConnection, packet);
    }

    public void setKeepAliveTime(Object playerConnection, long value) {
        set(keepAliveTimeField, playerConnection, value);
    }

    public Object getEntityPlayer(Object player) {
        return invoke(getHandleMethod, player);
    }

    public Object getPlayerConnection(Object entityPlayer) {
        return get(playerConnectionField, entityPlayer);
    }

    public Object getNetworkManager(Object playerConnection) {
        return invoke(getNetworkManagerMethod, playerConnection);
    }

    public Object getChannel(Object networkManager) {
        return get(channelField, networkManager);
    }

    public static IPlayerUtil impl() {
        switch (VersionCompatible.version) {
            case v1_18, v1_18_R1 -> {
                return VersionCompatible.get(IPlayerUtil.class, VersionCompatible.SupportVersion.v1_18);
            }
            case v1_18_R2 -> {
                return VersionCompatible.get(IPlayerUtil.class, VersionCompatible.SupportVersion.v1_18_R2);
            }
            case v1_19 -> {
                return VersionCompatible.get(IPlayerUtil.class, VersionCompatible.SupportVersion.v1_19);
            }
            case v1_19_R1, v1_19_R2 -> {
                return VersionCompatible.get(IPlayerUtil.class, VersionCompatible.SupportVersion.v1_19_R1);
            }
            default -> throw new UnsupportedOperationException();
        }
    }
}