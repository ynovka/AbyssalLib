package com.github.darksoulq.abyssallib.world.item

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry
import com.github.darksoulq.abyssallib.server.registry.`object`.Holder
import org.bukkit.Material

class CustomItemDsl(
    private val registry: DeferredRegistry<Item>,
    private val id: String
) {
    private var material: Material = Material.STONE
    private var localizedName: LocalizedName? = null
    private var tooltipDsl: TooltipDsl? = null
    private var eventsDsl: EventsDsl? = null
    private var dataBlock: (Item.() -> Unit)? = null
    private var cancelAll = false

    fun material(material: Material) { this.material = material }
    fun name(block: NameDsl.() -> Unit) { localizedName = NameDsl().apply(block).build() }
    fun tooltip(block: TooltipDsl.() -> Unit) { tooltipDsl = TooltipDsl().apply(block) }
    fun events(block: EventsDsl.() -> Unit) { eventsDsl = EventsDsl().apply(block) }
    fun data(block: Item.() -> Unit) { dataBlock = block }
    fun cancelAllInteractions() { cancelAll = true }

    fun register(): Holder<Item> {
        return registry.register(id) { itemId ->
            val item = GeneratedItem(
                itemId,
                material,
                localizedName,
                tooltipDsl,
                eventsDsl,
                cancelAll
            )

            dataBlock?.invoke(item)

            item
        }
    }
}