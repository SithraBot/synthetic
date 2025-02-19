package store

import context.Message
import kotlin.uuid.Uuid

interface IMessagesStore {
    class MessagesSession(
        override val sessionId: Uuid,
        override val chatModel: String,
        override val embedModel: String,
        override val messages: MutableList<Message> = mutableListOf()
    ) : ISession {
        override fun addMessage(message: Message) = messages.add(message)
    }

    fun getAllSessions(): List<Uuid>
    fun getMessagesBySessionId(sessionId: Uuid): MessagesSession?
    fun createSession(chatModel: String, embedModel: String): Uuid
    fun deleteSession(sessionId: Uuid)
}