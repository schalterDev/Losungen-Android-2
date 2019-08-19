package de.schalter.losungen.components.dialogs.widgetStyleChooser

data class WidgetStyle(
        val backgroundColor: Int,
        val fontColor: Int,
        val fontSize: Float = 20F) {
    companion object {
        val presets = listOf(
                WidgetStyle(-939524097, -16777216),
                WidgetStyle(-14606047, -1),
                WidgetStyle(-16742521, -1),
                WidgetStyle(-29813, -16777216),
                WidgetStyle(-5636236, -16777216),
                WidgetStyle(-973072027, -1),
                WidgetStyle(-369098915, -16777216),
                WidgetStyle(-4129024, -16777216),
                WidgetStyle(-7613989, -16777216),
                WidgetStyle(-16751932, -1),
                WidgetStyle(-4129024, -16777216),
                WidgetStyle(-4194304, -1),
                WidgetStyle(-256, -16777216),
                WidgetStyle(-16753385, -1),
                WidgetStyle(-12660507, -16777216),
                WidgetStyle(-1214720, -1),
                WidgetStyle(-6160168, -1),
                WidgetStyle(-12660507, -16777216),
                WidgetStyle(-2844204, -16777216)
        )
    }
}