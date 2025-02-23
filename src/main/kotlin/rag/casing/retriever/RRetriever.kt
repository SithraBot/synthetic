package rag.casing.retriever

import rag.IDocument
import rag.IReranker
import rag.IRetriever
import kotlin.uuid.Uuid

class RRetriever<Doc : IDocument>(val retriever: IRetriever<Doc>, val rRerankers: List<IReranker<Doc>>) :
    IRetriever<Doc> {
    override val rerankers: List<IReranker<Doc>>
        get() = retriever.rerankers + rRerankers

    override suspend fun query(string: String): List<Uuid> = retriever.query(string)

    override fun getDocuments(uuids: List<Uuid>): List<Doc> = retriever.getDocuments(uuids)


    companion object {
        operator fun <Doc : IDocument> invoke(retriever: IRetriever<Doc>, rReranker: IReranker<Doc>) =
            RRetriever(retriever, listOf(rReranker))
    }
}