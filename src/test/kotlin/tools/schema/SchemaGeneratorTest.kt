package tools.schema

import org.sithra.synthetic.store.Message
import kotlinx.serialization.json.Json
import org.sithra.synthetic.tools.schema.SchemaGenerator
import org.sithra.synthetic.tools.schema.schemaOf
import kotlin.test.Test

object SchemaGeneratorTest {
    @Test
    fun testGenerate() {
        val schema = SchemaGenerator.default.schemaOf<Message>()
        println(Json.encodeToString(schema))
    }
}