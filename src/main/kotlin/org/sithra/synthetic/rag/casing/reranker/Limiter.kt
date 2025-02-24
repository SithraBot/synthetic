package org.sithra.synthetic.rag.casing.reranker

import org.sithra.synthetic.rag.IDocument
import org.sithra.synthetic.rag.IReranker
import kotlin.uuid.Uuid

class Limiter<Doc : IDocument>(private val topN: Int) : IReranker<Doc> {
    override suspend fun query(docs: List<Doc>, query: String): List<Uuid> {
        return docs.take(topN).map { it.id }
    }
}