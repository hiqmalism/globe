package com.hiqmalism.mystorysubmission.view.upload

import androidx.lifecycle.ViewModel
import com.hiqmalism.mystorysubmission.data.resource.UserRepository
import java.io.File

class StoryUploadViewModel(private val repository: UserRepository) : ViewModel() {

    fun uploadStory(file: File, description: String) = repository.uploadStory(file, description)

}
