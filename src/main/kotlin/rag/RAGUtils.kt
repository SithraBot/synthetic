package rag

import context.IContext.ContextWithRAGBase

suspend fun <Doc : IDocument> ContextWithRAGBase<Doc>.search(query: String, limit: Int): List<Doc> {
    val queryVector = embeddedService.embed {
        model = ragBase.model
        input = query
    }.embedding
    return ragBase.search(queryVector, limit).map { ragBase.getDocument(it)!! }
}
