package com.hiqmalism.mystorysubmission.view.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hiqmalism.mystorysubmission.data.resource.UserRepository
import com.hiqmalism.mystorysubmission.data.api.response.ErrorResponse
import com.hiqmalism.mystorysubmission.data.api.response.Story
import kotlinx.coroutines.launch
import retrofit2.HttpException

class DetailViewModel(private val repository: UserRepository) :ViewModel() {

    private val _storyId = MutableLiveData<String>()

    private val _storyDetail = MutableLiveData<Story>()
    val storyDetail: LiveData<Story> = _storyDetail

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun setStoryId(id: String) {
        _storyId.value = id
        getStoryDetail(id)
    }

    private fun getStoryDetail(id: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val story = repository.getStoryDetail(id)!!
                _storyDetail.value = story
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                Log.e("DetailViewModel", "HTTP Exception: ${errorBody.message}")
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Exception: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}