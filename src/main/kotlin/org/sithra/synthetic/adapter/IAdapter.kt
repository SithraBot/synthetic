package org.sithra.synthetic.adapter

/**
 * IAdapter is an interface representing an adapter to an AI model.
 *
 * An adapter provides two main services: a chat service and an embedded service.
 * The chat service is used to generate text based on human instructions.
 * The embedded service is used to generate embeddings for text.
 *
 * @see IChatService
 * @see IEmbeddedService
 */
interface IAdapter {
    val chatService: IChatService
    val embeddedService: IEmbeddedService
}