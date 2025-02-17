package store.casing.inmemory

import context.Message
import store.IMessagesStore
import kotlin.uuid.Uuid

class MessagesStoreInMemory : IMessagesStore {
    private val sessions = mutableMapOf<Uuid, IMessagesStore.MessagesSession>()

    override fun createSession(chatModel: String, embedModel: String): Uuid {
        val sessionId = Uuid.random()
        sessions[sessionId] = IMessagesStore.MessagesSession(sessionId, chatModel, embedModel)
        return sessionId
    }

    override fun getAllSessions(): List<Uuid> {
        return sessions.keys.toList()
    }

    override fun getMessagesBySessionId(sessionId: Uuid): IMessagesStore.MessagesSession? {
        return sessions[sessionId]
    }

    override fun deleteSession(sessionId: Uuid) {
        sessions.remove(sessionId)
    }
}