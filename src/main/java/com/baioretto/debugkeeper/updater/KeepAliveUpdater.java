package com.baioretto.debugkeeper.updater;

import com.baioretto.debugkeeper.DebugKeeper;
import com.baioretto.debugkeeper.exception.DebugHelperInternalException;
import com.baioretto.debugkeeper.util.FieldGetter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.game.ClientboundKeepAlivePacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.System.currentTimeMillis;

public final class KeepAliveUpdater extends TimerTask {
    private final AtomicLong timer;
    private final AtomicInteger counter;

    public static boolean isDebugging;

    private KeepAliveUpdater() {
        timer = new AtomicLong(currentTimeMillis());
        counter = new AtomicInteger(0);
    }

    @Override
    public void run() {
        // we don't run below if state not match RUNNABLE
        if (!Thread.State.RUNNABLE.equals(DebugKeeper.mainThread.getState())) {
            isDebugging = false;
            return;
        }

        // current time - 1s > the latest update time, represent interval more than 1s
        if (currentTimeMillis() - 1100L > timer.get()) {
            resetCounter();
        }
        synchronousTimer();

        if (counter.incrementAndGet() < 20) return;
        resetCounter();

        Bukkit.getServer().getOnlinePlayers().forEach(this::setKeepAliveTime);

        if (DebugKeeper.log) DebugKeeper.sendConsoleMessage(Component.text("Reset player alive time!", NamedTextColor.GREEN));
    }

    private void resetCounter() {
        counter.set(0);
    }

    private void synchronousTimer() {
        timer.set(currentTimeMillis());
    }

    private void setKeepAliveTime(Player player) {
        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        long millis = net.minecraft.Util.getMillis();

        // mock ServerGamePacketListenerImpl update alive time
        try {
            KEEP_ALIVE_TIME.set(connection, millis + 100);
        } catch (IllegalAccessException e) {
            throw new DebugHelperInternalException(e);
        }

        if (!isDebugging) {
            isDebugging = true;
        }

        connection.send(new ClientboundKeepAlivePacket(millis));
    }

    private final static Field KEEP_ALIVE_TIME;
    private final static Timer TASK_SCHEDULER;
    private final static KeepAliveUpdater instance;

    static {
        KEEP_ALIVE_TIME = FieldGetter.getKeepAliveTime();

        TASK_SCHEDULER = new Timer();
        instance = new KeepAliveUpdater();
    }

    /**
     * doStart auto update task
     */
    public static void doStart() {
        TASK_SCHEDULER.schedule(instance, 0L, 1000L);
    }

    /**
     * close auto update task
     */
    public static void doStop() {
        TASK_SCHEDULER.cancel();
    }
}
