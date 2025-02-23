package context

import adapter.IAdapter
import adapter.IChatService
import store.IMessagesStore

open class Context(
    override val chatService: IChatService,
    override val messagesStore: IMessagesStore,
) : IContext {

    class ContextBuilder(
        var chatService: IChatService? = null,
        var apiAdapter: IAdapter? = null,
        var messagesStore: IMessagesStore? = null,
    ) {
        fun setChatService(chatService: IChatService) = apply { this.chatService = chatService }
        fun setChatService(adapter: IAdapter) = apply { this.chatService = adapter.getChatService() }
        fun setAdapter(adapter: IAdapter) = apply { this.apiAdapter = adapter }
        fun setMessagesStore(messagesStore: IMessagesStore) = apply { this.messagesStore = messagesStore }
        fun build() =
            Context(chatService ?: apiAdapter?.getChatService() ?: throw Exception("ChatService not set"), messagesStore!!)
    }
}