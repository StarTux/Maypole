package com.winthier.maypole.session;

import com.winthier.maypole.MaypolePlugin;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@RequiredArgsConstructor
public final class Sessions implements Listener {
    private final MaypolePlugin plugin;
    private Map<UUID, Session> sessions = new HashMap<>();

    public void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (Player player : Bukkit.getOnlinePlayers()) {
            enter(player);
        }
    }

    public void reload() {
        sessions.clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            enter(player);
        }
    }

    private void enter(Player player) {
        Session session = new Session(player.getUniqueId());
        sessions.put(session.uuid, session);
        session.enable();
    }

    private void exit(Player player) {
        sessions.remove(player.getUniqueId());
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        enter(event.getPlayer());
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        exit(event.getPlayer());
    }

    public Session get(Player player) {
        return sessions.get(player.getUniqueId());
    }

    public void apply(UUID uuid, Consumer<Session> callback) {
        Session session = sessions.get(uuid);
        if (session == null) {
            session = new Session(uuid);
            session.enableCallbacks.add(callback);
            session.enable();
        } else if (!session.isEnabled()) {
            session.enableCallbacks.add(callback);
        } else {
            callback.accept(session);
        }
    }
}
