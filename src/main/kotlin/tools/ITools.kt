package tools

import kotlinx.serialization.json.*
import store.ToolResultMessage
import tools.toolcall.IToolCall

interface ITools<T : IToolCall> {
    fun call(input: List<T>): List<ToolResultMessage>

    fun getJsonObject(): JsonElement
}