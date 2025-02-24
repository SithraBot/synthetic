package org.sithra.synthetic.tools

import kotlinx.serialization.json.*
import org.sithra.synthetic.store.ToolResultMessage
import org.sithra.synthetic.tools.toolcall.IToolCall

interface ITools<T : IToolCall> {
    fun call(input: List<T>): List<ToolResultMessage>

    fun getJsonObject(): JsonElement
}