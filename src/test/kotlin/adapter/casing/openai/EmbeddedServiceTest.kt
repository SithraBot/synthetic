package adapter.casing.openai

import adapter.IEmbeddedService
import adapter.casing.HybridTest
import kotlinx.coroutines.runBlocking
import kotlin.test.Test


internal object EmbeddedServiceTest {
    val testEmbeddedService = HybridTest.testHybrid.getEmbeddedService()

    @Test
    fun embed() {
        val response = runBlocking {
            testEmbeddedService.embed("text-embedding-v3", "Where does ‘Hello World’ come from?")
        }
        assert(response.embedding.isNotEmpty())
    }
}