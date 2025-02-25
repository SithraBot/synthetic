package org.sithra.synthetic.store

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * IMessage is an interface representing a message in a chat.
 */
sealed interface IMessage {
    /**
     * The role of the message sender.
     */
    @Serializable
    enum class Role {
        @SerialName("user")
        USER,

        @SerialName("system")
        SYSTEM,

        @SerialName("assistant")
        ASSISTANT,

        @SerialName("tool")
        TOOL
    }
}