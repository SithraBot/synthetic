package org.sithra.synthetic.store

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed interface IMessage {
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