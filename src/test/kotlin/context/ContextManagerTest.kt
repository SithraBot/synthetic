package context

import adapter.casing.HybridTest
import kotlinx.coroutines.runBlocking
import store.casing.inmemory.MessagesStoreInMemoryTest
import kotlin.test.Test

object ContextManagerTest {
    private val testContextManager = ContextManager {
        setApiAdapter(HybridTest.testHybrid)
        setMessagesStore(MessagesStoreInMemoryTest.testMessagesStore)
    }

    @Test
    fun chat() = runBlocking {
        val session = testContextManager.createSession("deepseek-chat", "text-embedding-v3")
        testContextManager.withSessionId(session) {
            val message = chat(Message("hello"))
            println(message.content)
            addMessage(message)
            val message2 = chat(Message("who are you"))
            println(message2.content)
            addMessage(message2)
        }
        println(testContextManager.getSessions())
        println(testContextManager.getSessionById(session)?.messages)
    }
}