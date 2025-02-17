import java.util.*

object Props {
    private val secret = kotlin.run {
        val props = Properties()
        props.load(Props::class.java.getResourceAsStream("secrets.properties"))
        props
    }

    fun getDeepSeekKey(): String {
        return secret.getProperty("deepseek_key")
    }

    fun getAliyunKey(): String {
        return secret.getProperty("aliyun_key")
    }
}