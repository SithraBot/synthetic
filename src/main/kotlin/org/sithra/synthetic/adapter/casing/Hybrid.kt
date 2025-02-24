package org.sithra.synthetic.adapter.casing

import org.sithra.synthetic.adapter.IAdapter
import org.sithra.synthetic.adapter.IChatService
import org.sithra.synthetic.adapter.IEmbeddedService

@Suppress("unused")
class Hybrid(private val chatFrom: IAdapter, private val embeddedFrom: IAdapter) : IAdapter {

    override fun getChatService(): IChatService {
        return chatFrom.getChatService()
    }

    override fun getEmbeddedService(): IEmbeddedService {
        return embeddedFrom.getEmbeddedService()
    }

}