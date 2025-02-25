package org.sithra.synthetic.rag

import kotlin.uuid.Uuid

/**
 * IReranker is an interface representing a reranker for RAGs.
 *
 * @param Doc The type of document
 *
 * @see IDocument
 */
interface IReranker<Doc : IDocument> {
    /**
     * Query the reranker.
     *
     * @param docs The documents to query.
     * @param query The query to use.
     *
     * @return Reranked documents id.
     */
    suspend fun query(docs: List<Doc>, query: String): List<Uuid>

    /**
     * Invoke the reranker.
     *
     * @param docs The documents to query.
     * @param query The query to use.
     *
     * @return Reranked documents.
     */
    suspend operator fun invoke(docs: List<Doc>, query: String): List<Uuid> {
        return query(docs, query)
    }
}