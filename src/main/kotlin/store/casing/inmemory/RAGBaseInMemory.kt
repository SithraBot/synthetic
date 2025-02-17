package store.casing.inmemory

import store.IRAGBase
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.uuid.Uuid

class RAGBaseInMemory : IRAGBase {
    val titles = mutableMapOf<Uuid, String>()
    val documents = mutableMapOf<Uuid, String>()
    val vectors = mutableMapOf<Uuid, FloatArray>()
    fun cosineSimilarity(v1: FloatArray, v2: FloatArray): Float {
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
            val vector = vectors[id] ?: FloatArray(queryVector.size) { 0f }
            val similarity = cosineSimilarity(queryVector, vector)
            id to similarity
        }.sortedByDescending { (_, similarity) -> similarity }.take(limit).map { (id, _) -> id }
    }

    override fun getDocument(id: Uuid): String {
        return documents[id] ?: "Not found"
    }

    override fun addDocument(id: Uuid, document: String, title: String, vector: FloatArray) {
        documents[id] = document
        titles[id] = title
        vectors[id] = vector
    }

    override fun getDocumentTitle(id: Uuid): String {
        return titles[id] ?: "Not found"
    }
}