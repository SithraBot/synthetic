package context

import store.IDocument
import store.IMessagesStore
import store.IMessagesStore.MessagesSession
import kotlin.uuid.Uuid

class ContextManager<Doc : IDocument>(private val center: IContext<Doc>) : IContext<Doc> by center {
    companion object {
        operator fun invoke(center: Context.ContextBuilder.() -> Unit) =
            ContextManager(Context.ContextBuilder().apply(center).build())
    }
}