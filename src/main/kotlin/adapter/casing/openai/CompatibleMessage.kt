package adapter.casing.openai

import store.IMessage
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import store.Message
import store.ToolMessage
import store.ToolResultMessage
import tools.casing.toolcall.OpenAIFunctionCall

@Serializable
data class CompatibleMessage(
    val role: IMessage.Role,
    val content: String,
    @SerialName("tool_calls")
    val toolCalls: List<OpenAIFunctionCall>?,
    @SerialName("tool_call_id")
    val toolCallId: String?
) {
    fun toMessage(): IMessage = when {
        this.toolCalls != null -> ToolMessage(toolCalls)
        this.toolCallId != null -> ToolResultMessage(content, role, toolCallId)
        else -> Message(content, role)
    }

    companion object {
        fun fromMessage(message: IMessage): CompatibleMessage = when (message) {
            is ToolMessage<*> -> CompatibleMessage(
                IMessage.Role.ASSISTANT,
                "",
                message.toolCalls.filterIsInstance<OpenAIFunctionCall>(),
                null
            )

            is ToolResultMessage -> CompatibleMessage(IMessage.Role.TOOL, message.content, null, message.toolCallId)
            is Message -> CompatibleMessage(message.role, message.content, null, null)
        }
    }
}