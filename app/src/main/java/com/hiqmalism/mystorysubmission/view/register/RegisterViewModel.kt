package com.hiqmalism.mystorysubmission.view.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hiqmalism.mystorysubmission.data.resource.UserRepository
import com.hiqmalism.mystorysubmission.data.api.response.ErrorResponse
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterViewModel(private val repository: UserRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun registerUser(name: String, email: String, password: String) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = repository.register(name, email, password)
                _successMessage.value = response.message
                _errorMessage.value = null
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
}
