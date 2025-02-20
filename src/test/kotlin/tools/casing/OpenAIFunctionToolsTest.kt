package tools.casing

import tools.Functions
import kotlinx.serialization.Serializable
import tools.schema.Description

object OpenAIFunctionToolsTest {
    val testOpenAIFunctionTools = OpenAIFunctionTools(Functions())

    init {
        testOpenAIFunctionTools.functions.register(this::add)
    }

    @Serializable
    data class AddInput(@Description("first number") val a: Int, @Description("second number") val b: Int)

    @Description("add two numbers")
    fun add(input: AddInput): String {
        println("\n-- add ${input.a} and ${input.b} returning ${input.a + input.b} -- \n")
        return (input.a + input.b).toString()
    }
}