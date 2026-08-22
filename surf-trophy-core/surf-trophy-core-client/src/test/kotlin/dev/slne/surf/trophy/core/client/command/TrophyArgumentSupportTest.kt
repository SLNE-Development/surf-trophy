package dev.slne.surf.trophy.core.client.command

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TrophyArgumentSupportTest {

    @Test
    fun `unquotes a suggested trophy name`() {
        assertEquals(
            "Bester Bauer 2024",
            TrophyArgumentSupport.normalizeInput("\"Bester Bauer 2024\"")
        )
    }

    @Test
    fun `leaves an unquoted name untouched`() {
        assertEquals("Eventsieger", TrophyArgumentSupport.normalizeInput("Eventsieger"))
    }

    @Test
    fun `drops quotes that appear inside a name`() {
        assertEquals("Der Beste", TrophyArgumentSupport.normalizeInput("Der \"Beste\""))
    }
}
