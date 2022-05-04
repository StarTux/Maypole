package com.winthier.maypole;

import com.cavetale.core.util.Json;
import com.winthier.maypole.session.Session;
import com.winthier.maypole.session.Sessions;
import com.winthier.maypole.sql.Database;
import com.winthier.maypole.sql.Highscore;
import java.io.File;
import java.util.List;
import java.util.Random;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextColor.color;

public final class MaypolePlugin extends JavaPlugin {
    protected static final List<BlockFace> SKULL_FACING = List.of(BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST);
    protected static final String BOOK_ID = "maypole:book";
    protected final Random random = new Random();
    protected final MaypoleCommand command = new MaypoleCommand(this);
    protected final MaypoleAdminCommand adminCommand = new MaypoleAdminCommand(this);
    protected final MaypoleBook maypoleBook = new MaypoleBook(this);
    protected final Sessions sessions = new Sessions(this);
    protected Tag tag;
    protected List<Highscore> highscore = List.of();
    public static final TextColor MAYPOLE_YELLOW = color(0xF0E68C);
    public static final TextColor MAYPOLE_BLUE = color(0x87cefa);
    protected Component maypoleTitle = join(noSeparators(),
                                            text("M", MAYPOLE_YELLOW),
                                            text("a", MAYPOLE_BLUE),
                                            text("y", MAYPOLE_YELLOW),
                                            text("p", MAYPOLE_BLUE),
                                            text("o", MAYPOLE_YELLOW),
                                            text("l", MAYPOLE_BLUE),
                                            text("e", MAYPOLE_YELLOW));

    @Override
    public void onEnable() {
        loadTag();
        Database.enable(this);
        saveResource("book.yml", tag.debug);
        getServer().getPluginManager().registerEvents(new EventListener(this), this);
        command.enable();
        adminCommand.enable();
        maypoleBook.enable();
        sessions.enable();
        loadHighscore();
    }

    @Override
    public void onDisable() {
        Database.disable(this);
    }

    protected void interact(Player player) {
        if (tag.enabled && playerReturns(player)) return;
        Session session = sessions.get(player);
        if (session == null || !session.isEnabled()) return;
        if (tag.enabled && !session.hasBook()) {
            if (giveBook(player)) {
                session.setHasBook(true);
                player.sendMessage(text("Here, take this book", GREEN));
                player.playSound(player.getEyeLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST,
                                 1.0f, 0.5f);
            } else {
                player.sendMessage(text("Your inventory is full", RED));
            }
        } else {
            if (tag.enabled) {
                player.openBook(maypoleBook.makeBook(session));
                player.playSound(player.getEyeLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1.0f, 0.6f);
            }
            int completions = session.getCompletions();
            if (completions == 1) {
                player.sendMessage(text("You have completed your collection once before", GREEN));
            } else if (completions > 1) {
                player.sendMessage(join(noSeparators(),
                                        text("You have completed your collection ", GREEN),
                                        text(completions, WHITE),
                                        text(" times")));
            }
            if (completions > 0) {
                command.highscore(player);
            }
        }
    }

    protected void unlockCollectible(Player player, Block block, Collectible collectible) {
        Session session = sessions.get(player);
        if (session == null || !session.isEnabled()) return;
        if (session.has(collectible)) return;
        int completions = session.getCompletions();
        double chance = 1.0 / (double) (completions + 3);
        double roll = random.nextDouble();
        Location loc = block.getLocation().add(0.5, 0.5, 0.5);
        if (tag.debug) {
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
        session.give(collectible);
        loadHighscore();
        sendUnlockMessage(player, collectible);
        block.getWorld().playSound(loc, Sound.ENTITY_PLAYER_LEVELUP, 0.20f, 1.5f);
        block.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, loc, 50, 1.0, 1.0, 1.0, 0.0);
    }

    protected void sendUnlockMessage(Player player, Collectible collectible) {
        Component message = join(noSeparators(),
                                 text("You collect the ", GREEN),
                                 collectible.mytems.component,
                                 text(" " + collectible.nice, GREEN));
        player.sendActionBar(message);
        player.sendMessage(message);
        player.showTitle(Title.title(collectible.mytems.component, Component.text(collectible.nice)));
    }

    // Return true if the player returns his trophies to the pole
    protected boolean playerReturns(Player player) {
        // Is in correct world?
        if (!player.getWorld().getName().equals(tag.pole.world)) return false;
        Session session = sessions.get(player);
        if (session == null || session.isEnabled()) return false;
        // Has all collectibles?
        for (Collectible collectible: Collectible.values()) {
            if (!session.has(collectible)) {
                return false;
            }
        }
        // Reset collectibles and give completion point
        session.clearCollection();
        int completions = session.getCompletions();
        session.setCompletions(completions + 1);
        // Dish out prizes for first completion
        if (completions == 0) {
            buildMaypole(player);
            serverCommand("kite member Maypole " + player.getName());
        } else {
            Collectible[] collectibles = Collectible.values();
            Collectible collectible = collectibles[random.nextInt(collectibles.length)];
            collectible.mytems.giveItemStack(player, 1);
            serverCommand("mytems give " + player.getName() + " kitty_coin");
        }
        player.playSound(player.getEyeLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 0.25f, 1.25f);
        player.sendMessage("You return a complete collection to the Maypole.");
        return true;
    }

    protected void serverCommand(String cmd) {
        getLogger().info("Running command: " + cmd);
        getServer().dispatchCommand(getServer().getConsoleSender(), cmd);
    }

    protected void buildMaypole(Player player) {
        Block poleBlock = tag.pole.toBlock();
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
            for (BlockFace face: SKULL_FACING) {
                Block skullBlock = poleBlock.getRelative(face);
                if (skullBlock.getType() != Material.AIR) continue;
                Directional directional = (Directional) Material.PLAYER_WALL_HEAD.createBlockData();
                directional.setFacing(face);
                skullBlock.setBlockData(directional);
                Skull skullState = (Skull) skullBlock.getState();
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
        Session session = sessions.get(player);
        if (session == null || !session.isEnabled()) return false;
        ItemStack book = maypoleBook.makeBook(session);
        return player.getInventory().addItem(book).isEmpty();
    }

    public boolean openBook(Player player) {
        Session session = sessions.get(player);
        if (session == null || !session.isEnabled()) return false;
        ItemStack book = maypoleBook.makeBook(session);
        player.openBook(book);
        return true;
    }

    protected void loadTag() {
        this.tag = Json.load(new File(getDataFolder(), "save.json"), Tag.class, Tag::new);
    }

    protected void saveTag() {
        Json.save(new File(getDataFolder(), "save.json"), tag);
    }

    protected void loadHighscore() {
        Highscore.list(ls -> this.highscore = ls);
    }
}
