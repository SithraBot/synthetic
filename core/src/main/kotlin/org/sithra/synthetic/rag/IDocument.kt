package org.sithra.synthetic.rag

import kotlin.uuid.Uuid

/**
 * IDocument is an interface representing a document for RAGs.
 *
 * @property id The unique identifier for the document.
 * @property document The content of the document.
 */
interface IDocument {
    val id: Uuid
    val document: String
}