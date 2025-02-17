package adapter.casing.openai

import adapter.IEmbeddedService
import adapter.casing.HybridTest
import kotlinx.coroutines.runBlocking
import kotlin.test.Test


internal object EmbeddedServiceTest {
    private val testEmbeddedService = HybridTest.testHybrid.getEmbeddedService()

    @Test
    fun getEmbeddedService() {
        val response = runBlocking {
            testEmbeddedService.embed("text-embedding-v3", "hello")
        }
        assert(response.embedding.isNotEmpty())
    }
}