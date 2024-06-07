package com.hiqmalism.mystorysubmission.view.maps

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.hiqmalism.mystorysubmission.data.resource.UserRepository

class MapsViewModel(private val repository: UserRepository) : ViewModel() {

    fun getStoriesWithLocation() = liveData {
        val stories = repository.getStoriesWithLocation()
        emit(stories)
    }
}