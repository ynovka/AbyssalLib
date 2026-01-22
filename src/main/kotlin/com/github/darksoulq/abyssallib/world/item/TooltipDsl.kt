package com.github.darksoulq.abyssallib.world.item

class TooltipDsl {
    private val lines = mutableListOf<TooltipLineDsl>()
    private var hideTooltip = false

    fun line(block: TooltipLineDsl.() -> Unit) {
        lines.add(TooltipLineDsl().apply(block))
    }

    fun hideTooltip() {
        this.hideTooltip = true
    }

    fun build(): LocalizedTooltip {
        val builder = LocalizedTooltip.builder()
        if (hideTooltip) builder.hideTooltip(true)

        val allLocales = lines.flatMap { it.getLocales() }.toSet()

        lines.forEach { builder.addLine(it.getText("en_us")) }

        allLocales.filter { it != "en_us" }.forEach { locale ->
            lines.forEach { line ->
                builder.addLocalLine(locale, line.getText(locale))
            }
        }

        return builder.build()
    }
}