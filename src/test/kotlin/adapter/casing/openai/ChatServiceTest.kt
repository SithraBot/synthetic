package adapter.casing.openai

import org.sithra.synthetic.store.IMessage
import org.sithra.synthetic.store.Message
import kotlinx.coroutines.runBlocking
import kotlin.test.Test

internal object ChatServiceTest {
    private val testChatService = OpenAIAdapterTest.testAIAdapter.chatService

    @Test
    fun chat() {
        val response = runBlocking {
            testChatService.chat {
                model = "deepseek-r1-distill-qwen-1.5b"
                messages = listOf(Message(role = IMessage.Role.USER, content = "hello"))
            }
        }
        assert(response.role == IMessage.Role.ASSISTANT)
        assert(response.content.isNotEmpty())
        println(response.content)
    }

    @Test
    fun chatStream() = runBlocking {
        val response = testChatService.chatStream({
            model { "deepseek-r1-distill-qwen-1.5b" }
            messages { listOf(Message(role = IMessage.Role.USER, content = "hello")) }
        })
        response.collect {
            print(it)
        }
        println()
    }

}