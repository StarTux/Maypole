package com.winthier.maypole;

import com.winthier.maypole.session.Session;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import static com.cavetale.core.font.Unicode.subscript;
import static com.cavetale.core.font.Unicode.superscript;
import static com.cavetale.core.font.Unicode.tiny;
import static net.kyori.adventure.text.Component.join;
import static net.kyori.adventure.text.Component.newline;
import static net.kyori.adventure.text.Component.space;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.JoinConfiguration.noSeparators;
import static net.kyori.adventure.text.JoinConfiguration.separator;
import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

@RequiredArgsConstructor @SuppressWarnings("LineLength")
public final class MaypoleBook {
    private final MaypolePlugin plugin;
    private static final int TOC_OFFSET = 3;
    private static final Component[] INTRODUCTION = new Component[] {
        text("Spring has sprung, my friends, and this month, it is finally time to celebrate the obligatory May Festival."),
        text("Aside from music, dancing, and free beer, by far the most important tradition thereof is the construction of the Maypole."),
        join(noSeparators(),
             text("For this task however, we require the cooperation of every able-bodied man, woman, and child, in the "),
             text("Mining Worlds", DARK_BLUE, UNDERLINED),
             text(" of Cavetale.")),
        text("The following is a list of the required ingredients for its construction. Some are mere decoration, others yield hidden powers known only to our top alchemists. Gather one of each and return them to the Maypole Steward."),
        join(noSeparators(),
             text("Keep in mind that all materials must be gathered in the "),
             text("Mining Worlds", DARK_BLUE, UNDERLINED),
             text(" and have to be natural, meaning you cannot just place them yourself.")),
        join(noSeparators(),
             text("Thank you in advance and good luck,"),
             newline(), newline(),
             text("The Council of May", DARK_GRAY, ITALIC)),
    };

    public void enable() { }

    private static String fmt(int i) {
        return i < 10 ? "0" + i : "" + i;
    }

    public ItemStack makeBook(Session session) {
        List<Component> content = new ArrayList<>();
        for (Component page : INTRODUCTION) {
            content.add(join(noSeparators(),
                             (text("Introduction", DARK_BLUE, BOLD)
                              .clickEvent(ClickEvent.changePage(1))
                              .hoverEvent(HoverEvent.showText(text("Return to table of contents")))),
                             newline(),
                             page));
        }
        Map<Collectible, Integer> pageNumbers = new EnumMap<>(Collectible.class);
        for (Collectible collectible : Collectible.values()) {
            pageNumbers.put(collectible, TOC_OFFSET + content.size());
            for (String page : collectible.getBookPages()) {
                content.add(join(noSeparators(),
                                 (join(noSeparators(),
                                       collectible.mytems.component,
                                       text(collectible.nice, DARK_BLUE, BOLD))
                                  .clickEvent(ClickEvent.changePage(1))
                                  .hoverEvent(HoverEvent.showText(text("Return to table of contents")))),
                                 newline(),
                                 text(page)));
            }
        }
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
                                 .hoverEvent(HoverEvent.showText(join(separator(newline()),
                                                                      text(collectible.nice, plugin.MAYPOLE_YELLOW),
                                                                      text("Page " + targetPage, DARK_GRAY, ITALIC),
                                                                      text("Collected!", plugin.MAYPOLE_BLUE)))))
                              : (collectible.mytems.component.color(BLACK)
                                 .hoverEvent(HoverEvent.showText(join(separator(newline()),
                                                                      text(collectible.nice, GRAY),
                                                                      text("Page " + targetPage, DARK_GRAY, ITALIC),
                                                                      text("Not yet collected!", DARK_GRAY))))))
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
        toc.append(join(separator(space()),
                        text(tiny("collected"), DARK_GRAY),
                        text(superscript(count) + "/" + subscript(collectibles.length), DARK_BLUE)));
        toc.append(newline());
        toc.append(join(separator(space()),
                        text(tiny("completed"), DARK_GRAY),
                        text(session.getCompletions(), DARK_BLUE)));
        toc.append(newline());
        toc.append(newline());
        targetPage = TOC_OFFSET;
        toc.append(join(noSeparators(),
                        text(subscript(fmt(targetPage)) + " ", DARK_GRAY),
                        text(tiny("introduction"), DARK_BLUE))
                   .clickEvent(ClickEvent.changePage(targetPage))
                   .hoverEvent(HoverEvent.showText(text("Jump to page " + targetPage, GRAY, ITALIC))));
        toc.append(newline());
        for (int i = 0; i < collectibles.length; i += 1) {
            Collectible collectible = collectibles[i];
            Component icon = collectible.mytems.component;
            targetPage = pageNumbers.get(collectible);
            toc.append(join(noSeparators(),
                            text(subscript(fmt(targetPage)) + " ", DARK_GRAY),
                            icon,
                            text(tiny(collectible.nice.toLowerCase()), DARK_BLUE))
                       .clickEvent(ClickEvent.changePage(targetPage))
                       .hoverEvent(HoverEvent.showText(join(noSeparators(),
                                                            icon, text(collectible.nice, BLUE), newline(),
                                                            text("Jump to page " + targetPage, GRAY, ITALIC)))));
            toc.append(newline());
            if (i == 4) {
                tocs.add(toc.build());
                toc = text();
                toc.append(text("Table of Contents", DARK_BLUE, BOLD));
                toc.append(newline());
                toc.append(newline());
            }
        }
        tocs.add(toc.build());
        if (tocs.size() + 1 != TOC_OFFSET) {
            throw new IllegalStateException("TOC_OFFSET = " + TOC_OFFSET + " != " + (tocs.size() + 1));
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
