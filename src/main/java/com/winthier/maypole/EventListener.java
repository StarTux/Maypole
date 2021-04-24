package com.winthier.maypole;

import com.cavetale.worldmarker.item.ItemMarker;
import com.winthier.exploits.Exploits;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.World;
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
import org.bukkit.inventory.ItemStack;

@RequiredArgsConstructor
public final class EventListener implements Listener {
    private final MaypolePlugin plugin;

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (!plugin.enabled) return;
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        if (!plugin.eventWorlds.contains(block.getWorld().getName())) return;
        if (Exploits.isPlayerPlaced(block)) return;
        switch (block.getType()) {
        case SEAGRASS:
            if (block.getBiome().name().contains("SWAMP")) {
                plugin.unlockCollectible(player, block, Collectible.LUCID_LILY);
            }
            break;
        case ORANGE_TULIP:
            plugin.unlockCollectible(player, block, Collectible.ORANGE_ONION);
            break;
        case ROSE_BUSH:
            plugin.unlockCollectible(player, block, Collectible.RED_ROSE);
            break;
        case GRASS:
        case TALL_GRASS:
            if (block.getTemperature() < 0.2) {
                plugin.unlockCollectible(player, block, Collectible.FROST_FLOWER);
            }
            break;
        case FERN:
            switch (block.getBiome()) {
            case BAMBOO_JUNGLE:
            case BAMBOO_JUNGLE_HILLS:
            case JUNGLE:
            case JUNGLE_HILLS:
            case JUNGLE_EDGE:
            case MODIFIED_JUNGLE:
            case MODIFIED_JUNGLE_EDGE:
                plugin.unlockCollectible(player, block, Collectible.PIPE_WEED);
            default: break;
            }
            break;
        case DEAD_BUSH:
            if (block.getBiome().name().contains("DESERT")) {
                plugin.unlockCollectible(player, block, Collectible.HEAT_ROOT);
            }
            break;
        case CACTUS:
            if (block.getRelative(0, -1, 0).getType() == Material.SAND) {
                plugin.unlockCollectible(player, block, Collectible.CACTUS_BLOSSOM);
            }
            break;
        case PUMPKIN:
            plugin.unlockCollectible(player, block, Collectible.KINGS_PUMPKIN);
            break;
        case BRAIN_CORAL:
        case BRAIN_CORAL_BLOCK:
        case BUBBLE_CORAL:
        case BUBBLE_CORAL_BLOCK:
        case FIRE_CORAL:
        case FIRE_CORAL_BLOCK:
        case HORN_CORAL:
        case HORN_CORAL_BLOCK:
        case TUBE_CORAL:
        case TUBE_CORAL_BLOCK:
            plugin.unlockCollectible(player, block, Collectible.CLAMSHELL);
            break;
        case BLUE_ICE:
        case PACKED_ICE:
            plugin.unlockCollectible(player, block, Collectible.FROZEN_AMBER);
            break;
        case SPRUCE_LEAVES:
            if (block.getBiome().name().contains("TAIGA")) {
                plugin.unlockCollectible(player, block, Collectible.PINE_CONE);
            }
            break;
        case MOSSY_COBBLESTONE:
        case MOSSY_COBBLESTONE_SLAB:
        case MOSSY_COBBLESTONE_STAIRS:
        case MOSSY_COBBLESTONE_WALL:
        case MOSSY_STONE_BRICKS:
        case MOSSY_STONE_BRICK_SLAB:
        case MOSSY_STONE_BRICK_STAIRS:
        case MOSSY_STONE_BRICK_WALL:
            plugin.unlockCollectible(player, block, Collectible.CLUMP_OF_MOSS);
            break;
        case BROWN_MUSHROOM:
            if (block.getBiome().name().contains("SWAMP")) {
                plugin.unlockCollectible(player, block, Collectible.MISTY_MOREL);
            }
            break;
        default: break;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!plugin.enabled) return;
        LivingEntity entity = event.getEntity();
        if (!plugin.eventWorlds.contains(entity.getWorld().getName())) return;
        if (entity.getType() == EntityType.PIGLIN) {
            if (entity.getWorld().getEnvironment() != World.Environment.NETHER) return;
            Player killer = entity.getKiller();
            if (killer == null) return;
            plugin.unlockCollectible(killer, entity.getEyeLocation().getBlock(), Collectible.FIRE_AMANITA);
        }
    }

    @EventHandler(priority  = EventPriority.HIGHEST)
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        if (!plugin.enabled) return;
        final Player player = event.getPlayer();
        final Block block = event.getBlockClicked();
        if (!plugin.eventWorlds.contains(block.getWorld().getName())) return;
        if (Exploits.isPlayerPlaced(block)) return;
        switch (block.getType()) {
        case LAVA:
            if (block.getY() > 48
                && block.getRelative(0, 1, 0).getLightFromSky() >= 8) {
                plugin.unlockCollectible(player, block, Collectible.SPARK_SEED);
            }
            break;
        case WATER:
            if (block.getBiome().name().contains("DESERT")) {
                plugin.unlockCollectible(player, block, Collectible.OASIS_WATER);
            }
            break;
        default: break;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        switch (event.getAction()) {
        case PHYSICAL: return;
        default: break;
        }
        if (!plugin.enabled) return;
        if (event.hasBlock()) {
            Block poleBlock = plugin.getPoleBlock();
            if (poleBlock.equals(event.getClickedBlock())) {
                plugin.interact(event.getPlayer());
                event.setCancelled(true);
                return;
            }
        }
        switch (event.getAction()) {
        case RIGHT_CLICK_BLOCK:
        case RIGHT_CLICK_AIR:
            ItemStack item = event.getItem();
            if (item != null && ItemMarker.hasId(item, MaypolePlugin.BOOK_ID)) {
                event.setCancelled(true);
                Player player = event.getPlayer();
                player.openBook(plugin.maypoleBook.makeBook(player));
                return;
            }
        default: break;
        }
    }
}
