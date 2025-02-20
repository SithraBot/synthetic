package store

import kotlin.uuid.Uuid

interface IMessagesStore {
    class MessagesSession(
        override val sessionId: Uuid,
        override val chatModel: String,
        override val messages: MutableList<IMessage> = mutableListOf()
    ) : ISession {
        override fun addMessage(message: IMessage) = messages.add(message)
    }

    fun getAllSessions(): List<Uuid>
    fun getMessagesBySessionId(sessionId: Uuid): MessagesSession?
    fun createSession(chatModel: String): Uuid
    fun deleteSession(sessionId: Uuid)
}