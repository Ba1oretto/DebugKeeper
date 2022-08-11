package com.baioretto.debugkeeper.core;

import com.baioretto.debugkeeper.DebugKeeper;
import com.baioretto.debugkeeper.compatible.packet.IPacketUtil;
import com.baioretto.debugkeeper.compatible.player.IPlayerUtil;
import com.baioretto.debugkeeper.compatible.util.IGameUtil;
import com.baioretto.debugkeeper.util.Reflections;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;
import org.spigotmc.WatchdogThread;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.System.currentTimeMillis;

public final class KeepAliveUpdater extends TimerTask {
    private final AtomicLong timer;
    private final AtomicInteger counter;
    private final IPlayerUtil playerUtil;

    public static boolean isDebugging;

    private KeepAliveUpdater() {
        timer = new AtomicLong(currentTimeMillis());
        counter = new AtomicInteger(0);
        playerUtil = IPlayerUtil.impl();
    }

    @Override
    public void run() {
        // we don't run below if state not match RUNNABLE
        if (!Thread.State.RUNNABLE.equals(DebugKeeper.mainThread.getState())) {
            if (isDebugging) isDebugging = false;
            if (getWatchdogIsStopping()) {
                setWatchdogIsStopping(false);
                WatchdogThread.doStart(SpigotConfig.timeoutTime, SpigotConfig.restartOnCrash);
            }
            return;
        }

        // current time - 1s > the latest update time, represent interval more than 1s
        if (currentTimeMillis() - 1100L > timer.get()) {
            resetCounter();
        }
        synchronousTimer();

        if (counter.get() >= DebugKeeper.earlyWarningEvery) {
            if (!getWatchdogIsStopping()) {
                WatchdogThread.doStop();
            }
        }

        if (counter.incrementAndGet() < 20) return;
        resetCounter();

        Bukkit.getServer().getOnlinePlayers().forEach(this::setKeepAliveTime);

        if (DebugKeeper.log)
            DebugKeeper.sendConsoleMessage(Component.text("Reset player alive time!", NamedTextColor.GREEN));
    }

    private void resetCounter() {
        counter.set(0);
    }

    private void synchronousTimer() {
        timer.set(currentTimeMillis());
    }

    private void setKeepAliveTime(Player player) {
        Object connection = playerUtil.getPlayerConnection(playerUtil.getEntityPlayer(player));
        long millis = IGameUtil.impl().getMillis();

        // mock ServerGamePacketListenerImpl update alive time
        playerUtil.setKeepAliveTime(connection, millis + 100L);

        if (!isDebugging) isDebugging = true;

        playerUtil.sendPacket(connection, IPacketUtil.impl().getClientboundKeepAlivePacket(millis));
    }

    private boolean getWatchdogIsStopping() {
        return getWatchdogInstance() != null && Boolean.parseBoolean(Reflections.get(WATCHDOG_STOPPING_FIELD, getWatchdogInstance()).toString());
    }

    @SuppressWarnings("SameParameterValue")
    private void setWatchdogIsStopping(boolean flag) {
        if (getWatchdogInstance() == null) return;
        Reflections.set(WATCHDOG_STOPPING_FIELD, getWatchdogInstance(), flag);
    }

    private Object getWatchdogInstance() {
        if (WATCHDOG_INSTANCE == null) synchronized (KeepAliveUpdater.class) {
            if (WATCHDOG_INSTANCE == null) WATCHDOG_INSTANCE = Reflections.get(WATCHDOG_INSTANCE_FIELD, null);
        }
        return WATCHDOG_INSTANCE;
    }

    private final static Timer TASK_SCHEDULER = new Timer();
    private final static KeepAliveUpdater instance = new KeepAliveUpdater();
    private static volatile Object WATCHDOG_INSTANCE;
    private final static Field WATCHDOG_STOPPING_FIELD = Reflections.getField(WatchdogThread.class, "stopping");
    private final static Field WATCHDOG_INSTANCE_FIELD = Reflections.getField(WatchdogThread.class, "instance");

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
