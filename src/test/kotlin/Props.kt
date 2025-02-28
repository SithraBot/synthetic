import java.util.Properties

object Props {
    private val secrets: Properties by lazy {
        val props = this::class.java.getResourceAsStream("/secrets.properties")
        Properties().apply { load(props) }
    }

    object OpenAI {
        val apiKey = secrets.getProperty("openai.api-key")
        val baseUrl = secrets.getProperty("openai.base-url")
        val model = secrets.getProperty("openai.model")
        val embeddingModel = secrets.getProperty("openai.embedding-model")
    }
}