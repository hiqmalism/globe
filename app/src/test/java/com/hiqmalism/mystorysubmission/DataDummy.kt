package com.hiqmalism.mystorysubmission

import com.hiqmalism.mystorysubmission.data.api.response.ListStoryItem

object DataDummy {

    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "createdAt + $i",
                "name $i",
                "description $i",
            )
            items.add(story)
        }
        return items
    }
}