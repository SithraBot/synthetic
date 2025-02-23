package examples

import adapter.casing.openai.OpenAIAdapter
import context.ContextManager
import store.Message
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import store.IMessage
import store.casing.InMemoryMessagesStore

@Suppress("unused")
object StreamExample {
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
            val response = chatStream(Message("why hello world?"))
            var message = ""
            response.collect {
                message += it
                print(it)
            }
            println()
            addMessage(Message(message, IMessage.Role.ASSISTANT))
        }
    }
}