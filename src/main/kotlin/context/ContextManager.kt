package context

import store.IMessagesStore
import store.IMessagesStore.MessagesSession
import kotlin.uuid.Uuid

class ContextManager(private val center: IContext) : IContext by center {
    companion object {
        operator fun invoke(center: Context.ContextBuilder.() -> Unit) =
            ContextManager(Context.ContextBuilder().apply(center).build())
    }
}