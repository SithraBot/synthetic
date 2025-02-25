package org.sithra.synthetic.adapter.casing.openai

import org.sithra.synthetic.adapter.IAdapter
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import org.sithra.synthetic.adapter.IChatService
import org.sithra.synthetic.adapter.IEmbeddedService

/**
 * OpenAIAdapter is an adapter implementation for interfacing with OpenAI's services.
 *
 * @property config The configuration settings for the adapter.
 *
 * This adapter provides chat and embedding services by implementing the IAdapter interface.
 * It uses ktor HTTP client for making requests to the specified base URL with the provided token.
 *
 * The configuration for the adapter includes various timeout settings to control the request,
 * connection, and socket timeouts.
 *
 * Usage:
 * ```kotlin
 * val adapter = OpenAIAdapter {
 *     baseUrl = Url("https://api.openai.com/v1")
 *     token = "your-api-key"
 * }
 * ```
 *
 * @see ChatService
 * @see EmbeddedService
 */
class OpenAIAdapter(private val config: Config) : IAdapter {
    /**
     * The Config class encapsulates configuration settings for the OpenAIAdapter.
     *
     * @property baseUrl The base URL for making API requests.
     * @property token The authentication token for accessing OpenAI services.
     * @property requestTimeout The request timeout duration in milliseconds.
     * @property connectTimeout The connection timeout duration in milliseconds.
     * @property socketTimeout The socket timeout duration in milliseconds.
     *
     * Usage:
     *
     * Config Usage Example:
     * ```kotlin
     * val config = OpenAIAdapter.Config(
     *     baseUrl = Url("https://api.openai.com/v1"),
     *     token = "your-api-key"
     * )
     * ```
     *
     * The ConfigBuilder class provides a builder pattern for creating Config instances.
     *
     * ConfigBuilder Usage Example:
     * ```kotlin
     * val config = OpenAIAdapter.Config.builder()
     *     .setBaseUrl(Url("https://api.openai.com/v1"))
     *     .setToken("your-api-key")
     *     .build()
     * ```
     *
     * The ConfigBuilder allows for setting the base URL, token, and various timeout settings.
     */
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

    /**
     * Construct an instance of OpenAIAdapter using the given configuration settings.
     *
     * @param baseUrl The base URL for making API requests.
     * @param token The authentication token for accessing OpenAI services.
     * @param requestTimeout The request timeout duration in milliseconds.
     * @param connectTimeout The connection timeout duration in milliseconds.
     * @param socketTimeout The socket timeout duration in milliseconds.
     */
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
         * OpenAIAdapter is an adapter implementation for interfacing with OpenAI's services.
         *
         * This adapter provides chat and embedding services by implementing the IAdapter interface.
         * It uses ktor HTTP client for making requests to the specified base URL with the provided token.
         *
         * The configuration for the adapter includes various timeout settings to control the request,
         * connection, and socket timeouts.
         *
         * Usage:
         * ```kotlin
         * val adapter = OpenAIAdapter {
         *     baseUrl = Url("https://api.openai.com/v1")
         *     token = "your-api-key"
         *     requestTimeout = 10000L
         *     connectTimeout = 5000L
         *     socketTimeout = 5000L
         * }
         * ```
         *
         * @param builder A lambda expression which configures the OpenAIAdapter.
         *
         * @see ChatService
         * @see EmbeddedService
         */
        operator fun invoke(builder: ConfigBuilder.() -> Unit): OpenAIAdapter {
            val config = Config.builder().apply(builder).build()
            return OpenAIAdapter(config)
        }
    }

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
        encodeDefaults = true
    }
    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = config.requestTimeout
            connectTimeoutMillis = config.connectTimeout
            socketTimeoutMillis = config.socketTimeout
        }
    }
    override val chatService by lazy { ChatService(client, config, json) }
    override val embeddedService by lazy { EmbeddedService(client, config, json) }
}