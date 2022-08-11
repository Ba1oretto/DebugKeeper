package com.baioretto.debugkeeper.listener;

import com.baioretto.debugkeeper.core.PlayerDuplexAdaptor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class GameListener implements Listener {
    @EventHandler
    public void onServerReload(ServerLoadEvent event) {
        if (!ServerLoadEvent.LoadType.RELOAD.equals(event.getType())) return;
        PlayerDuplexAdaptor.registerAll();
    }
}
