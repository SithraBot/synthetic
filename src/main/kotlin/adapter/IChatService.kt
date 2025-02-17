package adapter

import context.Message
import kotlinx.serialization.Serializable

interface IChatService {
    @Serializable
    data class ChatRequest(val messages: List<Message>, val model: String?, val temperature: Double = 0.7)

    @Serializable
    data class ChatResponse(val message: Message)

    class ChatRequestBuilder(
        var messages: List<Message>,
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
        fun addMessage(message: Message) = apply { this.messages += message }

        /**
         * Adds a message to the chat request by evaluating the given lambda expression.
         *
         * This is a convenience function for adding a message to the chat request without
         * having to explicitly call the lambda expression.
         *
         * @param message The lambda expression which returns the Message object to be added to the chat request.
         * @return The current instance of ChatRequestBuilder for chaining.
         */
        inline fun addMessage(message: () -> Message) = addMessage(message())

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

    suspend fun chat(body: ChatRequest): ChatResponse

    suspend fun chat(bodyBuilder: ChatRequestBuilder.() -> Unit): ChatResponse {
        val builder = ChatRequestBuilder(emptyList(), null)
        builder.bodyBuilder()
        return chat(builder.build())
    }


    suspend fun chatStream(body: ChatRequest, block: suspend (content: String?, done: Boolean) -> Unit)

    suspend fun chatStream(
        bodyBuilder: ChatRequestBuilder.() -> Unit,
        block: suspend (content: String?, done: Boolean) -> Unit
    ) {
        val builder = ChatRequestBuilder(emptyList(), null)
        builder.bodyBuilder()
        chatStream(builder.build(), block)
    }
}