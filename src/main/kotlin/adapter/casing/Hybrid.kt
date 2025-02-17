package adapter.casing

import adapter.IAdapter

@Suppress("unused")
class Hybrid(private val chatFrom: IAdapter, private val embeddedFrom: IAdapter) : IAdapter {

    override fun getChatService(): adapter.IChatService {
        return chatFrom.getChatService()
    }

    override fun getEmbeddedService(): adapter.IEmbeddedService {
        return embeddedFrom.getEmbeddedService()
    }

}