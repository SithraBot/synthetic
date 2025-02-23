import java.util.*

object Props {
    private val secret = run {
        val props = Properties()
        props.load(Props::class.java.getResourceAsStream("secrets.properties"))
        props
    }

    val documents = run {
        val docs = Props::class.java.getResourceAsStream("docs.json")
        docs!!.bufferedReader().use { it.readText() }
    }

    fun getDeepSeekKey(): String {
        return secret.getProperty("deepseek_key")
    }

    fun getAliyunKey(): String {
        return secret.getProperty("aliyun_key")
    }
}