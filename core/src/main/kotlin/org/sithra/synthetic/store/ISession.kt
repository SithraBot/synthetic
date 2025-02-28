package org.sithra.synthetic.store

import kotlin.uuid.Uuid

/**
 * Interface representing a session with a unique identifier and chat model.
 *
 * @property sessionId The unique identifier for the session.
 * @property chatModel The model used for the chat session.
 * @property messages The list of messages associated with the session.
 */
interface ISession {
    val sessionId: Uuid
    val chatModel: String
    val messages: List<IMessage>
    /**
     * Adds a message to the session. Returns true if the message was added successfully, false if the message is already in the session.
     *
     * @param message The message to be added to the session.
     * @return True if the message was added successfully.
     */
    fun addMessage(message: IMessage): Boolean
    /**
     * Adds a collection of messages to the session.
     * Returns true if all messages were added successfully.
     *
     * @param messages The collection of messages to be added to the session.
     * @return True if all messages were added successfully.
     */
    fun addMessages(messages: Collection<IMessage>) {
        messages.forEach { addMessage(it) }
    }
}