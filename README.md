<h1 align="center">Synthetis <br/> <img src="https://github.com/SithraBot/synthetis/blob/dev/icon.svg" alt="Synthetis" width="30%"/></h1>

**Synthetis** is a lightweight Kotlin library for building LLM-driven applications, specializing in RAG (
Retrieval-Augmented Generation) pipelines. Designed for API flexibility, it implements an **adapter-first architecture**
to seamlessly integrate any AI provider (OpenAI, HuggingFace, custom endpoints) and data storage system.

Core features:

- Modular RAG components: Document processors, embedding interfaces, and retriever/reranker blueprints
- Agnostic data layer: Abstracted connectors for vector DBs, SQL/NoSQL, or file systems via unified interfaces
- API-neutral LLM operations: Swap models/endpoints without rewriting chains/agents
- Coroutine-native pipelines for async workflows
- Minimal abstraction overhead with clear extension points

Optimized for Kotlin developers prioritizing API interoperability and custom data flows over framework rigidity.

## Usage

### Common

```kotlin
private val apiKey = "..."
private val url = Url("...")
fun main() = runBlocking {
    val adapter = OpenAIAdapter {
        baseUrl = url
        token = apiKey
    }
    val contextManager = ContextManager {
        apiAdapter = adapter
        messagesStore = MessagesStoreInMemory()
    }
    val session = contextManager.createSession("...", "...")
    // common
    contextManager.withSession(session) {
        val message = chat(Message("why hello world?"))
        println(message.content)
        addMessage(message)
    }

    // streaming
    contextManager.withSession(session) {
        val response = chatStream(Message("why hello world?"))
        var message = ""
        response.collect {
            print(it)
            message += it
        }
        println()
        addMessage(Message(message, Message.Role.ASSISTANT))
    }
}
```

### RAG

```kotlin
// ... ...
testContextManager.withSession(session) {
    withRAGBase(testRAGBase) {
        suspend fun ask(question: String): Message =
            chat(Message(ragTemplate(question, search(question, 1))))

        val message1 = ask("why do we use Hello World?")
        println(message1.content)
        addMessage(message1)
        val message2 = ask("古关优是谁？")
        println(message2.content)
        addMessage(message2)
    }
}

fun ragTemplate(question: String, docs: List<IDocument>): String {
    val docsString = docs.joinToString { "${it.document};;" }
    return """
            Please answer the question according to the docs.
            question: $question
            docs: $docsString
            answer:
        """.trimIndent()
}
```