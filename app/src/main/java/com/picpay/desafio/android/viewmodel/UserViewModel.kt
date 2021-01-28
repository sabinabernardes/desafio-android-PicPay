package com.picpay.desafio.android.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.picpay.desafio.android.data.repository.UserRepository
import com.picpay.desafio.android.data.repository.UserRepositoryImplementation
import com.picpay.desafio.android.results.ResultUsers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) :ViewModel() {

    val usersMutableLiveData = MutableLiveData<ResultUsers>()

    fun getUsersCoroutines(){
        viewModelScope.launch(Dispatchers.Main)
        {
            try {
                val response = repository.getUsersRemoteDataSource()
                usersMutableLiveData.value = ResultUsers.AddUsers(response)

            }catch(exception:Exception){
                usersMutableLiveData.value = ResultUsers.SetErroDispay(error = true)
            }
        }
    }
    class UserViewModelFactory(
        private val repository: UserRepositoryImplementation
    ):ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return UserViewModel(repository) as T
        }

    }


}