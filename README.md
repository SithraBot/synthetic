<h1 align="center"><img src="https://github.com/SithraBot/synthetis/blob/dev/icon_text.svg" alt="Synthetis" height="64px"/></h1>

*Synthetis* is a lightweight Kotlin/JVM library for building LLM-driven applications, specializing in RAG (Retrieval-Augmented Generation) pipelines and agent systems. Designed for API flexibility, it implements an adapter-first architecture to seamlessly integrate any AI provider (OpenAI, HuggingFace, custom endpoints) and data storage system.

**Core features:**

- **Modular RAG components**: Document processors, embedding interfaces, and retriever/reranker blueprints
- **Flexible Agent framework**: Custom tool integration, stateful conversation management, and goal-oriented execution flows
- **Agnostic data layer**: Abstracted connectors for vector DBs, SQL/NoSQL, or file systems via unified interfaces
- **API-neutral LLM operations**: Swap models/endpoints without rewriting chains/agents
- **Coroutine-native pipelines**: Async workflows for both RAG and Agent operations
- **Minimal abstraction overhead**: Clear extension points for custom agent logic and data handlers

Optimized for Kotlin/JVM developers prioritizing API interoperability and custom data flows over framework rigidity.

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
    val ctx = ContextManager {
        apiAdapter = adapter
        messagesStore = MessagesStoreInMemory()
    }
    val session = ctx.createSession("...", "...")
    // common
    ctx.withSession(session) {
        val message = chat(Message("why hello world?"))
        println(message.content)
        addMessage(message)
    }

    // streaming
    ctx.withSession(session) {
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
ctx.withSession(session) {
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

### Tools

```kotlin
// ... ...
val tools = OpenAIFunctionTools(Functions())

@Serializable
data class AddInput(@Description("first number") val a: Int, @Description("second number") val b: Int)

@Description("add two numbers")
fun add(input: AddInput): String {
    return (input.a + input.b).toString()
}

tools.functions.register(this::add)


val session = testContextManager.createSession("qwen-turbo")
ctx.withSession(session) {
    withTools(OpenAIFunctionToolsTest.testOpenAIFunctionTools) {
        val message = chatWithTools(Message("134 + 983 等于几？"))
        println(message.content)
        addMessage(message)
    }
}
```

### Tools + RAG

```kotlin
// ... ...

val tools = OpenAIFunctionTools(Functions())
val session = testContextManager.createSession("qwen-turbo")
ctx.withSession(session) {
    withRAGBase(testRAGBase) {
        @Serializable
        class SearchInput(@Description("keywords") val keywords: String)

        tools.functions.register("searchDocuments", "search documents about any by keywords") { input: SearchInput ->
            runBlocking {
                search(input.keywords, 1).joinToString(";;") { it.document }
            }
        }

        withTools(tools) {
            val message = chatWithTools(Message("古关优是谁？"))
            println(message.content)
            addMessage(message)
        }
    }
}
```