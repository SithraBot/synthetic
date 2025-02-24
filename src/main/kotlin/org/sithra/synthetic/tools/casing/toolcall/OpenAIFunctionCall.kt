package org.sithra.synthetic.tools.casing.toolcall

import org.sithra.synthetic.tools.toolcall.IFunctionCall
import kotlinx.serialization.Serializable

@Serializable
class OpenAIFunctionCall(override val id: String, val function: Request) : IFunctionCall {
    @Serializable
    data class Request(val name: String, val arguments: String)

    val type = "function"

    override val arguments: String
        get() = function.arguments

    override val name: String
        get() = function.name
}