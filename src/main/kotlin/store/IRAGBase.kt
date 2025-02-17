package store

import kotlin.uuid.Uuid

interface IRAGBase {
    fun search(queryVector: FloatArray, limit: Int): List<Uuid>
    fun getDocumentTitle(id: Uuid): String
    fun getDocument(id: Uuid): String
    fun addDocument(id: Uuid, document: String, title: String, vector: FloatArray)
}