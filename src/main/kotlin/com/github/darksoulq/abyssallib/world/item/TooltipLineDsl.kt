package com.github.darksoulq.abyssallib.world.item

class TooltipLineDsl {
    private val translations = mutableMapOf<String, String>()

    fun default(text: String) { translations["en_us"] = text }
    fun locale(locale: String, text: String) { translations[locale.lowercase()] = text }

    fun getLocales(): Set<String> = translations.keys
    fun getText(locale: String): String = translations[locale.lowercase()] ?: translations["en_us"] ?: ""
}