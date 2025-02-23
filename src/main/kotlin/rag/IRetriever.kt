package rag

import kotlin.uuid.Uuid

interface IRetriever<Doc : IDocument> {
    val rerankers: List<IReranker<Doc>>
    fun getDocuments(uuids: List<Uuid>): List<Doc>
    suspend fun query(string: String): List<Uuid>
    suspend fun search(string: String): List<Uuid> {
        var result = query(string)
        for (reranker in rerankers) {
            result = reranker(getDocuments(result), string)
        }
        return result
    }

    suspend fun searchDocs(string: String): List<Doc> = getDocuments(search(string))
}