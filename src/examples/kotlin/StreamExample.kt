package examples

import adapter.casing.openai.OpenAIAdapter
import context.ContextManager
import context.Message
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import store.casing.inmemory.MessagesStoreInMemory

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
            messagesStore = MessagesStoreInMemory()
        }
        val session = contextManager.createSession("...", "...")
        contextManager.withSessionId(session) {
            chatStream(Message("why hello world?")) { content, done ->
                var message = ""
                if (done) {
                    addMessage(Message(message, Message.Role.ASSISTANT))
                    println()
                } else {
                    message += content
                    print(content)
                }
            }
        }
    }
}