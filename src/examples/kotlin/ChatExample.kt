package examples

import adapter.casing.openai.OpenAIAdapter
import context.ContextManager
import store.Message
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import store.casing.InMemoryMessagesStore

@Suppress("unused")
object ChatExample {
    private const val API_KEY = "..."
    private val url = Url("...")
    fun main() = runBlocking {
        val adapter = OpenAIAdapter {
            baseUrl = url
            token = API_KEY
        }
        val contextManager = ContextManager {
            apiAdapter = adapter
            messagesStore = InMemoryMessagesStore()
        }
        val session = contextManager.createSession("...")

        contextManager.withSession(session) {
            val message = chat(Message("why hello world?"))
            println(message.content)
            addMessage(message)
        }
    }
}