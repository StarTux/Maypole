package com.winthier.maypole;

import com.cavetale.worldmarker.item.ItemMarker;
import com.winthier.exploits.Exploits;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
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
            switch (block.getBiome()) {
            case SWAMP:
            case SWAMP_HILLS:
                plugin.unlockCollectible(player, block, Collectible.LUCID_LILY);
            default: break;
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
            switch (block.getBiome()) {
            case COLD_OCEAN:
            case DEEP_COLD_OCEAN:
            case FROZEN_OCEAN:
            case FROZEN_RIVER:
            case DEEP_FROZEN_OCEAN:
            case ICE_SPIKES:
            case TAIGA:
            case TAIGA_HILLS:
            case SNOWY_TAIGA:
            case SNOWY_TAIGA_HILLS:
            case GIANT_TREE_TAIGA:
            case GIANT_TREE_TAIGA_HILLS:
            case TAIGA_MOUNTAINS:
            case SNOWY_TAIGA_MOUNTAINS:
            case GIANT_SPRUCE_TAIGA:
            case GIANT_SPRUCE_TAIGA_HILLS:
            case SNOWY_TUNDRA:
            case SNOWY_MOUNTAINS:
            case SNOWY_BEACH:
                plugin.unlockCollectible(player, block, Collectible.FROST_FLOWER);
            default: break;
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
            switch (block.getBiome()) {
            case DESERT:
            case DESERT_HILLS:
            case DESERT_LAKES:
                plugin.unlockCollectible(player, block, Collectible.HEAT_ROOT);
            default: break;
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
            plugin.unlockCollectible(player, block, Collectible.FROZEN_AMBER);
            break;
        case SWEET_BERRY_BUSH:
            plugin.unlockCollectible(player, block, Collectible.PINE_CONE);
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
        default: break;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerShearEntity(PlayerShearEntityEvent event) {
        if (!plugin.enabled) return;
        Player player = event.getPlayer();
        if (!plugin.eventWorlds.contains(player.getWorld().getName())) return;
        if (event.getEntity() instanceof MushroomCow) {
            MushroomCow cow = (MushroomCow) event.getEntity();
            if (cow.getVariant() != MushroomCow.Variant.BROWN) return;
            plugin.unlockCollectible(player, cow.getEyeLocation().getBlock(), Collectible.MISTY_MOREL);
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
                && block.getRelative(0, 1, 0).getLightFromSky() == 15) {
                plugin.unlockCollectible(player, block, Collectible.SPARK_SEED);
            }
            break;
        case WATER:
            switch (block.getBiome()) {
            case DESERT:
            case DESERT_HILLS:
            case DESERT_LAKES:
                plugin.unlockCollectible(player, block, Collectible.OASIS_WATER);
                break;
            default: break;
            }
            break;
        default: break;
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!plugin.enabled) return;
        if (event.hasBlock()) {
            Block poleBlock = plugin.getPoleBlock();
            if (poleBlock.equals(event.getClickedBlock())) {
                plugin.interact(event.getPlayer());
                event.setCancelled(true);
                return;
            }
        }
        ItemStack item = event.getItem();
        if (item != null && ItemMarker.hasId(item, MaypolePlugin.BOOK_ID)) {
            event.setCancelled(true);
            Player player = event.getPlayer();
            player.openBook(plugin.maypoleBook.makeBook(player));
            return;
        }
    }
}
