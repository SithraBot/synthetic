package org.sithra.synthetic.rag

import kotlin.uuid.Uuid

/**
 * IRetriever is an interface representing a retriever for RAGs.
 *
 * @param Doc The type of document
 *
 * @property rerankers The rerankers to use.
 *
 * @see IDocument
 */
interface IRetriever<Doc : IDocument> {
    /**
     * The list of rerankers.
     */
    val rerankers: List<IReranker<Doc>>

    /**
     * Get the documents with the given uuids.
     *
     * @param uuids The uuids of the documents to get.
     *
     * @return The documents with the given uuids.
     */
    fun getDocuments(uuids: List<Uuid>): List<Doc>

    /**
     * Call the retriever.
     *
     * Tip: Its result does not undergo any reranking.
     *
     * @param string The query to use.
     *
     * @return The documents id that match the query.
     *
     * @see search with reranking
     */
    suspend fun query(string: String): List<Uuid>

    /**
     * Call the retriever and rerank the result.
     *
     * @param string The query to use.
     *
     * @return The documents id that match the query.
     */
    suspend fun search(string: String): List<Uuid> {
        var result = query(string)
        for (reranker in rerankers) {
            result = reranker(getDocuments(result), string)
        }
        return result
    }

    /**
     * Call the retriever and rerank the result.
     *
     * @param string The query to use.
     *
     * @return The documents that match the query.
     */
    suspend operator fun invoke(string: String): List<Doc> = getDocuments(search(string))
}