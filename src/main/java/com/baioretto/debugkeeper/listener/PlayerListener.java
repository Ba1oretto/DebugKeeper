package com.baioretto.debugkeeper.listener;

import com.baioretto.debugkeeper.core.PlayerDuplexAdaptor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerDuplexAdaptor.register(event.getPlayer(), true);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        PlayerDuplexAdaptor.unregister(event.getPlayer(), true);
    }
}
