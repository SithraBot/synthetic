# Synthetis

Synthetis is a kotlin framework for building AI chat applications. Support RAG.

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
        chatStream(Message("why hello world?")) { content, done ->
            var message = ""
            if (done) {
                addMessage(Message(message, Message.Role.ASSISTANT))
                println()
            } else {
                message += content
                print(content)
            }
        }
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
        addMessage(message4)
    }
}
```