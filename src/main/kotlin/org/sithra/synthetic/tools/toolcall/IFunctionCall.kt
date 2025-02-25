package org.sithra.synthetic.tools.toolcall

/**
 * IFunctionCall is an interface representing a function call request.
 *
 * @property name The name of the function.
 * @property arguments The arguments of the function, as a JSON string.
 *
 * @see IToolCall
 */
interface IFunctionCall : IToolCall {
    val name: String
    val arguments: String
}
