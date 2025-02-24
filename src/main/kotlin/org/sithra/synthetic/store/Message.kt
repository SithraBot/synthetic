package org.sithra.synthetic.store

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val content: String,
    val role: IMessage.Role = IMessage.Role.USER,
) : IMessage