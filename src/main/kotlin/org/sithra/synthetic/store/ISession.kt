package org.sithra.synthetic.store

import kotlin.uuid.Uuid

interface ISession {
    val sessionId: Uuid
    val chatModel: String
    val messages: List<IMessage>
    fun addMessage(message: IMessage): Boolean
    fun addMessages(messages: Collection<IMessage>) {
        messages.forEach { addMessage(it) }
    }
}