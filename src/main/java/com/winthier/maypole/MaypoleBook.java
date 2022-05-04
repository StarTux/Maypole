package com.winthier.maypole;

import com.cavetale.worldmarker.item.ItemMarker;
import com.winthier.maypole.session.Session;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
    private List<Component> introduction;
    private Map<Collectible, List<Component>> collectiblePages;
    private Map<Collectible, Integer> pageNumbers = new EnumMap<>(Collectible.class);
    private List<Component> content;
    private final int tocOffset = 3;

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
        introduction = config2componentList(config.getList("introduction"));
        collectiblePages = new EnumMap<>(Collectible.class);
        content = new ArrayList<>();
        for (int i = 0; i < introduction.size(); i += 1) {
            introduction.set(i, Component.text()
                             .append(Component.text()
                                     .append(Component.text("Introduction\n", NamedTextColor.DARK_BLUE, TextDecoration.BOLD))
                                     .clickEvent(ClickEvent.changePage(1))
                                     .hoverEvent(HoverEvent.showText(Component.text("Return to table of contents")))
                                     .build())
                             .append(introduction.get(i))
                             .build());
        }
        content.addAll(introduction);
        for (Collectible collectible : Collectible.values()) {
            collectiblePages.put(collectible, config2componentList(config.getList(collectible.key)));
            int targetPage = tocOffset + content.size();
            pageNumbers.put(collectible, targetPage);
            List<Component> thePages = collectiblePages.get(collectible);
            for (int i = 0; i < thePages.size(); i += 1) {
                thePages.set(i, Component.text()
                             .append(Component.text()
                                     .append(collectible.mytems.component)
                                     .append(Component.text(" " + collectible.nice + "\n", NamedTextColor.DARK_BLUE, TextDecoration.BOLD))
                                     .clickEvent(ClickEvent.changePage(1))
                                     .hoverEvent(HoverEvent.showText(Component.text("Return to table of contents")))
                                     .build())
                             .append(thePages.get(i))
                             .build());
            }
            content.addAll(thePages);
        }
    }

    private static String fmt(int i) {
        return i < 10 ? "0" + i : "" + i;
    }

    public ItemStack makeBook(Session session) {
        List<Component> tocs = new ArrayList<>();
        TextComponent.Builder toc = Component.text();
        toc.append(Component.text("Table of Contents", NamedTextColor.DARK_BLUE, TextDecoration.BOLD));
        toc.append(Component.newline());
        toc.append(Component.newline());
        Collectible[] collectibles = Collectible.values();
        List<Component> listLine1 = new ArrayList<>(8);
        List<Component> listLine2 = new ArrayList<>(8);
        int count = 0;
        int targetPage = 0;
        for (int i = 0; i < collectibles.length; i += 1) {
            Collectible collectible = collectibles[i];
            boolean has = session.has(collectible);
            if (has) count += 1;
            targetPage = pageNumbers.get(collectible);
            Component icon = (has
                              ? (collectible.mytems.component
                                 .hoverEvent(HoverEvent.showText(Component.text()
                                                                 .append(Component.text(collectible.nice, NamedTextColor.BLUE))
                                                                 .append(Component.newline())
                                                                 .append(Component.text("Collected!", NamedTextColor.BLUE)))))
                              : (collectible.mytems.component.color(TextColor.color(0x202020))
                                 .hoverEvent(HoverEvent.showText(Component.text()
                                                                 .append(Component.text(collectible.nice, NamedTextColor.GRAY))
                                                                 .append(Component.newline())
                                                                 .append(Component.text("Not yet collected!", NamedTextColor.DARK_GRAY))))))
                .clickEvent(ClickEvent.changePage(targetPage));
            if (i < 8) {
                listLine1.add(icon);
            } else {
                listLine2.add(icon);
            }
        }
        toc.append(Component.space());
        toc.append(Component.join(JoinConfiguration.separator(Component.space()), listLine1));
        toc.append(Component.newline());
        toc.append(Component.space());
        toc.append(Component.join(JoinConfiguration.separator(Component.space()), listLine2));
        toc.append(Component.newline());
        toc.append(Component.text("Collected: ", NamedTextColor.DARK_GRAY))
            .append(Component.text(count + "/" + collectibles.length, NamedTextColor.DARK_BLUE));
        toc.append(Component.newline());
        toc.append(Component.text("Completions: ", NamedTextColor.DARK_GRAY))
            .append(Component.text(session.getCompletions(), NamedTextColor.DARK_BLUE));
        toc.append(Component.newline());
        toc.append(Component.newline());
        targetPage = tocOffset;
        toc.append(Component.text()
                   .append(Component.text(fmt(targetPage) + " ").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC))
                   .append(Component.text("Introduction\n", NamedTextColor.DARK_BLUE))
                   .clickEvent(ClickEvent.changePage(targetPage))
                   .hoverEvent(HoverEvent.showText(Component.text("Jump to page " + fmt(targetPage))))
                   .build());
        for (int i = 0; i < collectibles.length; i += 1) {
            Collectible collectible = collectibles[i];
            Component icon = collectible.mytems.component;
            targetPage = pageNumbers.get(collectible);
            toc.append(Component.text()
                       .append(Component.text(fmt(targetPage) + " ").color(NamedTextColor.DARK_GRAY).decorate(TextDecoration.ITALIC))
                       .append(icon)
                       .append(Component.text(" " + collectible.nice + "\n", NamedTextColor.DARK_BLUE))
                       .clickEvent(ClickEvent.changePage(targetPage))
                       .hoverEvent(HoverEvent.showText(Component.text("Jump to page " + fmt(targetPage))))
                       .build());
            if (i == 4) {
                tocs.add(toc.build());
                toc = Component.text();
                toc.append(Component.text("Table of Contents", NamedTextColor.DARK_BLUE, TextDecoration.BOLD));
                toc.append(Component.newline());
                toc.append(Component.newline());
            }
        }
        tocs.add(toc.build());
        if (tocs.size() + 1 != tocOffset) {
            throw new IllegalStateException("tocOffset = " + tocOffset + " != " + (tocs.size() + 1));
        }
        List<Component> pages = new ArrayList<>(tocs.size() + content.size());
        pages.addAll(tocs);
        pages.addAll(content);
        ItemStack item = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta meta = (BookMeta) item.getItemMeta();
        meta.setGeneration(BookMeta.Generation.ORIGINAL);
        meta.author(Component.text("Council of May"));
        meta.title(Component.text("Building a Maypole"));
        meta.pages(pages);
        ItemMarker.setId(meta, MaypolePlugin.BOOK_ID);
        item.setItemMeta(meta);
        return item;
    }
}
