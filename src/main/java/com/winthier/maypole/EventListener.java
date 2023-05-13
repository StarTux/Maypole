package com.winthier.maypole;

import com.cavetale.core.event.hud.PlayerHudEvent;
import com.cavetale.core.event.hud.PlayerHudPriority;
import com.cavetale.core.font.Unicode;
import com.winthier.maypole.session.Session;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import static com.cavetale.core.exploits.PlayerPlacedBlocks.isPlayerPlaced;
import static com.winthier.maypole.MaypoleAction.*;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK;
import static org.bukkit.inventory.EquipmentSlot.HAND;

@RequiredArgsConstructor
public final class EventListener implements Listener {
    protected static final List<String> WORLDS = List.of("mine", "mine_nether", "mine_the_end");
    private final MaypolePlugin plugin;
    private final EnumMap<Material, List<MaypoleAction>> blockActions = new EnumMap<>(Material.class);
    private final EnumMap<EntityType, List<MaypoleAction>> entityActions = new EnumMap<>(EntityType.class);

    protected void enable() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (MaypoleAction action : MaypoleAction.values()) {
            switch (action.type) {
            case INVALID: break;
            case BLOCK_BREAK:
            case BUCKET_FILL:
                for (Material material : action.materials) {
                    blockActions.computeIfAbsent(material, m -> new ArrayList<>()).add(action);
                }
                break;
            case ENTITY_KILL:
                for (EntityType entityType : action.entityTypes) {
                    entityActions.computeIfAbsent(entityType, e -> new ArrayList<>()).add(action);
                }
                break;
            default: throw new IllegalStateException(action.type.name());
            }
        }
        EnumSet<MaypoleAction> unusedActions = EnumSet.allOf(MaypoleAction.class);
        unusedActions.remove(NONE);
        for (List<MaypoleAction> list : blockActions.values()) {
            for (MaypoleAction action : list) {
                unusedActions.remove(action);
            }
        }
        for (List<MaypoleAction> list : entityActions.values()) {
            for (MaypoleAction action : list) {
                unusedActions.remove(action);
            }
        }
        if (!unusedActions.isEmpty()) {
            plugin.getLogger().warning("[EventListener] Unused MaypoleActions: " + unusedActions);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    private void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.tag.enabled) return;
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        if (!WORLDS.contains(block.getWorld().getName())) return;
        if (isPlayerPlaced(block)) return;
        List<MaypoleAction> list = blockActions.get(block.getType());
        if (list != null) {
            for (MaypoleAction action : list) {
                if (action.type == MaypoleAction.Type.BLOCK_BREAK && action.checkBlock(block)) {
                    plugin.unlockCollectible(player, block, action);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!plugin.tag.enabled) return;
        LivingEntity entity = event.getEntity();
        if (!WORLDS.contains(entity.getWorld().getName())) return;
        List<MaypoleAction> list = entityActions.get(entity.getType());
        if (list != null) {
            Player killer = entity.getKiller();
            if (killer == null) return;
            Block block = entity.getEyeLocation().getBlock();
            for (MaypoleAction action : list) {
                if (action.type == MaypoleAction.Type.ENTITY_KILL && action.checkBlock(block)) {
                    plugin.unlockCollectible(killer, block, action);
                }
            }
        }
    }

    @EventHandler(priority  = EventPriority.HIGHEST)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (!plugin.tag.enabled) return;
        final Player player = event.getPlayer();
        final Block block = event.getBlockClicked();
        if (!WORLDS.contains(block.getWorld().getName())) return;
        if (isPlayerPlaced(block)) return;
        List<MaypoleAction> list = blockActions.get(block.getType());
        if (list != null) {
            for (MaypoleAction action : list) {
                if (action.type == MaypoleAction.Type.BUCKET_FILL && action.checkBlock(block)) {
                    plugin.unlockCollectible(player, block, action);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.tag.enabled) return;
        if (event.getHand() == HAND && event.getAction() == RIGHT_CLICK_BLOCK
            && event.hasBlock() && plugin.tag.pole.isAt(event.getClickedBlock())) {
            plugin.interact(event.getPlayer());
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerHud(PlayerHudEvent event) {
        if (!plugin.tag.enabled) return;
        Player player = event.getPlayer();
        if (!player.hasPermission("maypole.maypole")) return;
        Session session = plugin.sessions.get(player);
        if (session == null || !session.isEnabled()) return;
        if (session.getCompletions() > 0 && session.getCollectibles() == 0) return;
        Collectible[] collectibles = Collectible.values();
        final int lineCount = 2;
        final int lineLength = collectibles.length / lineCount;
        List<List<Component>> lines2 = new ArrayList<>(lineCount);
        for (int i = 0; i < lineCount; i += 1) {
            lines2.add(new ArrayList<>());
        }
        int total = 0;
        for (Collectible collectible : collectibles) {
            boolean has = session.has(collectible);
            if (has) total += 1;
            lines2.get(collectible.ordinal() / lineLength)
                .add(has
                     ? collectible.mytems.component
                     : collectible.mytems.component.color(BLACK));
        }
        List<Component> lines = new ArrayList<>();
        lines.add(join(noSeparators(),
                       text("/maypole ", plugin.MAYPOLE_YELLOW),
                       text(Unicode.superscript(total) + "/" + Unicode.subscript(collectibles.length), plugin.MAYPOLE_BLUE)));
        for (List<Component> components : lines2) {
            lines.add(join(noSeparators(), components));
        }
        event.sidebar(PlayerHudPriority.HIGH, lines);
    }
}
