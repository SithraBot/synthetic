package context

import kotlin.uuid.Uuid

class ContextWithSessionId(private val center: Context, sessionId: Uuid) {
    private val session = center.messagesStore.getMessagesBySessionId(sessionId) ?: throw Exception("Session not found")

    suspend fun chat(message: Message): Message {
        session.addMessage(message)
        val response = center.chatService.chat {
            model = session.chatModel
            messages = session.messages
        }
        return response.message
    }

    suspend fun chatStream(message: Message, block: suspend (content: String?, done: Boolean) -> Unit) {
        session.addMessage(message)
        center.chatService.chatStream({
            model = session.chatModel
            messages = session.messages
        }) { content, done -> block(content, done) }
    }

    fun addMessage(message: Message) = session.addMessage(message)
}