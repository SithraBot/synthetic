package org.sithra.synthetic.tools

import kotlinx.serialization.json.*
import org.sithra.synthetic.tools.toolcall.OpenAIFunctionCall
import org.sithra.synthetic.store.ToolResultMessage

/**
 * OpenAIFunctionTools is for processing tool call requests from the OpenAI API.
 *
 * @property functions The functions to call.
 *
 * @see ITools
 */
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