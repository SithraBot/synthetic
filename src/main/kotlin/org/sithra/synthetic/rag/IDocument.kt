package org.sithra.synthetic.rag

import kotlin.uuid.Uuid

interface IDocument {
    val id: Uuid
    val document: String
}