package rag.casing.reranker

import rag.IDocument
import rag.IReranker
import kotlin.uuid.Uuid

class Limiter<Doc : IDocument>(private val topN: Int) : IReranker<Doc> {
    override suspend fun query(docs: List<Doc>, query: String): List<Uuid> {
        return docs.take(topN).map { it.id }
    }
}