package rag

import kotlin.uuid.Uuid

interface IRAGBase<Doc : IDocument> {
    val model: String
    fun search(queryVector: FloatArray, limit: Int): List<Uuid>
    fun getDocument(id: Uuid): Doc?
    fun addDocument(id: Uuid, document: Doc)
}