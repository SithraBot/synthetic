package org.sithra.synthetic.context

import org.sithra.synthetic.adapter.IChatService
import kotlinx.coroutines.flow.Flow
import org.sithra.synthetic.rag.IDocument
import org.sithra.synthetic.rag.IRetriever
import org.sithra.synthetic.store.*
import org.sithra.synthetic.tools.ITools
import org.sithra.synthetic.tools.toolcall.IToolCall
import kotlin.uuid.Uuid

interface IContext {
    val chatService: IChatService
    val messagesStore: IMessagesStore

    fun createSession(chatModel: String): Uuid {
        return messagesStore.createSession(chatModel)
    }

    suspend fun ISession.chat(message: Message): Message {
        addMessage(message)
        val chatMessages = messages
        val response = chatService.chat {
            model = chatModel
            messages = chatMessages
        }
        return response
    }

    fun ISession.chatStream(message: Message): Flow<String> {
        addMessage(message)
        val chatMessages = messages
        return chatService.chatStream {
            model = chatModel
            messages = chatMessages
        }
    }

    class ContextWithSessionAndTools<T : IToolCall>(
        val session: ISession,
        val tools: ITools<T>,
        val context: IContext
    ) :
        IContext by context,
        ITools<T> by tools,
        ISession by session

    suspend fun <T : IToolCall> ISession.withTools(
        tools: ITools<T>,
        callback: suspend ContextWithSessionAndTools<T>.() -> Unit
    ) {
        val context = ContextWithSessionAndTools(this, tools, this@IContext)
        return context.callback()
    }

    suspend fun <T : IToolCall> ISession.chatWithTools(message: Message, tools: ITools<T>): IMessage {
        addMessage(message)
        val chatMessages = messages
        return chatService.chatWithTools(tools) {
            model = chatModel
            messages = chatMessages
        }
    }

    suspend fun <T : IToolCall> ISession.chatWithTools(result: List<ToolResultMessage>, tools: ITools<T>): IMessage {
        addMessages(result)
        val chatMessages = messages
        return chatService.chatWithTools(tools) {
            model = chatModel
            messages = chatMessages
        }
    }

    class ContextWithSession(val session: ISession, val context: IContext) :
        IContext by context,
        ISession by session

    suspend fun withSession(sessionId: Uuid, callback: suspend ContextWithSession.() -> Unit) {
        val session = messagesStore.getMessagesBySessionId(sessionId) ?: return
        val context = ContextWithSession(session, this)
        context.callback()
    }

    class ContextWithRAG<Doc : IDocument>(val retriever: IRetriever<Doc>, val context: IContext) :
        IContext by context,
        IRetriever<Doc> by retriever

    suspend fun <Doc : IDocument> ContextWithSession.withRAG(
        retriever: IRetriever<Doc>,
        callback: suspend ContextWithRAG<Doc>.() -> Unit
    ) {
        val context = ContextWithRAG(retriever, this)
        context.callback()
    }
}