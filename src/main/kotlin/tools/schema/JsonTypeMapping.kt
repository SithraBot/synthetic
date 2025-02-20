package tools.schema

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.descriptors.StructureKind

// source: https://github.com/Stream29/JsonSchemaGenerator/blob/master/json-schema-generator/src/commonMain/kotlin/io/github/stream29/jsonschemagenerator/JsonTypeMapping.kt
/* changes
 *  - remove all 'public'
 */

/**
 * Returns the JSON type name for the given [SerialKind].
 */
@OptIn(ExperimentalSerializationApi::class)
val SerialKind.jsonTypeName: String
    get() = when (this) {
        PrimitiveKind.STRING -> "string"
        PrimitiveKind.BYTE -> "integer"
        PrimitiveKind.SHORT -> "integer"
        PrimitiveKind.INT -> "integer"
        PrimitiveKind.LONG -> "integer"
        PrimitiveKind.FLOAT -> "number"
        PrimitiveKind.DOUBLE -> "number"
        PrimitiveKind.BOOLEAN -> "boolean"
        PrimitiveKind.CHAR -> "string"
        StructureKind.LIST -> "array"
        StructureKind.MAP -> "object"
        StructureKind.CLASS -> "object"
        StructureKind.OBJECT -> "string"
        SerialKind.ENUM -> "string"
        is PolymorphicKind -> "object"
        SerialKind.CONTEXTUAL -> "object"
    }

/**
 * Returns the JSON type restriction for the given [SerialKind].
 * The result is a closed interval of the form `[min, max]`.
 */
val SerialKind.jsonNumberRestriction: Pair<Number, Number>?
    get() = when (this) {
        PrimitiveKind.BYTE -> Byte.MIN_VALUE to Byte.MAX_VALUE
        PrimitiveKind.SHORT -> Short.MIN_VALUE to Short.MAX_VALUE
        PrimitiveKind.INT -> Int.MIN_VALUE to Int.MAX_VALUE
        PrimitiveKind.FLOAT -> Float.MIN_VALUE to Float.MAX_VALUE
        else -> null
    }
