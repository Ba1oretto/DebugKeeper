package com.baioretto.debugkeeper;

import com.baioretto.debugkeeper.core.PlayerDuplexAdaptor;
import com.baioretto.debugkeeper.listener.GameListener;
import com.baioretto.debugkeeper.listener.PlayerListener;
import com.baioretto.debugkeeper.core.KeepAliveUpdater;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.NamedTextColor.*;

@SuppressWarnings("unused")
public class DebugKeeper extends JavaPlugin {
    public static Thread mainThread;
    public final static boolean enable = Boolean.getBoolean("debughelper.enable");
    public final static boolean log = Boolean.getBoolean("debughelper.log");

    @Override
    public void onLoad() {
        sendConsoleMessage(text("enable debug keeper: ", YELLOW).append(text(enable, enable ? GREEN : RED)));
    }

    @Override
    public void onEnable() {
        if (enable) {
            Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
            Bukkit.getPluginManager().registerEvents(new GameListener(), this);
            KeepAliveUpdater.doStart();
        }
    }

    @Override
    public void onDisable() {
        if (enable) {
            PlayerDuplexAdaptor.unregisterAll();
            KeepAliveUpdater.doStop();
        }
    }

    public DebugKeeper() {
        mainThread = Thread.currentThread();
    }

    public static void sendConsoleMessage(Component messages) {
        Bukkit.getServer().getConsoleSender().sendMessage(LegacyComponentSerializer.legacySection().serialize(Component.text("[DebugKeeper] ", AQUA).append(messages)));
    }
}