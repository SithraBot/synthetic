package rag.casing.retriever

import Props
import adapter.casing.openai.OpenAIAdapterTest
import kotlinx.serialization.json.Json
import org.sithra.synthetic.rag.casing.retriever.InMemoryVectorRetriever

object InMemoryVectorRetrieverTest {
    val testRagBase by lazy {
        InMemoryVectorRetriever {
            model = "text-embedding-v3"
            adapter = OpenAIAdapterTest.testAIAdapter
            addDocuments(
                Json.decodeFromString<List<InMemoryVectorRetriever.Document>>(Props.documents)
            )
        }
    }
}