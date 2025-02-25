package org.sithra.synthetic.store

import kotlinx.serialization.Serializable
import org.sithra.synthetic.tools.toolcall.IToolCall

/**
 * Tool message, request from the model.
 *
 * @param T Type of tool call
 * @property toolCalls List of tool calls
 *
 * @see IMessage
 * @see ToolResultMessage
 */
@Serializable
class ToolMessage<T : IToolCall>(
    val toolCalls: List<T>
) : IMessage