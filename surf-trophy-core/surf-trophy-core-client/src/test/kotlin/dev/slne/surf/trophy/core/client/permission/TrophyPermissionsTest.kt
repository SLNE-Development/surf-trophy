package dev.slne.surf.trophy.core.client.permission

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TrophyPermissionsTest {

    @Test
    fun `command nodes keep their published names`() {
        assertEquals("surf.trophy.command.trophy", TrophyPermissions.COMMAND_TROPHY)
        assertEquals("surf.trophy.command.trophy.others", TrophyPermissions.COMMAND_TROPHY_OTHERS)
        assertEquals("surf.trophy.command.trophy.reload", TrophyPermissions.COMMAND_TROPHY_RELOAD)
        assertEquals("surf.trophy.command.trophy.refresh", TrophyPermissions.COMMAND_TROPHY_REFRESH)
        assertEquals("surf.trophy.command.trophy.manage", TrophyPermissions.COMMAND_TROPHY_MANAGE)
        assertEquals("surf.trophy.command.trophy.create", TrophyPermissions.COMMAND_TROPHY_CREATE)
        assertEquals("surf.trophy.command.trophy.list", TrophyPermissions.COMMAND_TROPHY_LIST)
        assertEquals("surf.trophy.command.trophy.delete", TrophyPermissions.COMMAND_TROPHY_DELETE)
    }
}
