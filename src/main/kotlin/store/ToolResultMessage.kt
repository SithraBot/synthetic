package store

import kotlinx.serialization.Serializable

@Serializable
class ToolResultMessage(
    val content: String,
    val role: IMessage.Role = IMessage.Role.TOOL,
    val toolCallId: String
) : IMessage