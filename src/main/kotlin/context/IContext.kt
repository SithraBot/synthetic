package context

import adapter.IChatService
import adapter.IEmbeddedService
import store.IMessagesStore
import store.IRAGBase
import store.ISession
import kotlin.uuid.Uuid

interface IContext {
    val chatService: IChatService
    val embeddedService: IEmbeddedService
    val messagesStore: IMessagesStore
    val ragBase: IRAGBase?

    fun createSession(chatModel: String, embedModel: String): Uuid {
        return messagesStore.createSession(chatModel, embedModel)
    }

    suspend fun ISession.chat(message: Message): Message {
        addMessage(message)
        val chatMessages = messages
        val response = chatService.chat {
            model = chatModel
            messages = chatMessages
        }
        return response.message
    }

    suspend fun ISession.chatStream(message: Message, block: suspend (content: String?, done: Boolean) -> Unit) {
        addMessage(message)
        val chatMessages = messages
        chatService.chatStream({
            model = chatModel
            messages = chatMessages
        }) { content, done -> block(content, done) }
    }

    class ContextWithSession(val session: ISession, val context: IContext) : IContext by context, ISession by session

    suspend fun withSession(sessionId: Uuid, callback: suspend ContextWithSession.() -> Unit) {
        val session = messagesStore.getMessagesBySessionId(sessionId) ?: return
        val context = ContextWithSession(session, this)
        context.callback()
    }
}