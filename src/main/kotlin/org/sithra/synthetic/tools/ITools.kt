package org.sithra.synthetic.tools

import kotlinx.serialization.json.*
import org.sithra.synthetic.store.ToolResultMessage
import org.sithra.synthetic.tools.toolcall.IToolCall

/**
 * ITools is an interface representing a collection of tools that can be used to handle chat requests.
 *
 * Is used to process tool call requests.
 *
 * @param T Type of tool call request.
 */
interface ITools<T : IToolCall> {
    /**
     * Calls the tool with the given input.
     *
     * @param input The input to the tool.
     * @return The result of the tool call.
     */
    fun call(input: List<T>): List<ToolResultMessage>

    /**
     * Gets the JSON Schema for the tools.
     *
     * @return The JSON Schema for the tools.
     */
    fun getJsonObject(): JsonElement
}