package com.github.darksoulq.abyssallib.world.item;

import com.github.darksoulq.abyssallib.AbyssalLib;
import com.github.darksoulq.abyssallib.common.util.CTag;
import com.github.darksoulq.abyssallib.common.util.Identifier;
import com.github.darksoulq.abyssallib.server.event.ActionResult;
import com.github.darksoulq.abyssallib.server.event.ClickType;
import com.github.darksoulq.abyssallib.server.event.InventoryClickType;
import com.github.darksoulq.abyssallib.server.event.context.item.AnvilContext;
import com.github.darksoulq.abyssallib.server.event.context.item.UseContext;
import com.github.darksoulq.abyssallib.server.registry.Registries;
import com.github.darksoulq.abyssallib.world.block.CustomBlock;
import com.github.darksoulq.abyssallib.world.data.tag.impl.ItemTag;
import com.github.darksoulq.abyssallib.world.item.component.ComponentMap;
import com.github.darksoulq.abyssallib.world.item.component.DataComponent;
import com.github.darksoulq.abyssallib.world.item.component.builtin.*;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemLore;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class Item implements Cloneable {
    private Identifier id;
    private ItemStack stack;
    private ComponentMap componentMap;
    private LocalizedName nameDefinition;
    private LocalizedTooltip tooltipDefinition;

    @ApiStatus.Internal
    public Item(ItemStack stack) {
        this.id = Identifier.of("unknown", "unknown");
        this.stack = stack;
        componentMap = new ComponentMap(this);
    }
    public Item(Identifier id, Material base) {
        this.id = id;
        stack = ItemStack.of(base);
        Integer size = stack.getData(DataComponentTypes.MAX_STACK_SIZE);
        Key model = stack.getData(DataComponentTypes.ITEM_MODEL);
        componentMap = new ComponentMap(this);
        for (Identifier cId : componentMap.getVanillaIds()) {
            componentMap.removeData(cId);
        }
        if (size != null) setData(new MaxStackSize(size));
        if (model != null) setData(new ItemModel(model));
        else setData(new ItemModel(id.asNamespacedKey()));
        setData(new CustomMarker(id));
        this.nameDefinition = LocalizedName.builder()
                .setDefault(Component.text(id.toString()))
                .build();
        this.tooltipDefinition = createTooltip();
        this.applyTooltipToComponents(this.tooltipDefinition, null);
        this.componentMap.applyData();
    }

    public void setNameDefinition(LocalizedName nameDefinition) {
        this.nameDefinition = nameDefinition;
    }

    public LocalizedTooltip createTooltip() {
        return LocalizedTooltip.builder().build();
    }

    private void applyTooltipToComponents(LocalizedTooltip tooltip, @Nullable Player player) {
        setData(new Lore(ItemLore.lore(tooltip.resolveLines(player))));
        setData(new DisplayTooltip(TooltipDisplay.tooltipDisplay()
                .hideTooltip(tooltip.isHidden())
                .hiddenComponents(tooltip.getHiddenComponents())
                .build()));

        if (tooltip.getStyle() != null) {
            setData(new TooltipStyle(tooltip.getStyle().asNamespacedKey()));
        } else {
            unsetData(TooltipStyle.class);
        }
    }

    public void setData(DataComponent<?> component) {
        componentMap.setData(component);
    }
    public DataComponent<?> getData(Identifier id) {
        return componentMap.getData(id);
    }
    public DataComponent<?> getData(DataComponentType type) {
        return componentMap.getData(type);
    }
    public <T extends DataComponent<?>> T getData(Class<T> clazz) {
        return componentMap.getData(clazz);
    }
    public boolean hasData(Identifier id) {
        return componentMap.hasData(id);
    }
    public boolean hasData(DataComponentType type) {
        return componentMap.hasData(type);
    }
    public void unsetData(Identifier id) {
        componentMap.removeData(id);
    }
    public void unsetData(Class<? extends DataComponent> clazz) {
        componentMap.removeData(clazz);
    }
    public <T extends DataComponent<?>> boolean hasData(Class<T> clazz) {
        return componentMap.hasData(clazz);
    }
    public boolean hasTag(Identifier id) {
        if (!(Registries.TAGS.get(id.toString()) instanceof ItemTag tag)) {
            AbyssalLib.getInstance().getLogger().severe("Unknown tag: " + id);
            return false;
        }
        return tag.contains(stack);
    }
    public boolean hasTag(ItemTag tag) {
        return tag.contains(stack);
    }
    public void setTag(ItemTag tag) {
        tag.add(ItemPredicate.builder()
                .value(new CustomMarker(id))
                .build());
    }

    public ActionResult postMine(LivingEntity source, Block target) { return ActionResult.PASS; }
    public ActionResult postHit(LivingEntity source, Entity target) { return ActionResult.PASS; }
    public ActionResult onUseOn(UseContext ctx) { return ActionResult.PASS; }
    public ActionResult onUse(LivingEntity source, EquipmentSlot hand, ClickType type) { return ActionResult.PASS; }
    public void onInventoryTick(Player player) {}
    public void onSlotChange(Player player, @Nullable Integer newSlot) {}
    public ActionResult onClickInInventory(Player player, int slot, PlayerInventory inventory, InventoryClickType type) { return ActionResult.PASS; }
    public ActionResult onDrop(Player player) { return ActionResult.PASS; }
    public ActionResult onPickup(Player player) { return ActionResult.PASS; }
    public ActionResult onSwapHand(Player player, EquipmentSlot current) { return ActionResult.PASS; }
    public ActionResult onAnvilPrepare(AnvilContext ctx) { return ActionResult.PASS; }
    public void onCraftedBy(Player player) {}

    public Identifier getId() {
        return id;
    }
    @ApiStatus.Internal
    public ItemStack getRawStack() {
        return this.stack;
    }

    public ItemStack getStack(@Nullable Player player) {
        Item contextItem = this.clone();

        contextItem.tooltipDefinition = contextItem.createTooltip();
        contextItem.applyTooltipToComponents(contextItem.tooltipDefinition, player);
        contextItem.getComponentMap().applyData();

        Component resolvedName = nameDefinition.resolve(player);
        if (resolvedName != null) {
            Component nonItalicName = resolvedName.decoration(TextDecoration.ITALIC, false);
            contextItem.stack.setData(DataComponentTypes.CUSTOM_NAME, nonItalicName);
            contextItem.stack.resetData(DataComponentTypes.ITEM_NAME);
            if (contextItem.stack.hasItemMeta()) {
                ItemMeta meta = contextItem.stack.getItemMeta();
                meta.displayName(nonItalicName);
                contextItem.stack.setItemMeta(meta);
            }
        }

        return contextItem.stack;
    }

    public ComponentMap getComponentMap() {
        return componentMap;
    }

    public CTag getCTag() {
        return CTag.getCTag(stack);
    }
    public void setCTag(CTag container) {
        CTag.setCTag(container, stack);
    }

    public static Item resolve(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) return null;
        Item base = new Item(stack);
        if (!base.hasData(CustomMarker.class)) return null;
        Identifier id = (Identifier) base.getData(CustomMarker.class).value;
        if (id == null) return null;
        Item item = Registries.ITEMS.get(id.toString());
        if (item == null) return null;
        Item clone = item.clone();
        clone.stack = stack;
        clone.componentMap = new ComponentMap(clone);
        return clone;
    }
    public static CustomBlock asBlock(Item item) {
        if (!item.hasData(Identifier.of("abyssallib:block_item"))) return null;
        Identifier blockId = (Identifier) item.getData(Identifier.of("abyssallib:block_item")).value;
        return Registries.BLOCKS.get(blockId.toString()).clone();
    }

    @Override
    public Item clone() {
        try {
            Item item = (Item) super.clone();
            item.id = this.id;
            item.stack = this.stack.clone();
            item.componentMap = new ComponentMap(item);
            item.nameDefinition = this.nameDefinition;
            item.tooltipDefinition = this.tooltipDefinition;
            return item;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Item item)) return false;
        return Objects.equals(id, item.id);
    }
    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}