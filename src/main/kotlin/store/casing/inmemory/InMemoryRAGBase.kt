package store.casing.inmemory

import kotlinx.serialization.Serializable
import store.IRAGBase
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.uuid.Uuid
import kotlinx.serialization.json.Json
import store.IDocument

class InMemoryRAGBase(
    private val documents: MutableMap<Uuid, Document> = mutableMapOf()
) : IRAGBase<InMemoryRAGBase.Document> {
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

    companion object {
        operator fun invoke(jsonString: String, json: Json = Json) =
            InMemoryRAGBase(json.decodeFromString<List<Document>>(jsonString).associateBy { it.id }
                .toMutableMap())
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

    override fun search(queryVector: FloatArray, limit: Int): List<Uuid> {
        return documents.map { (id, document) ->
            val similarity = cosineSimilarity(queryVector, document.vector)
            id to similarity
        }.sortedByDescending { (_, similarity) -> similarity }.take(limit).map { (id, _) -> id }
    }

    override fun getDocument(id: Uuid): Document? {
        return documents[id]
    }

    override fun addDocument(id: Uuid, document: Document) {
        documents[id] = document
    }
}