package store.casing.inmemory

import adapter.casing.openai.EmbeddedServiceTest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import kotlin.uuid.Uuid

object InMemoryRAGBaseTest {
    val testRagBase by lazy {
        InMemoryRAGBase(
            mutableMapOf(
                runBlocking {
                    val id = Uuid.random()
                    val title = "Where does ‘Hello World’ come from?"
                    id to InMemoryRAGBase.Document(
                        id,
                        "Brian Kernighan, author of one of the most widely read programming books, \"C Programming Language\", also created \"Hello, World\". He first referenced ‘Hello World’ in the C Programming Language book’s predecessor: A Tutorial Introduction to the Programming Language B published in 1973.",
                        title,
                        EmbeddedServiceTest.testEmbeddedService.embed {
                            model = "text-embedding-v3"
                            input = title
                        }.embedding
                    )
                },
                runBlocking {
                    val id = Uuid.random()
                    val title = "Kozeki Ui's personal profile."
                    id to InMemoryRAGBase.Document(
                        id,
                        "Kozeki Ui (古関ウイ) is one of the students studying in Trinity General School who wields a Sniper Rifle. She is a head of the Library Committee.",
                        title,
                        EmbeddedServiceTest.testEmbeddedService.embed {
                            model = "text-embedding-v3"
                            input = title
                        }.embedding
                    )
                }
            )
        )
    }
}