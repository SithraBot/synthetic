package rag

import kotlin.uuid.Uuid

interface IReranker<Doc : IDocument> {
    suspend fun query(docs: List<Doc>, query: String): List<Uuid>
    suspend operator fun invoke(docs: List<Doc>, query: String): List<Uuid> {
        return query(docs, query)
    }
}