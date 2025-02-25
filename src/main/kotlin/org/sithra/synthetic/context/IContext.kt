package org.sithra.synthetic.context

import org.sithra.synthetic.adapter.IChatService
import kotlinx.coroutines.flow.Flow
import org.sithra.synthetic.rag.IDocument
import org.sithra.synthetic.rag.IRetriever
import org.sithra.synthetic.store.*
import org.sithra.synthetic.tools.ITools
import org.sithra.synthetic.tools.toolcall.IToolCall
import kotlin.uuid.Uuid

/**
 * IContext is an interface representing a context for handling chat requests and storing chat messages.
 *
 * @see Context
 */
interface IContext {
    /**
     * The chat service used to handle chat requests.
     */
    val chatService: IChatService

    /**
     * The store used to store chat messages.
     */
    val messagesStore: IMessagesStore

    /**
     * Create a new session in the store.
     *
     * @param chatModel The model used for the chat session.
     * @return The unique identifier for the session.
     */
    fun createSession(chatModel: String): Uuid {
        return messagesStore.createSession(chatModel)
    }

    /**
     * Add a message to the store.
     *
     * @param message The message to add.
     */
    suspend fun ISession.chat(message: Message): Message {
        addMessage(message)
        val chatMessages = messages
        val response = chatService.chat {
            model = chatModel
            messages = chatMessages
        }
        return response
    }

    /**
     * Handle a chat request in a streaming fashion.
     *
     * @param message The message to handle.
     * @return The response from the chat service as a Flow of strings.
     */
    fun ISession.chatStream(message: Message): Flow<String> {
        addMessage(message)
        val chatMessages = messages
        return chatService.chatStream {
            model = chatModel
            messages = chatMessages
        }
    }

    /**
     * A class representing a context with a session and tools.
     *
     * @see IContext
     * @see ITools
     * @see ISession
     */
    class ContextWithSessionAndTools<T : IToolCall>(
        val session: ISession,
        val tools: ITools<T>,
        val context: IContext
    ) :
        IContext by context,
        ITools<T> by tools,
        ISession by session

    /**
     * Add tools to the session and execute a callback function.
     *
     * @param tools The tools to add.
     * @param callback The callback function to execute.
     */
    suspend fun <T : IToolCall> ISession.withTools(
        tools: ITools<T>,
        callback: suspend ContextWithSessionAndTools<T>.() -> Unit
    ) {
        val context = ContextWithSessionAndTools(this, tools, this@IContext)
        return context.callback()
    }

    /**
     * Handle a chat request with tools.
     *
     * @param message The message to handle.
     * @param tools The tools to use.
     * @return The response from the chat service.
     */
    suspend fun <T : IToolCall> ISession.chatWithTools(message: Message, tools: ITools<T>): IMessage {
        addMessage(message)
        val chatMessages = messages
        return chatService.chatWithTools(tools) {
            model = chatModel
            messages = chatMessages
        }
    }

    /**
     * Handle a result of a chat request with tools.
     *
     * @param result The result of the chat request.
     * @param tools The tools to use.
     * @return The response from the chat service.
     */
    suspend fun <T : IToolCall> ISession.chatWithTools(result: List<ToolResultMessage>, tools: ITools<T>): IMessage {
        addMessages(result)
        val chatMessages = messages
        return chatService.chatWithTools(tools) {
            model = chatModel
            messages = chatMessages
        }
    }

    /**
     * A class representing a context with a session.
     *
     * @see IContext
     * @see ISession
     */
    class ContextWithSession(val session: ISession, val context: IContext) :
        IContext by context,
        ISession by session

    /**
     * Execute a callback function with a session.
     *
     * @param sessionId The unique identifier for the session.
     * @param callback The callback function to execute.
     */
    suspend fun withSession(sessionId: Uuid, callback: suspend ContextWithSession.() -> Unit) {
        val session = messagesStore.getMessagesBySessionId(sessionId) ?: return
        val context = ContextWithSession(session, this)
        context.callback()
    }

    /**
     * A class representing a context with a retriever.
     *
     * @see IContext
     * @see IRetriever
     */
    class ContextWithRetriever<Doc : IDocument>(val retriever: IRetriever<Doc>, val context: IContext) :
        IContext by context,
        IRetriever<Doc> by retriever

    /**
     * Execute a callback function with a retriever.
     *
     * @param retriever The retriever to use.
     * @param callback The callback function to execute.
     */
    suspend fun <Doc : IDocument> ContextWithSession.withRetriever(
        retriever: IRetriever<Doc>,
        callback: suspend ContextWithRetriever<Doc>.() -> Unit
    ) {
        val context = ContextWithRetriever(retriever, this)
        context.callback()
    }
}