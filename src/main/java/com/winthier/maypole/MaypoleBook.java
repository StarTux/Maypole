package com.winthier.maypole;

import com.cavetale.worldmarker.item.ItemMarker;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

@RequiredArgsConstructor
public final class MaypoleBook {
    private final MaypolePlugin plugin;
    private ItemStack prototype;

    private ConfigurationSection loadConfiguration() {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "book.yml"));
        YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(plugin.getResource("book.yml")));
        config.setDefaults(defaultConfig);
        return config;
    }

    private List<Component> config2componentList(List<?> in) {
        List<Component> pages = new ArrayList<>(in.size());
        for (Object o : in) {
            if (o instanceof String) {
                String string = (String) o;
                string = ChatColor.translateAlternateColorCodes('&', string);
                pages.add(Component.text(string));
            } else {
                throw new IllegalStateException(o.getClass().getName());
            }
        }
        return pages;
    }

    public void enable() {
        ConfigurationSection config = loadConfiguration();
        List<Component> introduction = config2componentList(config.getList("introduction"));
        Map<Collectible, List<Component>> collectiblePages = new EnumMap<>(Collectible.class);
        for (Collectible collectible : Collectible.values()) {
            collectiblePages.put(collectible, config2componentList(config.getList(collectible.key)));
        }
        List<Component> tocs = new ArrayList<>();
        List<Component> content = new ArrayList<>();
        int tocOffset = 3;
        Component toc = Component.empty();
        int targetPage = tocOffset + content.size();
        content.addAll(introduction);
        toc = toc.append(Component.text("Table of Contents\n\n"));
        toc = toc.append(Component.empty()
                         .append(Component.text(targetPage + " ").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC))
                         .append(Component.text("Introduction\n").color(NamedTextColor.DARK_BLUE))
                         .clickEvent(ClickEvent.changePage(targetPage))
                         .hoverEvent(HoverEvent.showText(Component.text("Jump to page " + targetPage))));
        int linum = 3;
        for (Collectible collectible: Collectible.values()) {
            targetPage = tocOffset + content.size();
            List<Component> thePages = collectiblePages.get(collectible);
            thePages.set(0, Component.empty()
                         .append(Component.empty()
                                 .append(collectible.mytems.component)
                                 .append(Component.text(" " + collectible.nice + "\n").color(NamedTextColor.DARK_BLUE).decorate(TextDecoration.BOLD))
                                 .clickEvent(ClickEvent.changePage(1))
                                 .hoverEvent(HoverEvent.showText(Component.text("Return to table of contents"))))
                         .append(thePages.get(0)));
            content.addAll(thePages);
            toc = toc.append(Component.empty()
                             .append(Component.text(targetPage + " ").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC))
                             .append(collectible.mytems.component)
                             .append(Component.text(" " + collectible.nice + "\n").color(NamedTextColor.DARK_BLUE))
                             .clickEvent(ClickEvent.changePage(targetPage))
                             .hoverEvent(HoverEvent.showText(Component.text("Jump to page " + targetPage))));
            linum += 1;
            if (linum >= 14) {
                tocs.add(toc);
                toc = Component.empty();
                linum = 0;
            }
        }
        tocs.add(toc);
        if (tocs.size() + 1 != tocOffset) {
            throw new IllegalStateException("tocOffset = " + tocOffset + " != " + (tocs.size() + 1));
        }
        List<Component> pages = new ArrayList<>(tocs.size() + content.size());
        pages.addAll(tocs);
        pages.addAll(content);
        prototype = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) prototype.getItemMeta();
        meta.setGeneration(BookMeta.Generation.ORIGINAL);
        meta.author(Component.text("Council of May"));
        meta.title(Component.text("Building a Maypole"));
        meta.pages(pages);
        ItemMarker.setId(meta, MaypolePlugin.BOOK_ID);
        prototype.setItemMeta(meta);
    }

    public ItemStack makeBook() {
        return prototype.clone();
    }
}
