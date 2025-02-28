package org.sithra.synthetic.store

import kotlinx.serialization.Serializable

/**
 * The result of a tool call returned by the terminal.
 *
 * @property content The content of the message.
 * @property role The role of the message sender.
 * @property toolCallId The id of the tool call.
 *
 * @see IMessage
 * @see ToolMessage
 */
@Serializable
class ToolResultMessage(
    val content: String, val role: IMessage.Role = IMessage.Role.TOOL, val toolCallId: String
) : IMessage