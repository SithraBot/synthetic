package store

import context.Message
import kotlin.uuid.Uuid

interface ISession {
    val sessionId: Uuid
    val chatModel: String
    val embedModel: String
    val messages: List<Message>
    fun addMessage(message: Message): Boolean
}