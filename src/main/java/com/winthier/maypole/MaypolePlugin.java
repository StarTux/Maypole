package com.winthier.maypole;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Directional;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public final class MaypolePlugin extends JavaPlugin {
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    public static final String BOOK_ID = "maypole:book";
    protected YamlConfiguration playerProgress = null;
    protected List<String> eventWorlds;
    protected String poleWorld;
    protected List<Integer> poleCoords;
    protected List<BlockFace> skullFacings;
    protected List<String> originalWinCommands;
    protected List<?> anyWinCommands;
    protected final Random random = new Random();
    protected boolean debug;
    protected boolean enabled;
    protected MaypoleBook maypoleBook = new MaypoleBook(this);
    protected HighscoreCommand highscoreCommand = new HighscoreCommand(this);

    @Override
    public void onEnable() {
        saveDefaultConfig();
        parseConfig();
        saveResource("book.yml", debug);
        playerProgress = null;
        maypoleBook.enable();
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        getCommand("maypole").setExecutor(new MaypoleCommand(this));
        getCommand("hi").setExecutor(highscoreCommand);
    }

    void parseConfig() {
        reloadConfig();
        eventWorlds = getConfig().getStringList("EventWorlds");
        poleWorld = getConfig().getString("PoleWorld");
        poleCoords = getConfig().getIntegerList("PoleCoords");
        skullFacings = getConfig().getStringList("SkullFacings").stream()
            .map(a -> BlockFace.valueOf(a.toUpperCase())).collect(Collectors.toList());
        originalWinCommands = getConfig().getStringList("OriginalWinCommands");
        anyWinCommands = getConfig().getList("AnyWinCommands");
        debug = getConfig().getBoolean("Debug");
        enabled = getConfig().getBoolean("Enabled");
    }

    protected void reloadAll() {
        playerProgress = null;
        reloadConfig();
        parseConfig();
        maypoleBook.enable();
    }

    protected void interact(Player player) {
        if (enabled && playerReturns(player)) return;
        ConfigurationSection prog = getPlayerProgress(player);
        if (enabled && !prog.getBoolean("HasBook")) {
            if (giveBook(player)) {
                prog.set("HasBook", true);
                savePlayerProgress();
                player.sendMessage(ChatColor.GREEN + "Here, take this book.");
                player.playSound(player.getEyeLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST,
                                 1.0f, 0.5f);
            } else {
                player.sendMessage(ChatColor.RED + "Your inventory is full");
            }
        } else {
            if (enabled) {
                player.openBook(maypoleBook.makeBook(player));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 0.6f);
            }
            int completions = prog.getInt("Completions", 0);
            if (completions == 1) {
                player.sendMessage("You have completed your collection once before.");
            } else if (completions > 1) {
                player.sendMessage("You have completed your collection " + ChatColor.GREEN
                                   + completions + ChatColor.WHITE + " times.");
            }
            if (completions > 0) {
                highscoreCommand.highscore(player);
            }
        }
    }

    protected YamlConfiguration getPlayerProgress() {
        if (playerProgress == null) {
            playerProgress = YamlConfiguration
                .loadConfiguration(new File(getDataFolder(), "player_progress.yml"));
        }
        return playerProgress;
    }

    protected void savePlayerProgress() {
        if (playerProgress == null) return;
        try {
            playerProgress.save(new File(getDataFolder(), "player_progress.yml"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    protected ConfigurationSection getPlayerProgress(Player player) {
        String key = player.getUniqueId().toString();
        ConfigurationSection result = getPlayerProgress().getConfigurationSection(key);
        if (result == null) {
            result = getPlayerProgress().createSection(key);
            result.set("Name", player.getName());
            result.set("Completions", 0);
        }
        return result;
    }

    public boolean hasCollectible(Player player, Collectible collectible) {
        ConfigurationSection progress = getPlayerProgress(player);
        return progress.getBoolean(collectible.key, false);
    }

    public int getCompletions(Player player) {
        ConfigurationSection progress = getPlayerProgress(player);
        return progress.getInt("Completions", 0);
    }

    void unlockCollectible(Player player, Block block, Collectible collectible) {
        ConfigurationSection progress = getPlayerProgress(player);
        if (progress.getBoolean(collectible.key, false)) return;
        int maxCompletions = 0;
        for (String key: getPlayerProgress().getKeys(false)) {
            maxCompletions = Math.max(maxCompletions, getPlayerProgress()
                                      .getConfigurationSection(key).getInt("Completions"));
        }
        int completions = progress.getInt("Completions", 0);
        double chance = 1.0 / (double) (completions + 3);
        if (completions > 3 && completions >= maxCompletions - 1) chance *= 0.3;
        double roll = random.nextDouble();
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        if (debug) {
            getLogger().info("Unlock: " + player.getName()
                             + " " + block.getWorld().getName()
                             + ":" + block.getX() + "," + block.getY() + "," + block.getZ()
                             + " (" + block.getType().name().toLowerCase() + ")"
                             + " " + collectible.key
                             + ": " + roll + "/" + chance);
        }
        if (roll > chance) {
            player.spawnParticle(Particle.CRIT, loc, 32, 0.5, 0.5, 0.5, 0.0);
            return;
        }
        progress.set(collectible.key, true);
        savePlayerProgress();
        sendUnlockMessage(player, collectible);
        block.getWorld().playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 0.20f, 1.5f);
        block.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 50, 1.0, 1.0, 1.0, 0.0);
    }

    void sendUnlockMessage(Player player, Collectible collectible) {
        Component message = Component.empty().color(NamedTextColor.GOLD)
            .append(Component.text("You collect the "))
            .append(collectible.mytems.component)
            .append(Component.text(" " + collectible.nice));
        player.sendActionBar(message);
        player.sendMessage(message);
        player.showTitle(Title.title(collectible.mytems.component, Component.text(collectible.nice)));
    }

    // Return true if the player returns his trophies to the pole
    protected boolean playerReturns(Player player) {
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
            buildMaypole(player);
            for (String cmd: originalWinCommands) {
                serverCommand(cmd, player);
            }
        } else {
            Collectible[] collectibles = Collectible.values();
            Collectible collectible = collectibles[random.nextInt(collectibles.length)];
            collectible.mytems.giveItemStack(player, 1);
            if (!this.anyWinCommands.isEmpty()) {
                Object o = this.anyWinCommands.get(random.nextInt(this.anyWinCommands.size()));
                if (o instanceof String) {
                    serverCommand((String) o, player);
                } else if (o instanceof List) {
                    for (Object p : (List) o) {
                        serverCommand((String) p, player);
                    }
                }
            }
        }
        player.playSound(player.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 0.25f, 1.25f);
        player.sendMessage("You return a complete collection to the Maypole.");
        return true;
    }

    protected void serverCommand(String cmd, Player player) {
        cmd = cmd.replace("{player}", player.getName());
        getLogger().info("Running command: " + cmd);
        getServer().dispatchCommand(getServer().getConsoleSender(), cmd);
    }

    protected Block getPoleBlock() {
        World world = getServer().getWorld(poleWorld);
        return world.getBlockAt(poleCoords.get(0), poleCoords.get(1), poleCoords.get(2));
    }

    protected void buildMaypole(Player player) {
        Block poleBlock = getPoleBlock();
        boolean placed = false;
        while (!placed) {
            poleBlock = poleBlock.getRelative(0, 1, 0);
            if (poleBlock.getType() == Material.AIR) {
                if ((poleBlock.getY() & 1) == 0) {
                    poleBlock.setType(Material.STRIPPED_SPRUCE_LOG);
                } else {
                    poleBlock.setType(Material.SPRUCE_LOG);
                }
            }
            for (BlockFace face: skullFacings) {
                Block skullBlock = poleBlock.getRelative(face);
                if (skullBlock.getType() != Material.AIR) continue;
                Directional directional = (Directional) Material.PLAYER_WALL_HEAD.createBlockData();
                directional.setFacing(face);
                skullBlock.setBlockData(directional);
                Skull skullState = (Skull) skullBlock.getState();
                skullState.setOwner(player.getName());
                skullState.setOwningPlayer(player);
                skullState.setPlayerProfile(player.getPlayerProfile());
                skullState.update();
                final Location blockLocation = skullBlock.getLocation().add(0.5, 0.5, 0.5);
                final Location playerLocation = player.getEyeLocation();
                new BukkitRunnable() {
                    int i = 0;
                    @Override public void run() {
                        i += 1;
                        if (i >= 100) cancel();
                        double p = random.nextDouble();
                        Vector v = playerLocation.toVector().multiply(p)
                            .add(blockLocation.toVector().multiply(1.0 - p));
                        Location loc = v.toLocation(playerLocation.getWorld(), 0f, 0f);
                        playerLocation.getWorld().spawnParticle(Particle.SPELL_MOB, loc, 1,
                                                                0.5, 0.5, 0.5, -1f);
                    }
                }.runTaskTimer(this, 1, 1);
                placed = true;
                break;
            }
        }
    }

    protected boolean giveBook(Player player) {
        ItemStack book = maypoleBook.makeBook(player);
        return player.getInventory().addItem(book).isEmpty();
    }
}
