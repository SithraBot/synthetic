package adapter.casing.openai

import adapter.IChatService
import adapter.casing.HybridTest
import context.Message
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

internal object ChatServiceTest {
    private val testChatService = OpenAIAdapterTest.testAIAdapter.getChatService()

    @Test
    fun chat() {
        val response = runBlocking {
            testChatService.chat {
                model = "deepseek-chat"
                messages = listOf(Message(role = Message.Role.USER, content = "hello"))
            }
        }
        assert(response.message.role == Message.Role.ASSISTANT)
        assert(response.message.content.isNotEmpty())
        println(response.message.content)
    }

    @Test
    fun chatStream() = runBlocking {
        testChatService.chatStream({
            model { "deepseek-chat" }
            messages { listOf(Message(role = Message.Role.USER, content = "hello")) }
        }) { content, done ->
            if (done) {
                println()
            } else {
                print(content)
            }
        }
    }

}