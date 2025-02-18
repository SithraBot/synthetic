package adapter.casing.openai

import adapter.IChatService
import context.Message
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.serialization.json.*
import kotlinx.serialization.Serializable

@Suppress("unused")
class ChatService(
    private val client: HttpClient,
    private val config: OpenAIAdapter.Config,
    private val json: Json = Json
) :
    IChatService {
    @Serializable
    data class Request(
        val model: String,
        val messages: List<Message>,
        val temperature: Double,
        var stream: Boolean = false
    ) {
        companion object {
            @JvmStatic
            fun fromChatRequest(chatRequest: IChatService.ChatRequest): Request {
                return Request(chatRequest.model ?: "gpt-3.5-turbo", chatRequest.messages, chatRequest.temperature)
            }
        }

        fun stream(): Request {
            this.stream = true
            return this
        }
    }

    @Serializable
    data class Choice(val message: Message)

    @Serializable
    data class Response(val choices: List<Choice>)

    private val chatUrl =
        URLBuilder(config.baseUrl).apply { path(config.baseUrl.fullPath, "chat", "completions") }.build()

    override suspend fun chat(body: IChatService.ChatRequest): IChatService.ChatResponse {
        val response = client.post(chatUrl) {
            contentType(ContentType.Application.Json)
            bearerAuth(config.token)
            setBody(json.encodeToString(Request.fromChatRequest(body)))
        }
        val bodyString: String = response.body()
        if (response.status.value >= 400) throw Exception(bodyString)
        val responseData = json.decodeFromString<Response>(bodyString)
        return IChatService.ChatResponse(responseData.choices[0].message)
    }

    @Serializable
    data class MessageWithoutRole(val content: String)

    @Serializable
    data class StreamChoice(val delta: MessageWithoutRole)

    @Serializable
    data class StreamResponse(val choices: List<StreamChoice>)

    override suspend fun chatStream(
        body: IChatService.ChatRequest,
        block: suspend (content: String?, done: Boolean) -> Unit
    ) {
        client.preparePost(chatUrl) {
            contentType(ContentType.Application.Json)
            bearerAuth(config.token)
            setBody(json.encodeToString(Request.fromChatRequest(body).stream()))
        }.execute { response ->
            val channel: ByteReadChannel = response.body()
            if (response.status.value >= 400) throw Exception(response.bodyAsText())
            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line()
                if (line != null && line.startsWith("data:")) {
                    val data = line.removePrefix("data:").trim()
                    if (data == "[DONE]") {
                        block(null, true)
                    } else {
                        val streamResponse = json.decodeFromString<StreamResponse>(data)
                        val message = streamResponse.choices[0].delta.content
                        block(message, false)
                    }
                }
            }
            println()
        }
    }
}