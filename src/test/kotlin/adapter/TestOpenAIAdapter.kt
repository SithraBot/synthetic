package adapter

import Props
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.sithra.synthetic.adapter.OpenAIAdapter
import org.sithra.synthetic.store.TextMessage
import kotlin.test.Test

val openAIAdapter = OpenAIAdapter {
    baseUrl = Url(Props.OpenAI.baseUrl)
    token = Props.OpenAI.apiKey
    httpClient = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 60_000
            connectTimeoutMillis = 60_000
            socketTimeoutMillis = 60_000
        }
    }
}

class TestOpenAIAdapter {
    @Test
    fun testChat() = runBlocking {
        val message = openAIAdapter.chatService.chat {
            messages = listOf(
                // TextMessage(role = IMessage.Role.SYSTEM, content = "You're a useless piece of shit. Please answer every question with one word."),
                TextMessage("Hello")
            )
            model = Props.OpenAI.model
        }
        println(if (message is TextMessage) message.content else "not a text message")
    }
}