package com.baioretto.debugkeeper.listener;

import com.baioretto.debugkeeper.network.PlayerDuplexAdaptor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        PlayerDuplexAdaptor.register(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        onPlayerLeave(event.getPlayer());
    }

    @EventHandler
    public void onPlayerLeave(PlayerKickEvent event) {
        onPlayerLeave(event.getPlayer());
    }

    private void onPlayerLeave(Player player) {
        PlayerDuplexAdaptor.unregister(player);
    }
}
