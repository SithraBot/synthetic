package org.sithra.synthetic.tools

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import org.sithra.synthetic.tools.schema.Description
import org.sithra.synthetic.tools.schema.SchemaGenerator
import org.sithra.synthetic.tools.schema.schemaOf
import kotlin.reflect.KFunction
import kotlin.reflect.full.findAnnotation

/**
 * Functions is a collection of functions that can be used to handle tool call requests.
 *
 * @property functions The functions to call.
 */
open class Functions(
    private val functions: MutableMap<String, TFunction<*>> = mutableMapOf()
) : MutableMap<String, Functions.TFunction<*>> by functions {
    val json = Json

    /**
     * TFunction is a function that can be used to handle tool call requests.
     *
     * @property function The function to call.
     * @property name The name of the function.
     * @property deserializationStrategy The deserialization strategy to use to deserialize the input.
     * @property schema The schema of the input.
     * @property description The description of the function.
     * @property json The json object to use to deserialize the input.
     */
    data class TFunction<T>(
        private val function: (input: T) -> String,
        private val name: String,
        private val deserializationStrategy: DeserializationStrategy<T>,
        val schema: JsonObject,
        val description: String? = null,
        val json: Json = Json
    ) {
        operator fun invoke(inputJson: String): String =
            function(json.decodeFromString(deserializationStrategy, inputJson))

        companion object {
            inline operator fun <reified T : Any> invoke(
                name: String,
                noinline f: (T) -> String,
                cache: Functions,
                description: String?
            ): TFunction<T> {
                val serializer: KSerializer<T> = serializer()
                val schema = SchemaGenerator.default.schemaOf(T::class)
                return TFunction(f, name, serializer, schema, description, cache.json)
            }
        }
    }

    /**
     * register a tool function.
     *
     * @param f The function to register.
     */
    inline fun <reified T> register(noinline f: (T) -> String) {
        val function = f as KFunction<*>
        val description = function.findAnnotation<Description>()
        register(function.name, description?.value, f)
    }

    /**
     * register a tool function.
     *
     * @param name The name of the function.
     * @param description The description of the function.
     * @param f The function to register.
     */
    inline fun <reified T> register(name: String, description: String?, noinline f: (T) -> String) {
        put(name, TFunction(name, f, this, description))
    }

    /**
     * call a tool function.
     *
     * @param name The name of the function.
     * @param inputJson The input to the function.
     * @return The output of the function.
     */
    fun call(name: String, inputJson: String): String {
        val function = functions[name] ?: return "Function $name not found"
        return function(inputJson)
    }

}