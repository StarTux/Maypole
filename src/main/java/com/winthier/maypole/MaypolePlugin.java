package com.winthier.maypole;

import com.winthier.exploits.bukkit.BukkitExploits;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.SkullType;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.json.simple.JSONValue;

public final class MaypolePlugin extends JavaPlugin implements Listener {
    private YamlConfiguration playerProgress = null;
    private List<String> eventWorlds;
    private String poleWorld;
    private List<Integer> poleCoords;
    private List<BlockFace> skullFacings;
    private List<String> originalWinCommands;

    enum Collectible {
        LUCID_LILY,
        PINE_CONE,
        ORANGE_ONION,
        MISTY_MOREL,
        RED_ROSE,
        FROST_FLOWER,
        HEAT_ROOT,
        CACTUS_BLOSSOM,
        PIPE_WEED,
        KINGS_PUMPKIN,
        SPARK_SEED,
        OASIS_WATER,
        CLAMSHELL,
        FROZEN_AMBER,
        CLUMP_OF_MOSS,
        FIRE_AMANITA;

        final String key, nice;
        Collectible() {
            this.key = name().toLowerCase();
            String[] toks = name().split("_");
            StringBuilder sb = new StringBuilder();
            for (String tok: toks) {
                if (sb.length() != 0) sb.append(" ");
                sb.append(tok.substring(0 ,1));
                sb.append(tok.substring(1).toLowerCase());
            }
            this.nice = sb.toString();
        }
    }

    @Override
    public void onEnable() {
        reloadConfig();
        saveDefaultConfig();
        saveResource("book.yml", false);
        playerProgress = null;
        getServer().getPluginManager().registerEvents(this, this);
        parseConfig();
        for (Player player: getServer().getOnlinePlayers()) {
            storePlayerMeta(player);
        }
    }

