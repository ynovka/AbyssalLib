package com.github.darksoulq.abyssallib.world.item

import com.github.darksoulq.abyssallib.server.registry.DeferredRegistry
import com.github.darksoulq.abyssallib.server.registry.`object`.Holder

fun customItem(
    registry: DeferredRegistry<Item>,
    id: String,
    block: CustomItemDsl.() -> Unit
): Holder<Item> = CustomItemDsl(registry, id).apply(block).register()