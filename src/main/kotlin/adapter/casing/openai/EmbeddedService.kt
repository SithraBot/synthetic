package adapter.casing.openai

import adapter.IEmbeddedService
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.json.*
import kotlinx.serialization.*

class EmbeddedService(
    private val client: HttpClient,
    private val config: OpenAIAdapter.Config,
    private val json: Json = Json
) :
    IEmbeddedService {
    private val embedUrl = URLBuilder(config.baseUrl).apply { path(config.baseUrl.fullPath, "embeddings") }.build()

    @Serializable
    data class Request(
        val input: String,
        val model: String,
        @SerialName("encoding_format") val encodingFormat: String = "float",
    ) {
        companion object {
            @JvmStatic
            fun fromEmbeddedRequest(body: IEmbeddedService.EmbeddedRequest): Request {
                return Request(body.input, body.model ?: "text-embedding-ada-002")
            }
        }
    }

    @Serializable
    data class Data(val embedding: List<Float>)

    @Serializable
    data class Response(val data: List<Data>)

    override suspend fun embed(body: IEmbeddedService.EmbeddedRequest): IEmbeddedService.EmbeddedResponse {
        val response = client.post(embedUrl) {
            contentType(ContentType.Application.Json)
            bearerAuth(config.token)
            setBody(json.encodeToString(Request.fromEmbeddedRequest(body)))
        }
        val bodyString: String = response.body()
        if (response.status.value >= 400) throw Exception(bodyString)
        val responseData = json.decodeFromString<Response>(bodyString)
        return IEmbeddedService.EmbeddedResponse(responseData.data[0].embedding.toFloatArray())
    }
}