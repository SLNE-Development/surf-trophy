package dev.slne.surf.trophy.core.client.message

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime
import java.time.ZoneOffset

class TrophyComponentsTest {

    @Test
    fun `formats a receive time in day-first notation`() {
        val moment = OffsetDateTime.of(2024, 3, 7, 8, 5, 42, 0, ZoneOffset.UTC)

        assertEquals("07.03.2024 09:05", formatReceivedAt(moment.toInstant().toEpochMilli()))
    }

    @Test
    fun `formats a receive time in the network time zone rather than UTC`() {
        val midsummerNoonUtc = OffsetDateTime.of(2024, 6, 21, 12, 0, 0, 0, ZoneOffset.UTC)

        assertEquals(
            "21.06.2024 14:00",
            formatReceivedAt(midsummerNoonUtc.toInstant().toEpochMilli())
        )
    }
}