    void parseConfig() {
        eventWorlds = getConfig().getStringList("EventWorlds");
        poleWorld = getConfig().getString("PoleWorld");
        poleCoords = getConfig().getIntegerList("PoleCoords");
        skullFacings = getConfig().getStringList("SkullFacings").stream().map(a -> BlockFace.valueOf(a.toUpperCase())).collect(Collectors.toList());
        originalWinCommands = getConfig().getStringList("OriginalWinCommands");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 0) return false;
        switch (args[0]) {
        case "reload":
            playerProgress = null;
            reloadConfig();
            parseConfig();
            sender.sendMessage("Maypole configurations reloaded");
            break;
        case "interact":
            if (args.length == 2) {
                Player target = getServer().getPlayer(args[1]);
                if (target != null) {
                    if (!playerReturns(target)) {
                        ConfigurationSection prog = getPlayerProgress(target);
                        if (!prog.getBoolean("HasBook")) {
                            giveBook(target);
                            prog.set("HasBook", true);
                            savePlayerProgress();
                            target.sendMessage("Here, take this book.");
                            target.playSound(target.getEyeLocation(), Sound.ENTITY_FIREWORK_BLAST, 1.0f, 0.5f);
                        } else {
                            StringBuilder sb = new StringBuilder("You still lack ");
                            boolean comma = false;
                            for (Collectible collectible: Collectible.values()) {
                                if (!prog.getBoolean(collectible.key, false)) {
                                    if (comma) sb.append(", ");
                                    sb.append(collectible.nice);
                                    comma = true;
                                }
                            }
                            sb.append(".");
                            target.sendMessage(sb.toString());
                            target.playSound(target.getEyeLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 0.6f);
                            int completions = prog.getInt("Completions", 0);
                            if (completions == 1) {
                                target.sendMessage("You have completed your collection once before.");
                            } else if (completions > 1) {
                                target.sendMessage("You have completed your collection " + ChatColor.GREEN + completions + ChatColor.WHITE + " times.");
                            }
                            if (completions > 0) {
                                Map<String, Integer> hi = new HashMap<>();
                                List<String> ls = new ArrayList<>();
                                for (String key: getPlayerProgress().getKeys(false)) {
                                    hi.put(key, getPlayerProgress().getConfigurationSection(key).getInt("Completions", 0));
                                    ls.add(key);
                                }
                                Collections.sort(ls, (a, b) -> Integer.compare(hi.get(b), hi.get(a)));
                                for (int i = 0; i < 3 && i < ls.size(); i += 1) {
                                    String key = ls.get(i);
                                    target.sendMessage("#" + (i + 1) + " " + ChatColor.GREEN + hi.get(key) + " " + ChatColor.WHITE + getPlayerProgress().getConfigurationSection(key).getString("Name", "N/A"));
                                }
                            }
                        }
                    }
                }
            }
            break;
        case "test":
            if (args.length == 2) {
                Player target = getServer().getPlayer(args[1]);
                if (target != null) {
                    for (Collectible collectible: Collectible.values()) {
                        getPlayerProgress(target).set(collectible.key, true);
                    }
                    getPlayerProgress(target).set("Completions", 0);
                    savePlayerProgress();
                    storePlayerMeta(target);
                    sender.sendMessage(target.getName() + " set to 0 completions and all collectibles");
                }
            }
            break;
        case "all":
            if (args.length == 2) {
                Player target = getServer().getPlayer(args[1]);
                if (target != null) {
                    for (Collectible collectible: Collectible.values()) {
                        getPlayerProgress(target).set(collectible.key, true);
                    }
                    savePlayerProgress();
                    storePlayerMeta(target);
                    sender.sendMessage(target.getName() + " was given all collectibles");
                }
            }
            break;
        case "none":
            if (args.length == 2) {
                Player target = getServer().getPlayer(args[1]);
                if (target != null) {
                    for (Collectible collectible: Collectible.values()) {
                        getPlayerProgress(target).set(collectible.key, false);
                    }
                    savePlayerProgress();
                    storePlayerMeta(target);
                    sender.sendMessage(target.getName() + " was cleared of all collectibles");
                }
            }
            break;
        case "info":
            if (args.length == 2) {
                Player target = getServer().getPlayer(args[1]);
                if (target != null) {
                    ConfigurationSection prog = getPlayerProgress(target);
                    sender.sendMessage(target.getName() + " Maypole Porgress");
                    for (String key: prog.getKeys(false)) {
                        sender.sendMessage(key + ": " + prog.get(key));
                    }
                }
            }
            break;
        case "book":
            if (args.length == 2) {
                Player target = getServer().getPlayer(args[1]);
                if (target != null) {
                    giveBook(target);
                }
                sender.sendMessage("Book given to " + target.getName());
            }
            break;
        case "highscore":
            {
                Map<String, Integer> hi = new HashMap<>();
                List<String> ls = new ArrayList<>();
                for (String key: getPlayerProgress().getKeys(false)) {
                    hi.put(key, getPlayerProgress().getConfigurationSection(key).getInt("Completions", 0));
                    ls.add(key);
                }
                Collections.sort(ls, (a, b) -> Integer.compare(hi.get(b), hi.get(a)));
                int i = 0;
                for (String key: ls) {
                    sender.sendMessage("#" + (++i) + " " + hi.get(key) + " " + getPlayerProgress().getConfigurationSection(key).getString("Name", "N/A"));
                }
            }
            break;
        default:
            return false;
        }
        return true;
    }

    private YamlConfiguration getPlayerProgress() {
        if (playerProgress == null) {
            playerProgress = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "player_progress.yml"));
        }
        return playerProgress;
    }

    private void savePlayerProgress() {
        if (playerProgress == null) return;
        try {
            playerProgress.save(new File(getDataFolder(), "player_progress.yml"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private ConfigurationSection getPlayerProgress(Player player) {
        String key = player.getUniqueId().toString();
        ConfigurationSection result = getPlayerProgress().getConfigurationSection(key);
        if (result == null) {
            result = getPlayerProgress().createSection(key);
            result.set("Name", player.getName());
            result.set("Completions", 0);
        }
        return result;
    }

    void unlockCollectible(Player player, Collectible collectible) {
        ConfigurationSection progress = getPlayerProgress(player);
        if (progress.getBoolean(collectible.key, false)) return;
        int maxCompletions = 0;
        for (String key: getPlayerProgress().getKeys(false)) {
            maxCompletions = Math.max(maxCompletions, getPlayerProgress().getConfigurationSection(key).getInt("Completions"));
        }
        int completions = progress.getInt("Completions", 0);
        double chance = 1.0 / (double)(completions + 2);
        if (completions > 3 && completions >= maxCompletions - 1) chance *= 0.3;
        Random random = new Random(System.currentTimeMillis());
        if (random.nextDouble() > chance) return;
        progress.set(collectible.key, true);
        savePlayerProgress();
        storePlayerMeta(player);
        player.playSound(player.getEyeLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.5f);
        getServer().dispatchCommand(getServer().getConsoleSender(), "minecraft:title " + player.getName() + " actionbar {\"color\":\"green\",\"text\":\"You collect the " + collectible.nice + ".\"}");
        player.sendMessage("You collect the " + collectible.nice + ".");
        player.spawnParticle(Particle.FIREWORKS_SPARK, player.getEyeLocation(), 100, 2.0, 2.0, 2.0, 0.0);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        if (!eventWorlds.contains(block.getWorld().getName())) return;
        if (BukkitExploits.getInstance().isPlayerPlaced(block)) return;
        switch (block.getType()) {
        case RED_ROSE: // Flowers
            switch (block.getData()) {
            case 5: // Orange tulip
                unlockCollectible(player, Collectible.ORANGE_ONION);
                break;
            default: break;
            }
            break;
        case WATER_LILY:
            switch (block.getBiome()) {
            case SWAMPLAND:
            case MUTATED_SWAMPLAND:
                unlockCollectible(player, Collectible.LUCID_LILY);
            default: break;
            }
            break;
        case BROWN_MUSHROOM:
            switch (block.getBiome()) {
            case SWAMPLAND:
            case MUTATED_SWAMPLAND:
                unlockCollectible(player, Collectible.MISTY_MOREL);
            default: break;
            }
            break;
        case RED_MUSHROOM:
            switch (block.getBiome()) {
            case HELL:
                unlockCollectible(player, Collectible.FIRE_AMANITA);
            default: break;
            }
            break;
        case DOUBLE_PLANT:
            switch (block.getData()) {
            case 4: // Rose Bush
                unlockCollectible(player, Collectible.RED_ROSE);
                break;
            default: break;
            }
            break;
        case LONG_GRASS:
            switch (block.getData()) {
            case 1: // Tall grass
                switch (block.getBiome()) {
                case COLD_BEACH:
                case FROZEN_OCEAN:
                case FROZEN_RIVER:
                case ICE_FLATS:
                case ICE_MOUNTAINS:
                case MUTATED_ICE_FLATS:
                case MUTATED_REDWOOD_TAIGA:
                case MUTATED_REDWOOD_TAIGA_HILLS:
                case MUTATED_TAIGA:
                case MUTATED_TAIGA_COLD:
                case REDWOOD_TAIGA:
                case REDWOOD_TAIGA_HILLS:
                case TAIGA:
                case TAIGA_COLD:
                case TAIGA_COLD_HILLS:
                case TAIGA_HILLS:
                    unlockCollectible(player, Collectible.FROST_FLOWER);
                default: break;
                }
                break;
            case 2: // Fern
                switch (block.getBiome()) {
                case JUNGLE:
                case JUNGLE_EDGE:
                case JUNGLE_HILLS:
                case MUTATED_JUNGLE:
                case MUTATED_JUNGLE_EDGE:
                    unlockCollectible(player, Collectible.PIPE_WEED);
                default: break;
                }
            default: break;
            }
            break;
        case DEAD_BUSH:
            switch (block.getBiome()) {
            case DESERT:
            case DESERT_HILLS:
            case MUTATED_DESERT:
                unlockCollectible(player, Collectible.HEAT_ROOT);
            default: break;
            }
            break;
        case CACTUS:
            unlockCollectible(player, Collectible.CACTUS_BLOSSOM);
            break;
        case PUMPKIN:
            unlockCollectible(player, Collectible.KINGS_PUMPKIN);
            break;
        case SAND:
            switch (block.getBiome()) {
            case BEACHES:
            case COLD_BEACH:
            case STONE_BEACH:
            case DEEP_OCEAN:
            case FROZEN_OCEAN:
            case OCEAN:
                unlockCollectible(player, Collectible.CLAMSHELL);
            default: break;
            }
            break;
        case PACKED_ICE:
            unlockCollectible(player, Collectible.FROZEN_AMBER);
            break;
        case LEAVES:
            if ((block.getData() & 3) == 1) {
                unlockCollectible(player, Collectible.PINE_CONE);
            }
            break;
        case MOSSY_COBBLESTONE:
            unlockCollectible(player, Collectible.CLUMP_OF_MOSS);
            break;
        default: break;
        }
    }

    @EventHandler
    public void onPlayerBucketFill(PlayerBucketFillEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlockClicked();
        if (!eventWorlds.contains(block.getWorld().getName())) return;
        if (BukkitExploits.getInstance().isPlayerPlaced(block)) return;
        switch (block.getType()) {
        case LAVA:
        case STATIONARY_LAVA:
            if (block.getY() > 48
                && block.getRelative(0, 1, 0).getLightFromSky() == 15) {
                unlockCollectible(player, Collectible.SPARK_SEED);
            }
            break;
        case WATER:
        case STATIONARY_WATER:
            switch (block.getBiome()) {
            case DESERT:
            case DESERT_HILLS:
            case MUTATED_DESERT:
                unlockCollectible(player, Collectible.OASIS_WATER);
                break;
            default: break;
            }
            break;
        default: break;
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        storePlayerMeta(event.getPlayer());
    }

    // Return true if the player returns his trophies to the pole
    private boolean playerReturns(Player player) {
        // Is in correct world?
        if (!player.getWorld().getName().equals(poleWorld)) return false;
        ConfigurationSection prog = getPlayerProgress(player);
        // Has all collectibles?
        for (Collectible collectible: Collectible.values()) {
            if (!prog.getBoolean(collectible.key, false)) {
                return false;
            }
        }
        // Reset collectibles and give completion point
        for (Collectible collectible: Collectible.values()) {
            prog.set(collectible.key, null);
        }
        int completions = prog.getInt("Completions", 0);
        prog.set("Completions", completions + 1);
        savePlayerProgress();
        // Dish out prizes for first completion
        if (completions == 0) {
            World world = getServer().getWorld(poleWorld);
            Block poleBlock = world.getBlockAt(poleCoords.get(0), poleCoords.get(1), poleCoords.get(2));
            boolean placed = false;
            while (!placed) {
                poleBlock = poleBlock.getRelative(0, 1, 0);
                if (poleBlock.getType() == Material.AIR) {
                    poleBlock.setType(Material.CONCRETE);
                    if ((poleBlock.getY() & 1) == 0) {
                        poleBlock.setData((byte)0);
                    } else {
                        poleBlock.setData((byte)3); // 3=blue, 14=red
                    }
                }
                for (BlockFace face: skullFacings) {
                    Block skullBlock = poleBlock.getRelative(face);
                    if (skullBlock.getType() != Material.AIR) continue;
                    skullBlock.setType(Material.SKULL);
                    switch (face) {
                    case NORTH: skullBlock.setData((byte)2); break;
                    case EAST: skullBlock.setData((byte)5); break;
                    case SOUTH: skullBlock.setData((byte)3); break;
                    case WEST: skullBlock.setData((byte)4); break;
                    default: break;
                    }
                    Skull skullState = (Skull)(skullBlock.getState());
                    //skullState.setRotation(face);
                    skullState.setSkullType(SkullType.PLAYER);
                    skullState.setOwningPlayer(player);
                    skullState.update();
                    new BukkitRunnable() {
                        @Override public void run() {
                            skullState.update();
                        }
                    }.runTaskLater(this, 20L);
                    final Location blockLocation = skullBlock.getLocation().add(0.5, 0.5, 0.5);
                    final Location playerLocation = player.getEyeLocation();
                    new BukkitRunnable() {
                        int i = 0;
                        @Override public void run() {
                            i += 1;
                            if (i >= 100) cancel();
                            Random random = new Random(System.currentTimeMillis());
                            double p = random.nextDouble();
                            Vector v = playerLocation.toVector().multiply(p).add(blockLocation.toVector().multiply(1.0 - p));
                            Location loc = v.toLocation(playerLocation.getWorld(), 0f, 0f);
                            playerLocation.getWorld().spawnParticle(Particle.SPELL_MOB, loc, 1, 0.5, 0.5, 0.5, -1f);
                        }
                    }.runTaskTimer(this, 1, 1);
                    placed = true;
                    break;
                }
            }
            for (String cmd: originalWinCommands) {
                cmd = cmd.replace("%player%", player.getName());
                getServer().dispatchCommand(getServer().getConsoleSender(), cmd);
            }
        }
        player.playSound(player.getEyeLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 1.0f, 1.0f);
        player.sendMessage("You return a complete collection to the Maypole.");
        return true;
    }

    void giveBook(Player player) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "book.yml"));
        List<Object> pages = new ArrayList<>();
        List<Object> page = new ArrayList<>();
        Map<String, Integer> anchors = new HashMap<>();
        for (String par: config.getStringList("pages")) {
            switch (par) {
            case "page":
                if (page.size() == 1) {
                    pages.add(JSONValue.toJSONString(page.get(0)));
                } else {
                    pages.add(JSONValue.toJSONString(page));
                }
                page = new ArrayList<>();
                break;
            default:
                if (par.startsWith("anchor")) {
                    anchors.put(par.split(" ", 2)[1], pages.size());
                } else {
                    page.add(ChatColor.translateAlternateColorCodes('&', par));
                }
            }
        }
        page = new ArrayList<>();
        page.add("Table of Contents\n\n");
        Map<String, Object> tocEntry = new HashMap<>();
        tocEntry.put("text", "3 Introduction");
        tocEntry.put("color", "blue");
        tocEntry.put("underlined", true);
        Map<String, Object> clickEvent = new HashMap<>();
        tocEntry.put("clickEvent", clickEvent);
        clickEvent.put("action", "change_page");
        clickEvent.put("value", 3);
        Map<String, Object> hoverEvent = new HashMap<>();
        tocEntry.put("hoverEvent", hoverEvent);
        hoverEvent.put("action", "show_text");
        hoverEvent.put("value", "Jump to page 3");
        page.add(tocEntry);
        int entries = 1;
        for (Collectible collectible: Collectible.values()) {
            Integer pageNo = anchors.get(collectible.key);
            if (pageNo == null) {
                System.err.println("No anchor for " + collectible);
                continue;
            }
            pageNo += 3;
            tocEntry = new HashMap<>();
            tocEntry.put("text", pageNo + " " + collectible.nice);
            tocEntry.put("color", "blue");
            tocEntry.put("underlined", true);
            clickEvent = new HashMap<>();
            tocEntry.put("clickEvent", clickEvent);
            clickEvent.put("action", "change_page");
            clickEvent.put("value", pageNo);
            hoverEvent = new HashMap<>();
            tocEntry.put("hoverEvent", hoverEvent);
            hoverEvent.put("action", "show_text");
            hoverEvent.put("value", "Jump to page " + pageNo);
            page.add("\n");
            page.add(tocEntry);
            entries += 1;
            if (entries == 12) {
                pages.add(0, JSONValue.toJSONString(page));
                page = new ArrayList<>();
            }
        }
        pages.add(1, JSONValue.toJSONString(page));
        Map<String, Object> book = new HashMap<>();
        book.put("generation", 3);
        book.put("author", "Council of May");
        book.put("title", "Building a Maypole");
        book.put("pages", pages);
        String cmd = "give " + player.getName() + " minecraft:written_book 1 0 " + JSONValue.toJSONString(book);
        getServer().dispatchCommand(getServer().getConsoleSender(), cmd);
    }

    void storePlayerMeta(Player player) {
        Collectible[] collectibles = Collectible.values();
        ConfigurationSection prog = getPlayerProgress(player);
        for (int i = 0; i < collectibles.length; i += 1) {
            player.setMetadata("Collectible" + i, new FixedMetadataValue(this, prog.getBoolean(collectibles[i].key, false)));
        }
    }
}
