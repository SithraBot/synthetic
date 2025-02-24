package adapter.casing

import Props
import org.sithra.synthetic.adapter.casing.openai.ChatService
import org.sithra.synthetic.adapter.casing.openai.EmbeddedService
import org.sithra.synthetic.adapter.casing.openai.OpenAIAdapter
import adapter.casing.openai.OpenAIAdapterTest
import io.ktor.http.*
import org.sithra.synthetic.adapter.casing.Hybrid
import kotlin.test.Test

object HybridTest {
    private val testOtherService = OpenAIAdapter(
        OpenAIAdapter.Config(
            Url("https://dashscope.aliyuncs.com/compatible-mode/v1"),
            Props.getAliyunKey()
        )
    )
    val testHybrid = Hybrid(OpenAIAdapterTest.testAIAdapter, testOtherService)

    @Test
    fun getChatService() {
        val chatService = testHybrid.getChatService()
        assert(chatService is ChatService)
    }

    @Test
    fun getEmbeddedService() {
        val embeddedService = testHybrid.getEmbeddedService()
        assert(embeddedService is EmbeddedService)
    }
}