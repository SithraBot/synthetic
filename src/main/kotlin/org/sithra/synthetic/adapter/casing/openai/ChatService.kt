package org.sithra.synthetic.adapter.casing.openai

import org.sithra.synthetic.adapter.IChatService
import org.sithra.synthetic.store.Message
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.serialization.json.*
import kotlinx.serialization.Serializable
import org.sithra.synthetic.store.IMessage
import org.sithra.synthetic.tools.ITools
import org.sithra.synthetic.tools.toolcall.IToolCall

/**
 * A chat service that uses the OpenAI API.
 *
 * @property client The HTTP client to use for making requests.
 * @property config The configuration for the OpenAI API.
 * @property json The JSON serializer to use for encoding and decoding data.
 */
class ChatService(
    private val client: HttpClient,
    private val config: OpenAIAdapter.Config,
    private val json: Json = Json
) :
    IChatService {
    @Serializable

    /**
     * Represents a request to chat with the model.
     *
     * @property model The ID of the model to use.
     * @property messages The messages to send to the model. The messages are sent in the order they are specified in the list.
     * @property temperature The temperature of the model.
     * A higher value will make the model more "creative" and a lower value will make it more conservative.
     * @property stream Whether the response should be streamed back.
     * If this is `true`, the response will be a stream of messages, otherwise it will be a single message.
     * @property tools The tools to use, if any. This is an optional field.
     */
    data class Request(
        val model: String,
        val messages: List<CompatibleMessage>,
        val temperature: Double,
        var stream: Boolean = false,
        var tools: JsonElement? = null
    ) {
        companion object {
            @JvmStatic
            fun fromChatRequest(chatRequest: IChatService.ChatRequest): Request {
                return Request(
                    chatRequest.model ?: "gpt-3.5-turbo",
                    chatRequest.messages.map { CompatibleMessage.fromMessage(it) },
                    chatRequest.temperature
                )
            }
        }

        fun tools(tools: JsonElement) = apply { this.tools = tools }

        fun stream() = apply { this.stream = true }
    }

    @Serializable
    data class Choice<M>(val message: M)

    @Serializable
    data class Response<M>(val choices: List<Choice<M>>)

    private val chatUrl =
        URLBuilder(config.baseUrl).apply { path(config.baseUrl.fullPath, "chat", "completions") }.build()

    override suspend fun chat(body: IChatService.ChatRequest): Message {
        val response = client.post(chatUrl) {
            contentType(ContentType.Application.Json)
            bearerAuth(config.token)
            setBody(json.encodeToString(Request.fromChatRequest(body)))
        }
        val bodyString: String = response.body()
        if (response.status.value >= 400) throw Exception(bodyString)
        val responseData = json.decodeFromString<Response<Message>>(bodyString)
        return responseData.choices[0].message
    }

    @Serializable
    data class MessageWithoutRole(val content: String)

    @Serializable
    data class StreamChoice(val delta: MessageWithoutRole)

    @Serializable
    data class StreamResponse(val choices: List<StreamChoice>)

    override fun chatStream(
        body: IChatService.ChatRequest,
    ): Flow<String> = callbackFlow {
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
                        close()
                    } else {
                        val streamResponse = json.decodeFromString<StreamResponse>(data)
                        val message = streamResponse.choices[0].delta.content
                        if (message.isNotEmpty()) trySend(message).onFailure { e -> cancel("Error sending message", e) }
                    }
                }
            }
            println()
        }
    }

    override suspend fun <T : IToolCall> chatWithTools(
        body: IChatService.ChatRequest,
        tools: ITools<T>
    ): IMessage {
        val toolSchema = tools.getJsonObject()
        val response = client.post(chatUrl) {
            contentType(ContentType.Application.Json)
            bearerAuth(config.token)
            setBody(json.encodeToString(Request.fromChatRequest(body).tools(toolSchema)))
        }
        val bodyString: String = response.body()
        if (response.status.value >= 400) throw Exception(bodyString)
        val responseData = json.decodeFromString<Response<CompatibleMessage>>(bodyString)
        return responseData.choices[0].message.toMessage()
    }
}