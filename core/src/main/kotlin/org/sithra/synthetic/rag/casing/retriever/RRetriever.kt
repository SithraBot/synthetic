package org.sithra.synthetic.rag.casing.retriever

import org.sithra.synthetic.rag.IDocument
import org.sithra.synthetic.rag.IReranker
import org.sithra.synthetic.rag.IRetriever
import kotlin.uuid.Uuid

/**
 * RRetriever is a retriever that uses a reranker to rank the documents.
 *
 * @property Doc The type of document
 *
 * @property retriever The retriever to use.
 * @property rRerankers The rerankers to use.
 *
 * @see IRetriever
 * @see IReranker
 * @see IDocument
 */
class RRetriever<Doc : IDocument>(private val retriever: IRetriever<Doc>, private val rRerankers: List<IReranker<Doc>>) :
    IRetriever<Doc> {
    override val rerankers: List<IReranker<Doc>>
        get() = retriever.rerankers + rRerankers

    override suspend fun query(string: String): List<Uuid> = retriever.query(string)

    override fun getDocuments(uuids: List<Uuid>): List<Doc> = retriever.getDocuments(uuids)


    companion object {
        /**
         * Create a RRetriever.
         *
         * @param retriever The retriever to use.
         * @param rReranker The reranker to use.
         *
         * @return A RRetriever.
         */
        operator fun <Doc : IDocument> invoke(retriever: IRetriever<Doc>, rReranker: IReranker<Doc>) =
            RRetriever(retriever, listOf(rReranker))
    }
}