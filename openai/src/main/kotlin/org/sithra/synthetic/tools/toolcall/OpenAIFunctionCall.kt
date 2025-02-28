package org.sithra.synthetic.tools.toolcall

import kotlinx.serialization.Serializable

/**
 * OpenAIFunctionCall is a tool call request for the OpenAI API.
 *
 * @property id The id of the tool call.
 * @property function The function to call.
 */
@Serializable
class OpenAIFunctionCall(override val id: String, val function: Request) : IFunctionCall {

    /**
     * Request is a request for a function.
     *
     * @property name The name of the function.
     * @property arguments The arguments of the function, as a JSON string.
     */
    @Serializable
    data class Request(val name: String, val arguments: String)

    /**
     * Type is the type of the tool call.
     */
    @Serializable
    @Suppress("unused")
    val type = "function"

    override val arguments: String
        get() = function.arguments

    override val name: String
        get() = function.name
}