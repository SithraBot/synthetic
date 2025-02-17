package adapter

interface IEmbeddedService {
    data class EmbeddedRequest(
        val input: String,
        val model: String?,
    )

    class EmbeddedRequestBuilder(var input: String? = null, var model: String? = null) {
        fun setInput(input: String) = apply { this.input = input }

        fun input(input: () -> String) = setInput(input())

        fun setModel(model: String) = apply { this.model = model }

        fun model(model: () -> String) = setModel(model())

        fun build(): EmbeddedRequest {
            return EmbeddedRequest(input ?: throw IllegalArgumentException("MUST HAVE INPUT"), model)
        }
    }

    data class EmbeddedResponse(val embedding: FloatArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is EmbeddedResponse) return false

            if (!embedding.contentEquals(other.embedding)) return false

            return true
        }

        override fun hashCode(): Int {
            return embedding.contentHashCode()
        }
    }

    suspend fun embed(body: EmbeddedRequest): EmbeddedResponse

    suspend fun embed(bodyBuilder: EmbeddedRequestBuilder.() -> Unit): EmbeddedResponse {
        val builder = EmbeddedRequestBuilder(null, null)
        builder.bodyBuilder()
        return embed(builder.build())
    }

    suspend fun embed(model: String?, input: String): EmbeddedResponse =
        embed(EmbeddedRequest(input, model))
}