package com.github.darksoulq.abyssallib.world.item

import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.Component


class NameDsl {
    private var defaultName: Component = Component.empty()
    private val translations = mutableMapOf<String, Component>()

    fun default(miniMessage: String) {
        this.defaultName = MiniMessage.miniMessage().deserialize(miniMessage)
    }

    fun default(component: Component) {
        this.defaultName = component
    }

    fun locale(locale: String, miniMessage: String) {
        this.translations[locale.lowercase()] = MiniMessage.miniMessage().deserialize(miniMessage)
    }

    fun locale(locale: String, component: Component) {
        this.translations[locale.lowercase()] = component
    }

    fun build(): LocalizedName {
        val builder = LocalizedName.builder().setDefault(defaultName)
        translations.forEach { (lang, comp) -> builder.addTranslation(lang, comp) }
        return builder.build()
    }
}