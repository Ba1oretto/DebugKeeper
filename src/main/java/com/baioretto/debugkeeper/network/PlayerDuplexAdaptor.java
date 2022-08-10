package com.baioretto.debugkeeper.network;

import com.baioretto.debugkeeper.updater.KeepAliveUpdater;
import io.netty.channel.*;
import net.minecraft.network.protocol.game.ServerboundKeepAlivePacket;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public final class PlayerDuplexAdaptor {
    private final static String handlerName = "debug_helper_packet_handler";

    public static void register(Player player) {
        getChannel(player).pipeline().addBefore("packet_handler", handlerName, new ChannelDuplexHandler() {
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
                try {
                    super.write(ctx, msg, promise);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                if (msg instanceof ServerboundKeepAlivePacket && KeepAliveUpdater.isDebugging) {
                    return;
                }

                try {
                    super.channelRead(ctx, msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void unregister(Player player) {
        Channel channel = getChannel(player);
        channel.eventLoop().submit(() -> {
            ChannelPipeline pipeline = channel.pipeline();
            if (pipeline.get(handlerName) == null) return;
            pipeline.remove(handlerName);
        });
    }

    private static Channel getChannel(Player player) {
        return ((CraftPlayer) player).getHandle().connection.getConnection().channel;
    }

    private PlayerDuplexAdaptor() {
        throw new UnsupportedOperationException();
    }
}
