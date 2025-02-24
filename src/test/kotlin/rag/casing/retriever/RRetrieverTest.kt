package rag.casing.retriever

import kotlinx.coroutines.runBlocking
import org.sithra.synthetic.rag.IDocument
import org.sithra.synthetic.rag.IReranker
import org.sithra.synthetic.rag.IRetriever
import org.sithra.synthetic.rag.casing.retriever.RRetriever
import org.sithra.synthetic.rag.casing.reranker.Limiter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

object RRetrieverTest {
    data class Doc(override val document: String = "", override val id: Uuid = Uuid.random()) : IDocument

    val testRetriever = object : IRetriever<Doc> {
        val docs = listOf(Doc(), Doc(), Doc(), Doc())

        override val rerankers: List<IReranker<Doc>> = listOf()

        override fun getDocuments(uuids: List<Uuid>): List<Doc> {
            return docs
        }

        override suspend fun query(string: String): List<Uuid> {
            return docs.map { it.id }
        }

    }

    @Test
    fun testRetriever() = runBlocking {
        val result = testRetriever.search("")
        assertEquals(result.size, 4)
    }

    @Test
    fun testRetrieverWithLimit() = runBlocking {
        val retriever = RRetriever(testRetriever, Limiter(2))
        assertEquals(
            1,
            retriever.rerankers.size
        )
        val result = retriever.search("")
        assertEquals(2, result.size)
    }
}