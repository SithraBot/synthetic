package store

import kotlin.uuid.Uuid

interface IDocument {
    val id: Uuid
    val document: String
}