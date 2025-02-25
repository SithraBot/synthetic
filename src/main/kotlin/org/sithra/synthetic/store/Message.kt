package org.sithra.synthetic.store

import kotlinx.serialization.Serializable

/**
 * Common message data.
 *
 * @property content The content of the message.
 * @property role The role of the message sender.
 *
 * @see IMessage
 */
@Serializable
data class Message(
    val content: String,
    val role: IMessage.Role = IMessage.Role.USER,
) : IMessage