package com.picpay.desafio.android.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.picpay.desafio.android.data.model.User
import com.picpay.desafio.android.data.repository.UserRepository
import com.picpay.desafio.android.results.ResultUsers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) :ViewModel() {

    val usersMutableLiveData = MutableLiveData<ResultUsers>()
    fun getUsersCoroutines(){
        viewModelScope.launch(Dispatchers.Main)
        {
            try {
                val result = repository.getUsers()
                usersMutableLiveData.value = ResultUsers.AddUsers(result)
                Log.e(ContentValues.TAG, "dentro VIEW MODEL retorno")
            }catch(exception:Exception){
                usersMutableLiveData.value = ResultUsers.Error(error = true)
            }
        }
    }

}