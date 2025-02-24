package org.sithra.synthetic.store

import kotlinx.serialization.Serializable
import org.sithra.synthetic.tools.toolcall.IToolCall

@Serializable
class ToolMessage<T : IToolCall>(
    val toolCalls: List<T>
) : IMessage