package com.github.darksoulq.abyssallib.world.item.component.builtin;

import com.github.darksoulq.abyssallib.common.serialization.Codec;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.Vanilla;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemName extends DataComponent<ItemName.LocalizedNameContainer> implements Vanilla {

    // Note: You will need to update your Codec to support serialization of the Map/Container
    // if you plan to save this component to disk/network in this specific format.
    // For now, I have left it as a placeholder or you can implement a composite Codec.
    public static final Codec<ItemName> CODEC = null;

    public ItemName(LocalizedNameContainer value) {
        super(Identifier.of(DataComponentTypes.ITEM_NAME.key().asString()), value, CODEC);
    }

    /**
     * Creates a simple ItemName with just a default component (legacy support).
     */
    public ItemName(Component defaultName) {
        this(new LocalizedNameContainer(defaultName, new HashMap<>()));
    }

    /**
     * Resolves the component for a specific player based on their locale.
     * Logic: Player Locale -> en_us -> Default
     */
    public Component resolve(@Nullable Player player) {
        if (value == null) return Component.empty();
        if (player == null) return value.defaultName;

        String localeKey = player.locale().toString().toLowerCase();

        // 1. Try exact match
        if (value.translations.containsKey(localeKey)) {
            return value.translations.get(localeKey);
        }

        // 2. Try 'en_us' as standard fallback
        if (value.translations.containsKey("en_us")) {
            return value.translations.get("en_us");
        }

        // 3. Return default
        return value.defaultName;
    }

    @Override
    public void apply(ItemStack stack) {
        stack.setData(DataComponentTypes.ITEM_NAME, value.defaultName);
    }

    @Override
    public void remove(ItemStack stack) {
        stack.unsetData(DataComponentTypes.ITEM_NAME);
    }

    public static Builder builder() {
        return new Builder();
    }

    // --- Data Container ---
    public static class LocalizedNameContainer {
        private final Component defaultName;
        private final Map<String, Component> translations;

        public LocalizedNameContainer(Component defaultName, Map<String, Component> translations) {
            this.defaultName = defaultName != null ? defaultName : Component.empty();
            this.translations = translations != null ? translations : new HashMap<>();
        }
    }

    // --- Builder ---
    public static class Builder {
        private Component defaultName = Component.empty();
        private final Map<String, Component> translations = new HashMap<>();

        public Builder setDefault(@NotNull Component component) {
            this.defaultName = component;
            return this;
        }

        public Builder setDefault(@NotNull String miniMessage) {
            this.defaultName = MiniMessage.miniMessage().deserialize(miniMessage);
            return this;
        }

        public Builder addLocal(@NotNull String locale, @NotNull Component component) {
            this.translations.put(locale.toLowerCase(), component);
            return this;
        }

        public Builder addLocal(@NotNull String locale, @NotNull String miniMessage) {
            this.translations.put(locale.toLowerCase(), MiniMessage.miniMessage().deserialize(miniMessage));
            return this;
        }

        public ItemName build() {
            return new ItemName(new LocalizedNameContainer(defaultName, translations));
        }
    }
}