package com.winthier.maypole;

import com.cavetale.mytems.Mytem;
import com.cavetale.mytems.Mytems;
import com.cavetale.mytems.util.Items;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import static com.cavetale.core.font.Unicode.tiny;
import static com.cavetale.mytems.util.Text.wrapLore;
import static com.winthier.maypole.MaypolePlugin.MAYPOLE_BLUE;
import static com.winthier.maypole.MaypolePlugin.MAYPOLE_YELLOW;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

@RequiredArgsConstructor @Getter
public final class BookOfMay implements Mytem {
    private final MaypolePlugin plugin;
    private ItemStack prototype;
    private Component displayName;
    private String description = "The Council of May requires your help!"
        + " Gather all the items described in this book.";

    @Override
    public Mytems getKey() {
        return Mytems.BOOK_OF_MAY;
    }

    @Override
    public void enable() {
        this.displayName = join(separator(space()),
                                text("Book", MAYPOLE_YELLOW),
                                text("of", MAYPOLE_BLUE),
                                text("May", MAYPOLE_YELLOW));
        List<Component> lore = new ArrayList<>();
        lore.add(displayName);
        lore.add(text(2022, DARK_GRAY, ITALIC));
        lore.addAll(wrapLore(tiny(description.toLowerCase()), c -> c.color(MAYPOLE_BLUE)));
        prototype = new ItemStack(getKey().material);
        prototype.editMeta(meta -> {
                Items.text(meta, lore);
                getKey().markItemMeta(meta);
            });
    }

    @Override
    public ItemStack createItemStack() {
        return prototype.clone();
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event, Player player, ItemStack item) {
        event.setCancelled(true);
    }

    @Override
    public void onPlayerRightClick(PlayerInteractEvent event, Player player, ItemStack item) {
        event.setUseItemInHand(Event.Result.DENY);
        plugin.openBook(event.getPlayer());
    }
}
