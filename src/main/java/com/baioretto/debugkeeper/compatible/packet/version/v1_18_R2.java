package com.baioretto.debugkeeper.compatible.packet.version;

import com.baioretto.debugkeeper.compatible.packet.IPacketUtil;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class v1_18_R2 extends IPacketUtil {
    @Override
    protected @NotNull String getClientboundKeepAlivePacketClassName() {
        return "net.minecraft.network.protocol.game.PacketPlayOutKeepAlive";
    }

    @Override
    protected @NotNull String getServerboundKeepAlivePacketClassName() {
        return "net.minecraft.network.protocol.game.PacketPlayInKeepAlive";
    }
}
