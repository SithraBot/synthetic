package tools.casing

import kotlinx.serialization.json.*
import store.ToolResultMessage
import tools.Functions
import tools.ITools
import tools.casing.toolcall.OpenAIFunctionCall

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