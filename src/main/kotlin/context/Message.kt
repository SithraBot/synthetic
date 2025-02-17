package context

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class Message(
    val content: String,
    val role: Role = Role.USER,
) {
    @Serializable
    enum class Role {
        @SerialName("user")
        USER,

        @SerialName("system")
        SYSTEM,

        @SerialName("assistant")
        ASSISTANT
    }
}