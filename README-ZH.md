<h1 align="center"><img src="https://github.com/SithraBot/synthetis/blob/dev/icon_text.svg" alt="Synthetis" height="64px"/></h1>

**Synthetis** 是一个轻量级 Kotlin/JVM 库，专为构建 LLM 驱动型应用设计，核心支持 RAG（检索增强生成）管道和智能体（Agent）系统。采用适配器优先架构，可无缝集成任意 AI 服务提供商（OpenAI、HuggingFace、自定义端点）和数据存储系统。

**核心特性：**

- **模块化 RAG 组件**：文档处理器、嵌入接口、检索器/重排器模板
- **灵活的 Agent 框架**：支持自定义工具集成、带状态的会话管理、目标导向的任务流
- **数据层无关性**：通过统一接口抽象化向量数据库/SQL/NoSQL/文件系统的连接
- **API 无关的 LLM 操作**：自由切换模型/服务端点而无需重构业务链或 Agent
- **协程优先的管道**：原生支持 RAG 和 Agent 的异步工作流
- **最小抽象开销**：提供清晰的扩展点用于定制 Agent 逻辑和数据处理器

专为重视 API 互操作性和自定义数据流，而非框架约束的 Kotlin/JVM 开发者优化。

## 例子

### 普通聊天 (连续对话)

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

### RAG (检索增强生成)

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

### Tools (函数调用)

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

### Tools + RAG (组合使用)

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
