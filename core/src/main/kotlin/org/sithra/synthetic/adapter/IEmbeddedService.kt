package org.sithra.synthetic.adapter

/**
 * IEmbeddedService is an interface that defines the contract for an embedding service.
 *
 * This service provides functionality to generate embeddings for a given input text.
 * It includes a request and a response data class, as well as a builder class for creating requests.
 */
interface IEmbeddedService {

    /**
     * A data class representing a request to generate embeddings for a given text.
     *
     * @property input  The text to be embedded.
     * @property model  The model to be used for embedding. If not provided, the default model is used.
     */
    data class EmbeddedRequest(
        val input: String,
        val model: String?,
    )

    /**
     * A data class representing the response from the embedding service.
     *
     * @param embedding  The embedded vectors.
     */
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

    /**
     * A builder class for creating EmbeddedRequest instances.
     *
     * @param input  The text to be embedded.
     * @param model  The model to be used for embedding. If not provided, the default model is used.
     */
    class EmbeddedRequestBuilder(var input: String? = null, var model: String? = null) {
        fun setInput(input: String) = apply { this.input = input }

        fun input(input: () -> String) = setInput(input())

        fun setModel(model: String) = apply { this.model = model }

        fun model(model: () -> String) = setModel(model())

        fun build(): EmbeddedRequest {
            return EmbeddedRequest(input ?: throw IllegalArgumentException("MUST HAVE INPUT"), model)
        }
    }

    /**
     * Generates embeddings for a given text.
     *
     * @param body  The request to generate embeddings for.
     * @return  The embedded vectors.
     */
    suspend fun embed(body: EmbeddedRequest): EmbeddedResponse

    /**
     * Generates embeddings for a given text by evaluating the given lambda expression.
     *
     * This is a convenience function for generating embeddings without having to explicitly call the lambda expression.
     *
     * @param bodyBuilder  The lambda expression which returns the request to generate embeddings for.
     * @return  The embedded vectors.
     */
    suspend fun embed(bodyBuilder: EmbeddedRequestBuilder.() -> Unit): EmbeddedResponse {
        val builder = EmbeddedRequestBuilder(null, null)
        builder.bodyBuilder()
        return embed(builder.build())
    }

    suspend fun embed(model: String?, input: String): EmbeddedResponse =
        embed(EmbeddedRequest(input, model))
}