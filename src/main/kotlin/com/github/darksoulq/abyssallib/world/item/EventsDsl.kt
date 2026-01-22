package com.github.darksoulq.abyssallib.world.item

import com.github.darksoulq.abyssallib.server.event.ActionResult
import com.github.darksoulq.abyssallib.server.event.ClickType
import com.github.darksoulq.abyssallib.server.event.InventoryClickType
import com.github.darksoulq.abyssallib.server.event.context.item.AnvilContext
import com.github.darksoulq.abyssallib.server.event.context.item.UseContext
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.PlayerInventory

class EventsDsl {
    internal var onUse: ((LivingEntity, EquipmentSlot, ClickType) -> ActionResult)? = null
    internal var onUseOn: ((UseContext) -> ActionResult)? = null
    internal var onClickInInventory: ((Player, Int, PlayerInventory, InventoryClickType) -> ActionResult)? = null
    internal var onSwapHand: ((Player, EquipmentSlot) -> ActionResult)? = null
    internal var onDrop: ((Player) -> ActionResult)? = null
    internal var postMine: ((LivingEntity, Block) -> ActionResult)? = null
    internal var postHit: ((LivingEntity, Entity) -> ActionResult)? = null
    internal var onPickup: ((Player) -> ActionResult)? = null
    internal var onAnvilPrepare: ((AnvilContext) -> ActionResult)? = null

    fun onUse(block: (LivingEntity, EquipmentSlot, ClickType) -> ActionResult) { onUse = block }
    fun onUseOn(block: (UseContext) -> ActionResult) { onUseOn = block }
    fun onClickInInventory(block: (Player, Int, PlayerInventory, InventoryClickType) -> ActionResult) { onClickInInventory = block }

    // Nullability adjusted to match Paper API usually being NonNull for Player events,
    // but check your specific AbyssLib implementation if arguments can be null.
    fun onSwapHand(block: (Player, EquipmentSlot) -> ActionResult) { onSwapHand = block }
    fun onDrop(block: (Player) -> ActionResult) { onDrop = block }
    fun postMine(block: (LivingEntity, Block) -> ActionResult) { postMine = block }
    fun postHit(block: (LivingEntity, Entity) -> ActionResult) { postHit = block }
    fun onPickup(block: (Player) -> ActionResult) { onPickup = block }
    fun onAnvilPrepare(block: (AnvilContext) -> ActionResult) { onAnvilPrepare = block }
}