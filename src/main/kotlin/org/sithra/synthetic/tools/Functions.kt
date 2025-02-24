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

open class Functions(
    val functions: MutableMap<String, TFunction<*>> = mutableMapOf()
) : MutableMap<String, Functions.TFunction<*>> by functions {
    val json = Json

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

    inline fun <reified T> register(noinline f: (T) -> String) {
        val function = f as KFunction<*>
        val description = function.findAnnotation<Description>()
        register(function.name, description?.value, f)
    }

    inline fun <reified T> register(name: String, description: String?, noinline f: (T) -> String) {
        put(name, TFunction(name, f, this, description))
    }

    fun call(name: String, inputJson: String): String {
        val function = functions[name] ?: return "Function $name not found"
        return function(inputJson)
    }

}