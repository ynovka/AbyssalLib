package com.github.darksoulq.abyssallib.world.item

import com.github.darksoulq.abyssallib.common.util.Identifier
import com.github.darksoulq.abyssallib.server.event.ActionResult
import com.github.darksoulq.abyssallib.server.event.ClickType
import com.github.darksoulq.abyssallib.server.event.InventoryClickType
import com.github.darksoulq.abyssallib.server.event.context.item.AnvilContext
import com.github.darksoulq.abyssallib.server.event.context.item.UseContext
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.PlayerInventory

class GeneratedItem(
    id: Identifier,
    material: Material,
    nameFromDsl: LocalizedName?,
    private val tooltipDsl: TooltipDsl?,
    private val events: EventsDsl?,
    private val cancelAll: Boolean
) : Item(id, material) {

    init {
        if (nameFromDsl != null) {
            setNameDefinition(nameFromDsl)
        }
    }

    override fun createTooltip(): LocalizedTooltip {
        return tooltipDsl?.build() ?: super.createTooltip()
    }

    private fun defaultResult(): ActionResult =
        if (cancelAll) ActionResult.CANCEL else ActionResult.PASS

    override fun onUse(source: LivingEntity, hand: EquipmentSlot, type: ClickType): ActionResult {
        return events?.onUse?.invoke(source, hand, type) ?: defaultResult()
    }

    override fun onUseOn(ctx: UseContext): ActionResult {
        return events?.onUseOn?.invoke(ctx) ?: defaultResult()
    }

    override fun onClickInInventory(
        player: Player,
        slot: Int,
        inventory: PlayerInventory,
        type: InventoryClickType
    ): ActionResult {
        return events?.onClickInInventory?.invoke(player, slot, inventory, type) ?: defaultResult()
    }

    override fun onSwapHand(player: Player, current: EquipmentSlot): ActionResult {
        return events?.onSwapHand?.invoke(player, current) ?: defaultResult()
    }

    override fun onDrop(player: Player): ActionResult {
        return events?.onDrop?.invoke(player) ?: defaultResult()
    }

    override fun postMine(source: LivingEntity, target: Block): ActionResult {
        return events?.postMine?.invoke(source, target) ?: defaultResult()
    }

    override fun postHit(source: LivingEntity, target: Entity): ActionResult {
        return events?.postHit?.invoke(source, target) ?: defaultResult()
    }

    override fun onPickup(player: Player): ActionResult {
        return events?.onPickup?.invoke(player) ?: defaultResult()
    }

    override fun onAnvilPrepare(ctx: AnvilContext): ActionResult {
        return events?.onAnvilPrepare?.invoke(ctx) ?: defaultResult()
    }
}