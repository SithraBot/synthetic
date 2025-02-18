package store

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

interface IRAGBase {
    @Serializable
    data class Document(
        val id: Uuid, val document: String, val title: String, val vector: FloatArray
    ) {
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

    fun search(queryVector: FloatArray, limit: Int): List<Uuid>
    fun getDocument(id: Uuid): Document?
    fun addDocument(id: Uuid, document: Document)
}