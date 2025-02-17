package context

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

internal object MessageTest {
    @Test
    fun serialize() {
        val message = Message(role = Message.Role.USER, content = "world")
        assertEquals(Json.encodeToString(message), "{\"role\":\"user\",\"content\":\"world\"}")
    }

    @Test
    fun deserialize() {
        val json = "{\"role\":\"user\",\"content\":\"world\"}"
        val message = Json.decodeFromString<Message>(json)
        assertEquals(message.role, Message.Role.USER)
        assertEquals(message.content, "world")
    }
}