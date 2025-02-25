package org.sithra.synthetic.context

import org.sithra.synthetic.adapter.IAdapter
import org.sithra.synthetic.adapter.IChatService
import org.sithra.synthetic.store.IMessagesStore

/**
 * A context for handling chat requests and storing chat messages.
 *
 * @property chatService The service used to handle chat requests.
 * @property messagesStore The store used to store chat messages.
 *
 * @see IContext
 */
open class Context(
    override val chatService: IChatService,
    override val messagesStore: IMessagesStore,
) : IContext {

    /**
     * Builder class for [Context].
     *
     * @param chatService The service used to handle chat requests.
     * @param apiAdapter The adapter used to handle chat requests.
     * @param messagesStore The store used to store chat messages.
     */
    class ContextBuilder(
        var chatService: IChatService? = null,
        var apiAdapter: IAdapter? = null,
        var messagesStore: IMessagesStore? = null,
    ) {
        /**
         * Set the chat service used to handle chat requests.
         *
         * @param chatService The service used to handle chat requests.
         */
        fun setChatService(chatService: IChatService) = apply { this.chatService = chatService }

        /**
         * Set the adapter used to handle chat requests.
         *
         * @param adapter The adapter used to handle chat requests.
         */
        fun setChatService(adapter: IAdapter) = apply { this.chatService = adapter.chatService }

        /**
         * Set the adapter used to handle chat requests.
         *
         * @param adapter The adapter used to handle chat requests.
         */
        fun setAdapter(adapter: IAdapter) = apply { this.apiAdapter = adapter }

        /**
         * Set the store used to store chat messages.
         *
         * @param messagesStore The store used to store chat messages.
         */
        fun setMessagesStore(messagesStore: IMessagesStore) = apply { this.messagesStore = messagesStore }

        /**
         * Build the [Context] object.
         *
         * @return The built [Context] object.
         */
        fun build() = Context(
            chatService ?: apiAdapter?.chatService ?: throw Exception("ChatService not set"), messagesStore!!
        )
    }

    companion object {
        /**
         * Create a new [Context] object with the given initial values.
         *
         * @param center The initial values for the context builder.
         * @return The built [Context] object.
         */
        operator fun invoke(center: ContextBuilder.() -> Unit) = ContextBuilder().apply(center).build()
    }
}