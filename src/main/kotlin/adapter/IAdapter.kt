package adapter

interface IAdapter {
    fun getChatService(): IChatService

    fun getEmbeddedService(): IEmbeddedService
}