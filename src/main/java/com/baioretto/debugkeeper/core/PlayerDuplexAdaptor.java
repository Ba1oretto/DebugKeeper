package com.baioretto.debugkeeper.core;

import com.baioretto.debugkeeper.DebugKeeper;
import com.baioretto.debugkeeper.compatible.packet.IPacketUtil;
import com.baioretto.debugkeeper.compatible.player.IPlayerUtil;
import io.netty.channel.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;

public final class PlayerDuplexAdaptor {
    private final static String PACKET_HANDLER_NAME = "debug_helper_packet_handler";
    private final static IPacketUtil PACKET_UTIL = IPacketUtil.impl();

    public static void registerAll() {
        var onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        if (DebugKeeper.log && !onlinePlayers.isEmpty()) DebugKeeper.sendConsoleMessage(Component.text("Attempt to register the packet handler to player: ", NamedTextColor.YELLOW).append(Component.text(generateMessage(onlinePlayers), NamedTextColor.GREEN)));
        onlinePlayers.forEach(PlayerDuplexAdaptor::register);
    }

    public static void unregisterAll() {
        var onlinePlayers = Bukkit.getServer().getOnlinePlayers();
        if (DebugKeeper.log && !onlinePlayers.isEmpty()) DebugKeeper.sendConsoleMessage(Component.text("Attempt to unregister the packet handler to player: ", NamedTextColor.YELLOW).append(Component.text(generateMessage(onlinePlayers), NamedTextColor.GREEN)));
        onlinePlayers.forEach(PlayerDuplexAdaptor::unregister);
    }

    public static void register(Player player) {
        register(player, false);
    }

    public static void register(Player player, boolean log) {
        ChannelPipeline pipeline = getChannel(player).pipeline();
        if (pipeline.get(PACKET_HANDLER_NAME) != null) return;
        if (log && DebugKeeper.log) DebugKeeper.sendConsoleMessage(Component.text("Attempt to register the packet handler to player: ", NamedTextColor.YELLOW).append(Component.text(player.getDisplayName(), NamedTextColor.GREEN)));
        pipeline.addBefore("packet_handler", PACKET_HANDLER_NAME, new ChannelDuplexHandler() {
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
                if (KeepAliveUpdater.isDebugging && PACKET_UTIL.getServerboundKeepAlivePacketClass().isInstance(msg)) {
                    if (DebugKeeper.log) DebugKeeper.sendConsoleMessage(Component.text("Block incoming packet!", NamedTextColor.YELLOW));
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
        unregister(player, false);
    }

    public static void unregister(Player player, boolean log) {
        if (log && DebugKeeper.log) DebugKeeper.sendConsoleMessage(Component.text("Attempt to unregister the packet handler to player: ", NamedTextColor.YELLOW).append(Component.text(player.getDisplayName(), NamedTextColor.GREEN)));
        Channel channel = getChannel(player);
        channel.eventLoop().submit(() -> {
            ChannelPipeline pipeline = channel.pipeline();
            if (pipeline.get(PACKET_HANDLER_NAME) == null) return;
            pipeline.remove(PACKET_HANDLER_NAME);
        });
    }

    private static Channel getChannel(Player player) {
        IPlayerUtil playerUtil = IPlayerUtil.impl();
        return (Channel) playerUtil.getChannel(playerUtil.getNetworkManager(playerUtil.getPlayerConnection(playerUtil.getEntityPlayer(player))));
    }

    private static String generateMessage(Collection<? extends Player> onlinePlayers) {
        StringBuilder sb = new StringBuilder();
        onlinePlayers.forEach(player -> sb.append(player.getDisplayName()).append(", "));
        var msg = sb.toString().trim();
        return msg.length() == 0 ? "" : msg.substring(0, msg.length() - 1);
    }

    private PlayerDuplexAdaptor() {
        throw new UnsupportedOperationException();
    }
}