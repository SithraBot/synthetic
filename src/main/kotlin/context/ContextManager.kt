package context

import kotlin.uuid.Uuid

class ContextManager(private val center: Context) {
    companion object {
        operator fun invoke(center: Context.ContextBuilder.() -> Unit) =
            ContextManager(Context.ContextBuilder().apply(center).build())
    }

    fun createSession(chatModel: String, embedModel: String): Uuid =
        center.messagesStore.createSession(chatModel, embedModel)

    suspend fun withSessionId(sessionId: Uuid, callback: suspend ContextWithSessionId.() -> Unit) {
        val contextWithSessionId = ContextWithSessionId(center, sessionId)
        contextWithSessionId.callback()
    }

    fun getSessions() = center.messagesStore.getAllSessions()
    fun getSessionById(sessionId: Uuid) = center.messagesStore.getMessagesBySessionId(sessionId)
}