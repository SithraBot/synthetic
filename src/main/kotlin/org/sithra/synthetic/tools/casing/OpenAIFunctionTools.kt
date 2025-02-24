package org.sithra.synthetic.tools.casing

import kotlinx.serialization.json.*
import org.sithra.synthetic.store.ToolResultMessage
import org.sithra.synthetic.tools.Functions
import org.sithra.synthetic.tools.ITools
import org.sithra.synthetic.tools.casing.toolcall.OpenAIFunctionCall

class OpenAIFunctionTools(val functions: Functions) : ITools<OpenAIFunctionCall> {
    override fun getJsonObject(): JsonElement = buildJsonArray {
        for ((name, function) in functions) {
            addJsonObject {
                put("type", "function")
                putJsonObject("function") {
                    put("name", name)
                    put("description", function.description)
                    put("parameters", function.schema)
                }
            }
        }
    }

    override fun call(input: List<OpenAIFunctionCall>): List<ToolResultMessage> {
        return input.map {
            ToolResultMessage(
                functions[it.name]?.invoke(it.arguments) ?: "Function ${it.name} not found",
                toolCallId = it.id
            )
        }
    }
}