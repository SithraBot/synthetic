package org.sithra.synthetic.adapter

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

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
     * @property client The HTTP client to use for making requests.
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
        val client: HttpClient = HttpClient(CIO)
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
     */
    class ConfigBuilder(
        var baseUrl: Url? = null,
        var token: String? = null,
        var httpClient: HttpClient = HttpClient(CIO)
    ) {

        /**
         * Sets the base URL for the configuration.
         *
         * @param baseUrl The base URL to be set.
         * @return The current instance of ConfigBuilder for chaining.
         */
        fun setBaseUrl(baseUrl: Url) = apply { this.baseUrl = baseUrl }

        /**
         * Sets the token for the configuration.
         *
         * @param token The token to be set.
         * @return The current instance of ConfigBuilder for chaining.
         */
        fun setToken(token: String) = apply { this.token = token }

        /**
         * Sets the HTTP client for the configuration.
         *
         * @param httpClient The HTTP client to be set.
         * @return The current instance of ConfigBuilder for chaining.
         */
        fun setHttpClient(httpClient: HttpClient) = apply { this.httpClient = httpClient }

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
            return Config(baseUrl!!, token!!, httpClient)
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
    override val chatService by lazy { ChatService(config, json) }
    override val embeddedService by lazy { EmbeddedService(config, json) }
}