package com.winthier.maypole;

import com.winthier.maypole.session.Session;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

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
                pages.add(text((String) o));
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
            introduction.set(i, text()
                             .append(text()
                                     .append(text("Introduction\n", DARK_BLUE, BOLD))
                                     .clickEvent(ClickEvent.changePage(1))
                                     .hoverEvent(HoverEvent.showText(text("Return to table of contents")))
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
                thePages.set(i, text()
                             .append(text()
                                     .append(collectible.mytems.component)
                                     .append(text(" " + collectible.nice + "\n", DARK_BLUE, BOLD))
                                     .clickEvent(ClickEvent.changePage(1))
                                     .hoverEvent(HoverEvent.showText(text("Return to table of contents")))
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
        TextComponent.Builder toc = text();
        toc.append(text("Table of Contents", DARK_BLUE, BOLD));
        toc.append(newline());
        toc.append(newline());
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
                                 .hoverEvent(HoverEvent.showText(text()
                                                                 .append(text(collectible.nice, BLUE))
                                                                 .append(newline())
                                                                 .append(text("Collected!", BLUE)))))
                              : (collectible.mytems.component.color(TextColor.color(0x202020))
                                 .hoverEvent(HoverEvent.showText(text()
                                                                 .append(text(collectible.nice, GRAY))
                                                                 .append(newline())
                                                                 .append(text("Not yet collected!", DARK_GRAY))))))
                .clickEvent(ClickEvent.changePage(targetPage));
            if (i < 8) {
                listLine1.add(icon);
            } else {
                listLine2.add(icon);
            }
        }
        toc.append(space());
        toc.append(join(separator(space()), listLine1));
        toc.append(newline());
        toc.append(space());
        toc.append(join(separator(space()), listLine2));
        toc.append(newline());
        toc.append(text("Collected: ", DARK_GRAY))
            .append(text(count + "/" + collectibles.length, DARK_BLUE));
        toc.append(newline());
        toc.append(text("Completions: ", DARK_GRAY))
            .append(text(session.getCompletions(), DARK_BLUE));
        toc.append(newline());
        toc.append(newline());
        targetPage = tocOffset;
        toc.append(text()
                   .append(text(fmt(targetPage) + " ").color(DARK_GRAY).decorate(ITALIC))
                   .append(text("Introduction\n", DARK_BLUE))
                   .clickEvent(ClickEvent.changePage(targetPage))
                   .hoverEvent(HoverEvent.showText(text("Jump to page " + fmt(targetPage))))
                   .build());
        for (int i = 0; i < collectibles.length; i += 1) {
            Collectible collectible = collectibles[i];
            Component icon = collectible.mytems.component;
            targetPage = pageNumbers.get(collectible);
            toc.append(text()
                       .append(text(fmt(targetPage) + " ").color(DARK_GRAY).decorate(ITALIC))
                       .append(icon)
                       .append(text(" " + collectible.nice + "\n", DARK_BLUE))
                       .clickEvent(ClickEvent.changePage(targetPage))
                       .hoverEvent(HoverEvent.showText(text("Jump to page " + fmt(targetPage))))
                       .build());
            if (i == 4) {
                tocs.add(toc.build());
                toc = text();
                toc.append(text("Table of Contents", DARK_BLUE, BOLD));
                toc.append(newline());
                toc.append(newline());
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
        item.editMeta(m -> {
                if (m instanceof BookMeta meta) {
                    meta.setGeneration(BookMeta.Generation.ORIGINAL);
                    meta.author(text("Council of May"));
                    meta.title(text("Building a Maypole"));
                    meta.pages(pages);
                }
            });
        return item;
    }
}
