package tools

import context.IContext.ContextWithSessionAndTools
import store.Message
import store.ToolMessage
import tools.toolcall.IToolCall

suspend inline fun <reified T : IToolCall> ContextWithSessionAndTools<T>.chatWithTools(message: Message): Message {
    var response = session.chatWithTools(message, tools)
    while (response !is Message) {
        when (response) {
            is ToolMessage<*> -> {
                addMessage(response)
                val toolCalls = tools.call(response.toolCalls.filterIsInstance<T>())
                response = session.chatWithTools(toolCalls, tools)
            }
            else -> throw Exception("Unexpected message type: $response")
        }
    }
    return response
}