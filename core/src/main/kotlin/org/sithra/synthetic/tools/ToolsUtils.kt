package org.sithra.synthetic.tools

import org.sithra.synthetic.context.IContext.ContextWithSessionAndTools
import org.sithra.synthetic.store.IMessage
import org.sithra.synthetic.store.ToolMessage
import org.sithra.synthetic.tools.toolcall.IToolCall

/**
 * Handles a chat request with tools.
 *
 * @param message The message to handle.
 * @return The response from the chat service.
 */
suspend inline fun <reified T : IToolCall> ContextWithSessionAndTools<T>.chatWithTools(message: IMessage): IMessage {
    var response = session.chatWithTools(message, tools)
    while (response is ToolMessage<*>) {
        addMessage(response)
        val toolCalls = tools.call(response.toolCalls.filterIsInstance<T>())
        response = session.chatWithTools(toolCalls, tools)
    }
    return response
}