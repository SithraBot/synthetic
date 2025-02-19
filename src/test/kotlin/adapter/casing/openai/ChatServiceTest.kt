package adapter.casing.openai

import context.Message
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

internal object ChatServiceTest {
    private val testChatService = OpenAIAdapterTest.testAIAdapter.getChatService()

    @Test
    fun chat() {
        val response = runBlocking {
            testChatService.chat {
                model = "deepseek-r1-distill-qwen-1.5b"
                messages = listOf(Message(role = Message.Role.USER, content = "hello"))
            }
        }
        assert(response.message.role == Message.Role.ASSISTANT)
        assert(response.message.content.isNotEmpty())
        println(response.message.content)
    }

    @Test
    fun chatStream() = runBlocking {
        val response = testChatService.chatStream({
            model { "deepseek-r1-distill-qwen-1.5b" }
            messages { listOf(Message(role = Message.Role.USER, content = "hello")) }
        })
        response.collect {
            print(it)
        }
        println()
    }

}