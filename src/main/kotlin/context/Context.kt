package context

import adapter.IAdapter
import adapter.IChatService
import adapter.IEmbeddedService
import store.IDocument
import store.IMessagesStore
import store.IRAGBase

open class Context(
    apiAdapter: IAdapter,
    override val messagesStore: IMessagesStore,
) : IContext {
    override val chatService: IChatService = apiAdapter.getChatService()
    override val embeddedService: IEmbeddedService = apiAdapter.getEmbeddedService()

    class ContextBuilder(
        var apiAdapter: IAdapter? = null,
        var messagesStore: IMessagesStore? = null,
    ) {
        fun setApiAdapter(apiAdapter: IAdapter) = apply { this.apiAdapter = apiAdapter }
        fun setMessagesStore(messagesStore: IMessagesStore) = apply { this.messagesStore = messagesStore }
        fun build() = Context(apiAdapter!!, messagesStore!!)
    }
}