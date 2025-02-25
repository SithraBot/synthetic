package org.sithra.synthetic.store

import kotlin.uuid.Uuid

/**
 * IMessagesStore is an interface representing a store for chat messages.
 */
interface IMessagesStore {
    /**
     * A session with a unique identifier and chat model.
     */
    class MessagesSession(
        override val sessionId: Uuid,
        override val chatModel: String,
        override val messages: MutableList<IMessage> = mutableListOf()
    ) : ISession {
        override fun addMessage(message: IMessage) = messages.add(message)
    }

    /**
     * Returns a list of all sessions.
     *
     * @return A list of all sessions.
     */
    fun getAllSessions(): List<Uuid>

    /**
     * Returns the [MessagesSession] for a given session id.
     *
     * @param sessionId The session id.
     * @return The [MessagesSession] for the given session id.
     */
    fun getMessagesBySessionId(sessionId: Uuid): MessagesSession?

    /**
     * Creates a new session in the store.
     *
     * @param chatModel The model used for the chat session.
     * @return The unique identifier for the session.
     */
    fun createSession(chatModel: String): Uuid

    /**
     * Deletes a session from the store.
     *
     * @param sessionId The unique identifier for the session.
     */
    fun deleteSession(sessionId: Uuid)
}