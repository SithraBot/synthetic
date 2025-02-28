package org.sithra.synthetic.adapter.casing

import org.sithra.synthetic.adapter.IAdapter
import org.sithra.synthetic.adapter.IChatService
import org.sithra.synthetic.adapter.IEmbeddedService

/**
 * A hybrid adapter that combines the chat service of one adapter and the embedded service of another.
 *
 * @property chatFrom The adapter whose chat service is used.
 * @property embeddedFrom The adapter whose embedded service is used.
 */
class Hybrid(private val chatFrom: IAdapter, private val embeddedFrom: IAdapter) : IAdapter {
    override val chatService: IChatService
        get() = chatFrom.chatService

    override val embeddedService: IEmbeddedService
        get() = embeddedFrom.embeddedService
}