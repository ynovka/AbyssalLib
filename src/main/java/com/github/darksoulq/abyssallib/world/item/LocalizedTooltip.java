package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.common.util.Identifier;
import io.papermc.paper.datacomponent.DataComponentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Handles localized lore and tooltip settings (hiding flags, styles).
 */
public class LocalizedTooltip {

    private boolean hideTooltip = false;
    private Identifier style = null;
    private final Set<DataComponentType> hiddenComponents = new HashSet<>();

    // Localization storage: Locale -> List of Lines
    private final Map<String, List<Component>> localizedLines = new HashMap<>();
    private final List<Component> defaultLines = new ArrayList<>();

    private LocalizedTooltip() {}

    public static Builder builder() {
        return new Builder();
    }

    /**
     * Resolves the lore lines for a specific player.
     * Logic: Player Locale -> en_us -> Default
     */
    public List<Component> resolveLines(@Nullable Player player) {
        if (player == null) return new ArrayList<>(defaultLines);

        String localeKey = player.locale().toString().toLowerCase();

        if (localizedLines.containsKey(localeKey)) {
            return new ArrayList<>(localizedLines.get(localeKey));
        }

        return new ArrayList<>(defaultLines);
    }

    public boolean isHidden() { return hideTooltip; }
    public Identifier getStyle() { return style; }
    public Set<DataComponentType> getHiddenComponents() { return hiddenComponents; }

    // --- Builder ---
    public static class Builder {
        private final LocalizedTooltip tooltip;

        public Builder() {
            this.tooltip = new LocalizedTooltip();
        }

        public Builder hideTooltip(boolean hide) {
            tooltip.hideTooltip = hide;
            return this;
        }

        public Builder withStyle(Identifier style) {
            tooltip.style = style;
            return this;
        }

        public Builder hideComponent(DataComponentType type) {
            tooltip.hiddenComponents.add(type);
            return this;
        }

        /**
         * Adds a line to the DEFAULT (fallback) lore.
         */
        public Builder addLine(Component component) {
            tooltip.defaultLines.add(component);
            return this;
        }

        public Builder addLine(String miniMessage) {
            tooltip.defaultLines.add(MiniMessage.miniMessage().deserialize(miniMessage));
            return this;
        }

        /**
         * Adds a line to a SPECIFIC locale.
         * Note: This appends to the list for that locale.
         * You must build the full lore for that locale.
         */
        public Builder addLocalLine(String locale, Component component) {
            tooltip.localizedLines.computeIfAbsent(locale.toLowerCase(), k -> new ArrayList<>()).add(component);
            return this;
        }

        public Builder addLocalLine(String locale, String miniMessage) {
            tooltip.localizedLines.computeIfAbsent(locale.toLowerCase(), k -> new ArrayList<>())
                    .add(MiniMessage.miniMessage().deserialize(miniMessage));
            return this;
        }

        public LocalizedTooltip build() {
            return tooltip;
        }
    }
}