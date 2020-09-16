package com.jobayr.arxiv.models

data class Feed(
    var totalResults: Int = 0,
    var papers: MutableList<Entry> = mutableListOf()
)

data class Entry(
    var absUrl: String = "",
    var updatedOn: String = "",
    var publishedOn: String = "",
    var title: String = "",
    var abstract: String,
    var authors: MutableList<Author> = mutableListOf(),
    var doi: String = "",
    var comment: String = "",
    var journalRef: String = "",
    var pdfUrl: String = "",
    var primaryCat: String = "",
    var cat: String = ""
)

data class Author(
    var name: String = "",
    var affiliation: String = ""
)