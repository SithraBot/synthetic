package tools.schema

import store.Message
import kotlinx.serialization.json.Json
import kotlin.test.Test

object SchemaGeneratorTest {
    @Test
    fun testGenerate() {
        val schema = SchemaGenerator.default.schemaOf<Message>()
        println(Json.encodeToString(schema))
    }
}