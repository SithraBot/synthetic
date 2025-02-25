package org.sithra.synthetic.adapter

import org.sithra.synthetic.store.IMessage
import org.sithra.synthetic.store.Message
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import org.sithra.synthetic.tools.ITools
import org.sithra.synthetic.tools.toolcall.IToolCall

/**
 *  IChatService is an interface representing a service that can be used to handle chat requests.
 */
interface IChatService {
    @Serializable
    /**
     *  ChatRequest is a data class representing a request to the chat service.
     *
     *  @property messages The list of messages to be sent to the chat service.
     *  @property model The model name to be used for the chat service.
     *  @property temperature The temperature to be used for the chat service.
     */
    data class ChatRequest(
        val messages: List<IMessage>,
        val model: String?,
        val temperature: Double = 0.7,
    )

    /**
     *  Builder for building a ChatRequest object.
     *
     *  @constructor Creates a new ChatRequestBuilder object with the given initial values.
     *  @property messages The list of messages to be sent to the chat service.
     *  @property model The model name to be used for the chat service.
     *  @property temperature The temperature to be used for the chat service.
     */
    class ChatRequestBuilder(
        var messages: List<IMessage>,
        var model: String?,
        var temperature: Double = 0.7
    ) {
        /**
         * Sets the list of messages for the chat request.
         *
         * @param messages The list of Message objects to be set.
         * @return The current instance of ChatRequestBuilder for chaining.
         */
        fun setMessages(messages: List<Message>) = apply { this.messages = messages }

        /**
         * Sets the list of messages for the chat request by evaluating the given lambda expression.
         *
         * @param messages The lambda expression which returns the list of Message objects to be set.
         * @return The current instance of ChatRequestBuilder for chaining.
         */
        inline fun messages(messages: () -> List<Message>) = setMessages(messages())

        /**
         * Adds a message to the chat request.
         *
         * @param message The Message object to be added to the chat request.
         * @return The current instance of ChatRequestBuilder for chaining.
         */
        fun addMessage(message: IMessage) = apply { this.messages += message }

        /**
         * Adds a message to the chat request by evaluating the given lambda expression.
         *
         * This is a convenience function for adding a message to the chat request without
         * having to explicitly call the lambda expression.
         *
         * @param message The lambda expression which returns the Message object to be added to the chat request.
         * @return The current instance of ChatRequestBuilder for chaining.
         */
        inline fun addMessage(message: () -> IMessage) = addMessage(message())

        /**
         * Sets the model for the chat request.
         *
         * @param model The name of the model to be used for generating a response.
         * @return The current instance of ChatRequestBuilder for chaining.
         */
        fun setModel(model: String) = apply { this.model = model }

        /**
         * Sets the model for the chat request by evaluating the given lambda expression.
         *
         * This is a convenience function for setting the model without
         * having to explicitly call the lambda expression.
         *
         * @param model The lambda expression which returns the name of the model to be used.
         * @return The current instance of ChatRequestBuilder for chaining.
         */
        inline fun model(model: () -> String) = setModel(model())

        /**
         * Sets the temperature for the chat request.
         *
         * The temperature is a value between 0 and 1 that controls the randomness of the generated text.
         * A value of 0 results in a very conservative, repetitive output, while a value of 1 results in a more
         * random, diverse output. The default value is 0.7.
         *
         * @param temperature The temperature to be set, between 0 and 1.
         * @return The current instance of ChatRequestBuilder for chaining.
         */
        fun setTemperature(temperature: Double) = apply { this.temperature = temperature }

        /**
         * Sets the temperature for the chat request by evaluating the given lambda expression.
         *
         * This is a convenience function for setting the temperature without
         * having to explicitly call the lambda expression.
         *
         * @param temperature The lambda expression which returns the temperature between 0 and 1.
         * @return The current instance of ChatRequestBuilder for chaining.
         */
        inline fun temperature(temperature: () -> Double) = setTemperature(temperature())

        /**
         * Builds a ChatRequest object from the properties set on this builder.
         *
         * @return A ChatRequest object representing the chat request.
         */
        fun build(): ChatRequest {
            return ChatRequest(messages, model, temperature)
        }
    }

    /**
     * Handles a chat request.
     *
     * @param body The chat request to be handled.
     * @return The response from the chat service.
     */
    suspend fun chat(body: ChatRequest): Message

    /**
     * Handles a chat request with tools.
     *
     * @param body The chat request to be handled.
     * @param tools The tools to be used for the chat request.
     * @return The response from the chat service.
     */
    suspend fun <T : IToolCall> chatWithTools(body: ChatRequest, tools: ITools<T>): IMessage

    /**
     * Convenience function for handling a chat request using a builder.
     *
     * @param bodyBuilder The lambda expression which configures the ChatRequestBuilder.
     * @return The response from the chat service.
     */
    suspend fun chat(bodyBuilder: ChatRequestBuilder.() -> Unit): Message {
        val builder = ChatRequestBuilder(emptyList(), null)
        builder.bodyBuilder()
        return chat(builder.build())
    }

    /**
     * Convenience function for handling a chat request with tools using a builder.
     *
     * @param tools The tools to be used for the chat request.
     * @param bodyBuilder The lambda expression which configures the ChatRequestBuilder.
     * @return The response from the chat service.
     */
    suspend fun <T : IToolCall> chatWithTools(tools: ITools<T>, bodyBuilder: ChatRequestBuilder.() -> Unit): IMessage {
        val builder = ChatRequestBuilder(emptyList(), null)
        builder.bodyBuilder()
        return chatWithTools(builder.build(), tools)
    }

    /**
     * Convenience function for handling a chat request in a streaming fashion.
     *
     * The response from the chat service is returned as a Flow of strings.
     *
     * @param body The chat request to be handled.
     * @return The response from the chat service as a Flow of strings.
     */
    fun chatStream(body: ChatRequest): Flow<String>

    /**
     * Convenience function for handling a chat request in a streaming fashion using a builder.
     *
     * The response from the chat service is returned as a Flow of strings.
     *
     * @param bodyBuilder The lambda expression which configures the ChatRequestBuilder.
     * @return The response from the chat service as a Flow of strings.
     */
    fun chatStream(
        bodyBuilder: ChatRequestBuilder.() -> Unit,
    ): Flow<String> {
        val builder = ChatRequestBuilder(emptyList(), null)
        builder.bodyBuilder()
        return chatStream(builder.build())
    }
}