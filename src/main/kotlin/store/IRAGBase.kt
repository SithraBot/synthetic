package store

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

interface IRAGBase<Doc : IDocument> {
    fun search(queryVector: FloatArray, limit: Int): List<Uuid>
    fun getDocument(id: Uuid): Doc?
    fun addDocument(id: Uuid, document: Doc)
}