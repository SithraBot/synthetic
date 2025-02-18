package context

import adapter.casing.HybridTest
import kotlinx.coroutines.runBlocking
import store.casing.inmemory.InMemoryRAGBaseTest
import store.casing.inmemory.InMemoryMessagesStoreTest
import kotlin.test.Test

object ContextManagerTest {
    private val testContextManager = ContextManager {
        apiAdapter = HybridTest.testHybrid
        messagesStore = InMemoryMessagesStoreTest.testMessagesStore
        ragBase = InMemoryRAGBaseTest.testRagBase
    }

    @Test
    fun testChat() = runBlocking {
        val session = testContextManager.createSession("deepseek-chat", "text-embedding-v3")
        testContextManager.withSession(session) {
            val message = chat(Message("hello"))
            println(message.content)
            addMessage(message)
            val message2 = chat(Message("who are you"))
            println(message2.content)
            addMessage(message2)
        }
    }
}