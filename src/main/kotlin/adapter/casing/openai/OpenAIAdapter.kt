package adapter.casing.openai

import adapter.IAdapter
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

@Suppress("unused")
class OpenAIAdapter(private val config: Config) : IAdapter {
    data class Config(
        val baseUrl: Url,
        val token: String,
        val requestTimeout: Long = 180_000,
        val connectTimeout: Long = 30_000,
        val socketTimeout: Long = 30_000
    ) {
        companion object {
            fun builder() = ConfigBuilder()
        }
    }

    class ConfigBuilder(
        var baseUrl: Url? = null,
        var token: String? = null,
        var requestTimeout: Long = 180_000,
        var connectTimeout: Long = 30_000,
        var socketTimeout: Long = 30_000
    ) {

        /**
         * Sets the base URL for the configuration.
         *
         * @param baseUrl The base URL to be set.
         * @return The current instance of ConfigBuilder for chaining.
         */
        fun setBaseUrl(baseUrl: Url) = apply { this.baseUrl = baseUrl }

        /**
         * Sets the base URL for the configuration by evaluating the given lambda expression.
         *
         * @param baseUrl The lambda expression which returns the base URL to be set.
         * @return The current instance of ConfigBuilder for chaining.
         */
        inline fun baseUrl(baseUrl: () -> Url) = setBaseUrl(baseUrl())

        /**
         * Sets the token for the configuration.
         *
         * @param token The token to be set.
         * @return The current instance of ConfigBuilder for chaining.
         */
        fun setToken(token: String) = apply { this.token = token }

        /**
         * Sets the token for the configuration by evaluating the given lambda expression.
         *
         * @param token The lambda expression which returns the token to be set.
         * @return The current instance of ConfigBuilder for chaining.
         */
        inline fun token(token: () -> String) = setToken(token())

        /**
         * Sets the request timeout for the configuration.
         *
         * @param requestTimeout The request timeout in milliseconds to be set.
         * @return The current instance of ConfigBuilder for chaining.
         */
        fun setRequestTimeout(requestTimeout: Long) = apply { this.requestTimeout = requestTimeout }

        /**
         * Sets the request timeout for the configuration by evaluating the given lambda expression.
         *
         * The request timeout is the maximum time in milliseconds that the client will wait for a response.
         *
         * @param requestTimeout The lambda expression which returns the request timeout in milliseconds to be set.
         * @return The current instance of ConfigBuilder for chaining.
         */
        inline fun requestTimeout(requestTimeout: () -> Long) = setRequestTimeout(requestTimeout())

        /**
         * Sets the connect timeout for the configuration.
         *
         * The connect timeout is the maximum time in milliseconds that the client will wait while connecting to the server.
         *
         * @param connectTimeout The connect timeout in milliseconds to be set.
         * @return The current instance of ConfigBuilder for chaining.
         */
        fun setConnectTimeout(connectTimeout: Long) = apply { this.connectTimeout = connectTimeout }

        /**
         * Sets the connect timeout for the configuration by evaluating the given lambda expression.
         *
         * The connect timeout is the maximum time in milliseconds that the client will wait while connecting to the server.
         *
         * @param connectTimeout The lambda expression which returns the connect timeout in milliseconds to be set.
         * @return The current instance of ConfigBuilder for chaining.
         */
        inline fun connectTimeout(connectTimeout: () -> Long) = setConnectTimeout(connectTimeout())

        /**
         * Sets the socket timeout for the configuration.
         *
         * The socket timeout is the maximum time in milliseconds that the client will wait
         * for data to be available for reading from the socket.
         *
         * @param socketTimeout The socket timeout in milliseconds to be set.
         */

        fun setSocketTimeout(socketTimeout: Long) = apply { this.socketTimeout = socketTimeout }

        /**
         * Sets the socket timeout for the configuration by evaluating the given lambda expression.
         *
         * The socket timeout is the maximum time in milliseconds that the client will wait
         * for data to be available for reading from the socket.
         *
         * @param socketTimeout The lambda expression which returns the socket timeout in milliseconds to be set.
         * @return The current instance of ConfigBuilder for chaining.
         */
        inline fun socketTimeout(socketTimeout: () -> Long) = setSocketTimeout(socketTimeout())

        /**
         * Builds and returns a Config instance with the current settings.
         *
         * This method assembles the current configuration parameters, including
         * the base URL, token, request timeout, connect timeout, and socket timeout,
         * into a Config object. All parameters must be set before calling this method.
         *
         * @return A Config object initialized with the current settings.
         * @throws NullPointerException If any of the required parameters are not set.
         */
        fun build(): Config {
            return Config(baseUrl!!, token!!, requestTimeout, connectTimeout, socketTimeout)
        }
    }

    companion object {
        /**
         * A convenience function to create an OpenAIAdapter instance.
         *
         * This function takes a lambda expression with a receiver of ConfigBuilder.
         * Inside the lambda expression, you can set the base URL, token,
         * request timeout, connect timeout, and socket timeout.
         * The function then calls the build() method of the ConfigBuilder
         * and returns an OpenAIAdapter instance initialized with the built Config.
         *
         * @param builder A lambda expression with a receiver of ConfigBuilder.
         * @return An OpenAIAdapter instance initialized with the built Config.
         * @throws NullPointerException If any of the required parameters are not set.
         */
        operator fun invoke(builder: ConfigBuilder.() -> Unit): OpenAIAdapter {
            val config = Config.builder().apply(builder).build()
            return OpenAIAdapter(config)
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = config.requestTimeout
            connectTimeoutMillis = config.connectTimeout
            socketTimeoutMillis = config.socketTimeout
        }
    }
    private val chatService by lazy { ChatService(client, config, json) }
    private val embeddedService by lazy { EmbeddedService(client, config, json) }

    /**
     * Returns the chat service object.
     *
     * @return The chat service object associated with this adapter.
     */
    override fun getChatService(): adapter.IChatService {
        return chatService
    }

    /**
     * Returns the embedded service object.
     *
     * @return The embedded service object associated with this adapter.
     */
    override fun getEmbeddedService(): adapter.IEmbeddedService {
        return embeddedService
    }
}