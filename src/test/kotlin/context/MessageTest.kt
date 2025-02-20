package context

import kotlinx.serialization.json.Json
import store.IMessage
import store.Message
import kotlin.test.Test
import kotlin.test.assertEquals

internal object MessageTest {
    private val json = Json { encodeDefaults = true }

    @Test
    fun serialize() {
        val message = Message(role = IMessage.Role.USER, content = "world")
        assertEquals(
            json.decodeFromString(json.encodeToString(message)),
            json.decodeFromString<Message>("{\"role\":\"user\",\"content\":\"world\"}")
        )
    }

    @Test
    fun deserialize() {
        val json = "{\"role\":\"user\",\"content\":\"world\"}"
        val message = Json.decodeFromString<Message>(json)
        assertEquals(message.role, IMessage.Role.USER)
        assertEquals(message.content, "world")
    }
}