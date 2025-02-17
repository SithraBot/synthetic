# Synthetis

Synthetis is a kotlin framework for building AI chat applications. Support RAG.

## Usage

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
    contextManager.withSessionId(session) {
        val message = chat(Message("why hello world?"))
        println(message.content)
        addMessage(message)
    }
    
    // streaming
    contextManager.withSessionId(session) {
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