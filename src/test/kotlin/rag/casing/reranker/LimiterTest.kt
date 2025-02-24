package rag.casing.reranker

import kotlinx.coroutines.runBlocking
import org.sithra.synthetic.rag.IDocument
import org.sithra.synthetic.rag.casing.reranker.Limiter
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid


object LimiterTest {
    class Doc(override val id: Uuid = Uuid.random(), override val document: String = "") : IDocument

    val testLimiter = Limiter<Doc>(1)

    @Test
    fun limiterTest() = runBlocking {
        val docs = List(5) { Doc() }
        val result = testLimiter.query(docs, "")
        assertEquals(result.size, 1)
    }
}