package context

import adapter.casing.HybridTest
import kotlinx.coroutines.runBlocking
import store.casing.inmemory.InMemoryRAGBaseTest
import store.casing.inmemory.InMemoryMessagesStoreTest
import kotlin.test.Test
import context.*;
import store.IDocument

object ContextManagerTest {
    val testRAGBase = InMemoryRAGBaseTest.testRagBase

    private val testContextManager = ContextManager {
        apiAdapter = HybridTest.testHybrid
        messagesStore = InMemoryMessagesStoreTest.testMessagesStore
    }

    @Test
    fun testChat() = runBlocking {
        val session = testContextManager.createSession("qwen-turbo", "text-embedding-v3")
        testContextManager.withSession(session) {
            val message = chat(Message("hello"))
            println(message.content)
            addMessage(message)
            val message2 = chat(Message("who are you"))
            println(message2.content)
            addMessage(message2)
            withRAGBase(testRAGBase) {
                suspend fun ask(question: String): Message =
                    chat(Message(dbg(ragTemplate(question, search(question, 1)))))

                val message3 = ask("why do we use Hello World?")
                println(message3.content)
                addMessage(message3)
                val message4 = ask("古关优是谁？")
                println(message4.content)
                addMessage(message4)
            }
        }
    }

    private fun <T> dbg(message: T): T = message

    private fun ragTemplate(question: String, docs: List<IDocument>): String {
        val docsString = docs.joinToString { "${it.document};;" }
        return """
            Please answer the question according to the docs.
            question: $question
            docs: $docsString
            answer:
        """.trimIndent()
    }
}