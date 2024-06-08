package com.hiqmalism.mystorysubmission.view.main

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.gson.Gson
import com.hiqmalism.mystorysubmission.data.api.response.ErrorResponse
import com.hiqmalism.mystorysubmission.data.resource.UserRepository
import com.hiqmalism.mystorysubmission.data.api.response.ListStoryItem
import com.hiqmalism.mystorysubmission.data.pref.UserModel
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    val listStory: LiveData<PagingData<ListStoryItem>> = repository.getStories().cachedIn(viewModelScope)

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> get() = _errorMessage

    internal fun getStories() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                _isLoading.value = false
                listStory
            } catch (e: HttpException) {
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                _errorMessage.value = errorBody.message
                _successMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "An unexpected error occurred"
                _successMessage.value = null
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }
}
