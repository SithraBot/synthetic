package adapter.casing.openai

import Props
import io.ktor.http.*
import org.sithra.synthetic.adapter.casing.openai.ChatService
import org.sithra.synthetic.adapter.casing.openai.EmbeddedService
import org.sithra.synthetic.adapter.casing.openai.OpenAIAdapter
import kotlin.test.Test

internal object OpenAIAdapterTest {
    val testAIAdapter = OpenAIAdapter {
        val timeout = 180_000L
        baseUrl = Url("https://dashscope.aliyuncs.com/compatible-mode/v1")
        token = Props.getAliyunKey()
        requestTimeout = timeout
        connectTimeout = timeout
        socketTimeout = timeout
    }
}