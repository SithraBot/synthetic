package org.sithra.synthetic.adapter

interface IAdapter {
    fun getChatService(): IChatService

    fun getEmbeddedService(): IEmbeddedService
}