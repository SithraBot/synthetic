package context

import adapter.IChatService
import adapter.IEmbeddedService
import kotlinx.coroutines.flow.Flow
import store.IDocument
import store.IMessagesStore
import store.IRAGBase
import store.ISession
import kotlin.uuid.Uuid

interface IContext {
    val chatService: IChatService
    val embeddedService: IEmbeddedService
    val messagesStore: IMessagesStore

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

    fun ISession.chatStream(message: Message): Flow<String> {
        addMessage(message)
        val chatMessages = messages
        return chatService.chatStream({
            model = chatModel
            messages = chatMessages
        })
    }

    class ContextWithSession(val session: ISession, val context: IContext) :
        IContext by context,
        ISession by session

    suspend fun withSession(sessionId: Uuid, callback: suspend ContextWithSession.() -> Unit) {
        val session = messagesStore.getMessagesBySessionId(sessionId) ?: return
        val context = ContextWithSession(session, this)
        context.callback()
    }

    class ContextWithRAGBase<Doc : IDocument>(val ragBase: IRAGBase<Doc>, val context: IContext, val model: String) :
        IContext by context,
        IRAGBase<Doc> by ragBase

    class ContextWithRAGBaseAndSession<Doc : IDocument>(
        val session: ISession,
        val ragBase: IRAGBase<Doc>,
        val context: IContext,
    ) :
        IContext by context,
        IRAGBase<Doc> by ragBase,
        ISession by session

    suspend fun <Doc : IDocument> withRAGBase(
        ragBase: IRAGBase<Doc>,
        model: String,
        callback: suspend ContextWithRAGBase<Doc>.() -> Unit
    ) {
        val context = ContextWithRAGBase(ragBase, this, model)
        context.callback()
    }


    suspend fun <Doc : IDocument> ContextWithSession.withRAGBase(
        ragBase: IRAGBase<Doc>,
        callback: suspend ContextWithRAGBase<Doc>.() -> Unit
    ) {
        val context = ContextWithRAGBase(ragBase, this, embedModel)
        context.callback()
    }
}