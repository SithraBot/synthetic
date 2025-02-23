package rag.casing.retriever

import adapter.IAdapter
import adapter.IEmbeddedService
import kotlinx.serialization.Serializable
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.uuid.Uuid
import kotlinx.serialization.json.Json
import rag.IDocument
import rag.IReranker
import rag.IRetriever

class InMemoryVectorRetriever(
    private val embedModel: String,
    private val embeddedService: IEmbeddedService,
    val documents: MutableMap<Uuid, Document> = mutableMapOf(),
    override val rerankers: List<IReranker<Document>> = listOf()
) : IRetriever<InMemoryVectorRetriever.Document> {
    @Serializable
    data class Document(
        override val id: Uuid, override val document: String, val title: String, val vector: FloatArray
    ) : IDocument {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Document) return false

            if (id != other.id) return false
            if (document != other.document) return false
            if (title != other.title) return false
            if (!vector.contentEquals(other.vector)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = id.hashCode()
            result = 31 * result + document.hashCode()
            result = 31 * result + title.hashCode()
            result = 31 * result + vector.contentHashCode()
            return result
        }
    }

    class InMemoryVectorRetrieverBuilder(
        var model: String? = null,
        var embeddedService: IEmbeddedService? = null,
        var adapter: IAdapter? = null,
        private val documents: MutableMap<Uuid, Document> = mutableMapOf(),
        private val rerankers: MutableList<IReranker<Document>> = mutableListOf()
    ) {
        fun setEmbedModel(embedModel: String) = apply { this.model = embedModel }

        fun setEmbeddedService(embeddedService: IEmbeddedService) = apply { this.embeddedService = embeddedService }

        fun setEmbeddedService(adapter: IAdapter) = apply { this.adapter = adapter }

        fun addReranker(reranker: IReranker<Document>) = apply { rerankers.add(reranker) }

        fun addRerankers(rerankers: List<IReranker<Document>>) = apply { rerankers.forEach { addReranker(it) } }

        fun addRerankers(vararg rerankers: IReranker<Document>) = apply { rerankers.forEach { addReranker(it) } }

        fun addDocument(document: Document) = apply { documents[document.id] = document }

        fun addDocuments(documents: List<Document>) = apply { documents.forEach { addDocument(it) } }

        fun addDocuments(vararg documents: Document) = apply { documents.forEach { addDocument(it) } }

        fun build() = InMemoryVectorRetriever(
            model!!,
            embeddedService ?: adapter?.getEmbeddedService() ?: throw Exception("EmbeddedService not set"),
            documents,
            rerankers
        )
    }

    companion object {
        operator fun invoke(model: String, embeddedService: IEmbeddedService, jsonString: String, json: Json = Json) =
            InMemoryVectorRetriever(
                model,
                embeddedService,
                json.decodeFromString<List<Document>>(jsonString).associateBy { it.id }
                    .toMutableMap())

        operator fun invoke(model: String, adapter: IAdapter, jsonString: String, json: Json = Json) =
            InMemoryVectorRetriever(model, adapter.getEmbeddedService(), jsonString, json)

        operator fun invoke(model: String, embeddedService: IEmbeddedService, documents: List<Document>) =
            InMemoryVectorRetriever(model, embeddedService, documents.associateBy { it.id }.toMutableMap())

        operator fun invoke(model: String, adapter: IAdapter, documents: List<Document>) =
            InMemoryVectorRetriever(model, adapter.getEmbeddedService(), documents)

        operator fun invoke(builder: InMemoryVectorRetrieverBuilder.() -> Unit) =
            InMemoryVectorRetrieverBuilder().apply(builder).build()
    }

    private fun cosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
        require(v1.size == v2.size) { "Vectors must have the same length" }
        var dotProduct = 0f
        var norm1 = 0f
        var norm2 = 0f
        for (i in v1.indices) {
            dotProduct += v1[i] * v2[i]
            norm1 += v1[i].pow(2)
            norm2 += v2[i].pow(2)
        }
        return dotProduct / (sqrt(norm1) * sqrt(norm2))
    }

    override suspend fun query(string: String): List<Uuid> {
        val vector = embeddedService.embed {
            model = embedModel
            input = string
        }.embedding
        return documents.map { (id, document) -> id to cosineSimilarity(vector, document.vector) }
            .sortedByDescending { (_, similarity) -> similarity }.map { (id, _) -> id }
    }

    override fun getDocuments(uuids: List<Uuid>): List<Document> {
        return uuids.mapNotNull { documents[it] }
    }
}