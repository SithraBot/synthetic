package context

import adapter.IAdapter
import store.IMessagesStore

class Context(apiAdapter: IAdapter, val messagesStore: IMessagesStore) {
    class ContextBuilder(var apiAdapter: IAdapter? = null, var messagesStore: IMessagesStore? = null) {
        fun setApiAdapter(apiAdapter: IAdapter) = apply { this.apiAdapter = apiAdapter }
        fun setMessagesStore(messagesStore: IMessagesStore) = apply { this.messagesStore = messagesStore }
        fun build() = Context(apiAdapter!!, messagesStore!!)
    }

    val chatService = apiAdapter.getChatService()
    val embeddedService = apiAdapter.getEmbeddedService()
}