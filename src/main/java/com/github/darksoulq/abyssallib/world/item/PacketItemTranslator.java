package com.github.darksoulq.abyssallib.world.item;

import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;

public class PacketItemTranslator {

    private static Field FIELD_CONTENT_ID;
    private static Field FIELD_CONTENT_STATE;
    private static Field FIELD_CONTENT_ITEMS;
    private static Field FIELD_CONTENT_CARRIED;

    private static Field FIELD_SLOT_ID;
    private static Field FIELD_SLOT_STATE;
    private static Field FIELD_SLOT_INDEX;
    private static Field FIELD_SLOT_ITEM;

    static {
        try {
            FIELD_CONTENT_ID = ClientboundContainerSetContentPacket.class.getDeclaredField("containerId");
            FIELD_CONTENT_STATE = ClientboundContainerSetContentPacket.class.getDeclaredField("stateId");
            FIELD_CONTENT_ITEMS = ClientboundContainerSetContentPacket.class.getDeclaredField("items");
            FIELD_CONTENT_CARRIED = ClientboundContainerSetContentPacket.class.getDeclaredField("carriedItem");

            setAccessible(FIELD_CONTENT_ID, FIELD_CONTENT_STATE, FIELD_CONTENT_ITEMS, FIELD_CONTENT_CARRIED);

            FIELD_SLOT_ID = ClientboundContainerSetSlotPacket.class.getDeclaredField("containerId");
            FIELD_SLOT_STATE = ClientboundContainerSetSlotPacket.class.getDeclaredField("stateId");
            FIELD_SLOT_INDEX = ClientboundContainerSetSlotPacket.class.getDeclaredField("slot");
            FIELD_SLOT_ITEM = ClientboundContainerSetSlotPacket.class.getDeclaredField("itemStack");

            setAccessible(FIELD_SLOT_ID, FIELD_SLOT_STATE, FIELD_SLOT_INDEX, FIELD_SLOT_ITEM);
        } catch (Exception ignored) {
        }
    }

    private static void setAccessible(Field... fields) {
        for (Field f : fields) {
            f.setAccessible(true);
        }
    }

    public static ClientboundContainerSetContentPacket translateWindowItems(
            ClientboundContainerSetContentPacket packet,
            Player player
    ) {
        try {
            List<ItemStack> items = (List<ItemStack>) FIELD_CONTENT_ITEMS.get(packet);
            ItemStack carried = (ItemStack) FIELD_CONTENT_CARRIED.get(packet);

            for (int i = 0; i < items.size(); i++) {
                ItemStack old = items.get(i);
                ItemStack transformed = transform(old, player);
                if (transformed != old) {
                    items.set(i, transformed);
                }
            }

            try {
                FIELD_CONTENT_CARRIED.set(packet, transform(carried, player));
            } catch (IllegalAccessException ignored) {
                if (carried != null) {
                    transform(carried, player);
                }
            }

        } catch (Exception ignored) {
        }

        return packet;
    }

    public static ClientboundContainerSetSlotPacket translateSetSlot(
            ClientboundContainerSetSlotPacket packet,
            Player player
    ) {
        try {
            ItemStack item = (ItemStack) FIELD_SLOT_ITEM.get(packet);
            ItemStack transformed = transform(item, player);

            try {
                FIELD_SLOT_ITEM.set(packet, transformed);
            } catch (IllegalAccessException ignored) {
            }

        } catch (Exception ignored) {
        }

        return packet;
    }

    private static ItemStack transform(ItemStack nmsStack, Player player) {
        if (nmsStack == null || nmsStack.isEmpty()) {
            return nmsStack;
        }

        org.bukkit.inventory.ItemStack bukkitBefore = CraftItemStack.asBukkitCopy(nmsStack);
        Item custom = Item.resolve(bukkitBefore);

        if (custom == null) {
            return nmsStack;
        }

        org.bukkit.inventory.ItemStack localized = custom.getStack(player);

        try {
            org.bukkit.inventory.ItemStack mirror = CraftItemStack.asCraftMirror(nmsStack);
            boolean mutated = false;

            try {
                if (localized.hasItemMeta()) {
                    mirror.setItemMeta(localized.getItemMeta());
                    mutated = true;
                }
            } catch (Throwable ignored) {
            }

            try {
                Component customName = localized.getData(DataComponentTypes.CUSTOM_NAME);
                if (customName != null) {
                    mirror.setData(DataComponentTypes.CUSTOM_NAME, customName);
                    mirror.resetData(DataComponentTypes.ITEM_NAME);
                    mutated = true;
                }
            } catch (Throwable ignored) {
            }

            if (mutated) {
                return nmsStack;
            }

        } catch (Throwable ignored) {
        }

        return CraftItemStack.asNMSCopy(localized);
    }
}
