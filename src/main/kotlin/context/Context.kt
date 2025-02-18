package context

import adapter.IAdapter
import adapter.IChatService
import adapter.IEmbeddedService
import store.IMessagesStore
import store.IRAGBase

open class Context(
    apiAdapter: IAdapter,
    override val messagesStore: IMessagesStore,
    override val ragBase: IRAGBase?
) : IContext {
    override val chatService: IChatService = apiAdapter.getChatService()
    override val embeddedService: IEmbeddedService = apiAdapter.getEmbeddedService()

    class ContextBuilder(
        var apiAdapter: IAdapter? = null,
        var messagesStore: IMessagesStore? = null,
        var ragBase: IRAGBase? = null
    ) {
        fun setApiAdapter(apiAdapter: IAdapter) = apply { this.apiAdapter = apiAdapter }
        fun setMessagesStore(messagesStore: IMessagesStore) = apply { this.messagesStore = messagesStore }
        fun setRAGBase(ragBase: IRAGBase) = apply { this.ragBase = ragBase }
        fun build() = Context(apiAdapter!!, messagesStore!!, ragBase)
    }
}