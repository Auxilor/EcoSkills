package com.willfp.ecoskills.skills.display

import com.willfp.eco.core.EcoPlugin
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Location
import org.bukkit.entity.Entity

/**
 * A hologram implementation using native Minecraft TextDisplay entities.
 * Requires Minecraft 1.19.4+
 * Uses reflection to compile against older API but work at runtime.
 */
class DisplayEntityHologram(
    private val plugin: EcoPlugin,
    private val location: Location,
    private val text: String
) {
    private var textDisplay: Entity? = null

    init {
        spawn()
    }

    private fun spawn() {
        val world = location.world ?: return

        try {
            val textDisplayClass = Class.forName("org.bukkit.entity.TextDisplay")
            val displayClass = Class.forName("org.bukkit.entity.Display")
            val billboardClass = Class.forName("org.bukkit.entity.Display\$Billboard")

            // Spawn the TextDisplay entity using reflection
            val spawnMethod = world.javaClass.getMethod("spawn", Location::class.java, Class::class.java)
            textDisplay = spawnMethod.invoke(world, location, textDisplayClass) as Entity

            val display = textDisplay ?: return

            val component = try {
                MiniMessage.miniMessage().deserialize(
                    convertLegacyToMiniMessage(text)
                )
            } catch (e: Exception) {
                LegacyComponentSerializer.legacyAmpersand().deserialize(text.replace("§", "&"))
            }

            val textMethod = textDisplayClass.getMethod("text", net.kyori.adventure.text.Component::class.java)
            textMethod.invoke(display, component)

            val billboardStr = plugin.configYml.getString("damage-indicators.display-entity.billboard")
            val billboardValues = billboardClass.enumConstants as Array<*>
            val billboard = billboardValues.firstOrNull { 
                (it as Enum<*>).name.equals(billboardStr, ignoreCase = true) 
            } ?: billboardValues.firstOrNull { (it as Enum<*>).name == "CENTER" }
            
            if (billboard != null) {
                val setBillboardMethod = displayClass.getMethod("setBillboard", billboardClass)
                setBillboardMethod.invoke(display, billboard)
            }

            val colorInt = plugin.configYml.getInt("damage-indicators.display-entity.background-color")
            if (colorInt != -1) {
                try {
                    val colorClass = Class.forName("org.bukkit.Color")
                    val fromARGBMethod = colorClass.getMethod("fromARGB", Int::class.java)
                    val color = fromARGBMethod.invoke(null, colorInt)
                    val setBackgroundColorMethod = textDisplayClass.getMethod("setBackgroundColor", colorClass)
                    setBackgroundColorMethod.invoke(display, color)
                } catch (e: Exception) {
                }
            }

            try {
                val shadow = plugin.configYml.getBool("damage-indicators.display-entity.shadow")
                val setShadowedMethod = textDisplayClass.getMethod("setShadowed", Boolean::class.java)
                setShadowedMethod.invoke(display, shadow)
            } catch (e: Exception) {
            }

            try {
                val defaultBg = plugin.configYml.getBool("damage-indicators.display-entity.default-background")
                val setDefaultBackgroundMethod = textDisplayClass.getMethod("setDefaultBackground", Boolean::class.java)
                setDefaultBackgroundMethod.invoke(display, defaultBg)
            } catch (e: Exception) {
            }

            try {
                val seeThrough = plugin.configYml.getBool("damage-indicators.display-entity.see-through")
                val setSeeThroughMethod = textDisplayClass.getMethod("setSeeThrough", Boolean::class.java)
                setSeeThroughMethod.invoke(display, seeThrough)
            } catch (e: Exception) {
            }

            try {
                val opacity = plugin.configYml.getInt("damage-indicators.display-entity.text-opacity")
                    .coerceIn(0, 255).toByte()
                val setTextOpacityMethod = textDisplayClass.getMethod("setTextOpacity", Byte::class.java)
                setTextOpacityMethod.invoke(display, opacity)
            } catch (e: Exception) {
            }

            try {
                val viewRange = plugin.configYml.getDouble("damage-indicators.display-entity.view-range").toFloat()
                val setViewRangeMethod = displayClass.getMethod("setViewRange", Float::class.java)
                setViewRangeMethod.invoke(display, viewRange)
            } catch (e: Exception) {
            }

            display.isPersistent = false

        } catch (e: Exception) {
            plugin.logger.warning("Failed to spawn TextDisplay hologram: ${e.message}")
            textDisplay = null
        }
    }

    private fun convertLegacyToMiniMessage(input: String): String {
        var result = input.replace("§", "&")
        
        result = result.replace("&#([0-9a-fA-F]{6})".toRegex()) { match ->
            "<#${match.groupValues[1]}>"
        }
        
        result = result.replace("&([0-9a-fk-or])".toRegex()) { match ->
            when (val code = match.groupValues[1].lowercase()) {
                "0" -> "<black>"
                "1" -> "<dark_blue>"
                "2" -> "<dark_green>"
                "3" -> "<dark_aqua>"
                "4" -> "<dark_red>"
                "5" -> "<dark_purple>"
                "6" -> "<gold>"
                "7" -> "<gray>"
                "8" -> "<dark_gray>"
                "9" -> "<blue>"
                "a" -> "<green>"
                "b" -> "<aqua>"
                "c" -> "<red>"
                "d" -> "<light_purple>"
                "e" -> "<yellow>"
                "f" -> "<white>"
                "k" -> "<obfuscated>"
                "l" -> "<bold>"
                "m" -> "<strikethrough>"
                "n" -> "<underlined>"
                "o" -> "<italic>"
                "r" -> "<reset>"
                else -> match.value
            }
        }
        
        return result
    }

    /**
     * Remove the hologram TextDisplay entity
     */
    fun remove() {
        textDisplay?.remove()
        textDisplay = null
    }

    companion object {
        private var supported: Boolean? = null

        /**
         * Check if the server supports TextDisplay entities (1.19.4+)
         */
        fun isSupported(): Boolean {
            if (supported == null) {
                supported = try {
                    Class.forName("org.bukkit.entity.TextDisplay")
                    true
                } catch (e: ClassNotFoundException) {
                    false
                }
            }
            return supported!!
        }
    }
}
