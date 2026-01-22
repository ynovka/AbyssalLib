package com.github.darksoulq.abyssallib.world.item;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class LocalizedName {
    private final Component defaultName;
    private final Map<String, Component> translations;

    private LocalizedName(Component defaultName, Map<String, Component> translations) {
        this.defaultName = defaultName;
        this.translations = translations;
    }

    public Component resolve(@Nullable Player player) {
        if (player == null) return defaultName;

        String localeKey = player.locale().toString().toLowerCase();

        // Приоритет: Локаль игрока -> en_us -> Default
        if (translations.containsKey(localeKey)) {
            return translations.get(localeKey);
        }
        return translations.getOrDefault("en_us", defaultName);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Component defaultName = Component.empty();
        private final Map<String, Component> translations = new HashMap<>();

        public Builder setDefault(Component component) {
            this.defaultName = component;
            return this;
        }

        public Builder addTranslation(String locale, Component component) {
            this.translations.put(locale.toLowerCase(), component);
            return this;
        }

        public LocalizedName build() {
            return new LocalizedName(defaultName, translations);
        }
    }
}