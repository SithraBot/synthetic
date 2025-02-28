package context

import Props
import adapter.openAIAdapter
import kotlinx.coroutines.runBlocking
import org.sithra.synthetic.context.Context
import org.sithra.synthetic.store.TextMessage
import store.imMessagesStore
import kotlin.test.Test

val ctx = Context {
    apiAdapter = openAIAdapter
    messagesStore = imMessagesStore
}

class TestContext {
    @Test
    fun testChatWithSession() = runBlocking {
        val id = ctx.createSession(Props.OpenAI.model)
        val message = ctx.withSession(id) {
            chat(TextMessage("Hello"))
        }
        println(if (message is TextMessage) message.content else "not a text message")
    }
}