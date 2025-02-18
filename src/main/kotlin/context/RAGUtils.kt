package context

import context.IContext.ContextWithRAGBase
import store.IDocument

suspend fun <Doc : IDocument> ContextWithRAGBase<Doc>.search(query: String, limit: Int): List<Doc> {
    val embedModel = model
    val queryVector = embeddedService.embed {
        model = embedModel
        input = query
    }.embedding
    return ragBase.search(queryVector, limit).map { ragBase.getDocument(it)!! }
}
