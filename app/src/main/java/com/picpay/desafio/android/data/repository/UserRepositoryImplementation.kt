package com.picpay.desafio.android.data.repository

import com.picpay.desafio.android.data.model.User
import com.picpay.desafio.android.data.service.RemoteDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImplementation : UserRepository {

    override suspend fun getUsersRemoteDataSource()=
        withContext(Dispatchers.IO) {
            RemoteDataSource().service.getUsers()
        }
}