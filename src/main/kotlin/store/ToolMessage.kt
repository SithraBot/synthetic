package store

import kotlinx.serialization.Serializable
import tools.toolcall.IToolCall

@Serializable
class ToolMessage<T : IToolCall>(
    val toolCalls: List<T>
) : IMessage