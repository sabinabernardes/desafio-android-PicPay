package com.picpay.desafio.android.data.repository

import com.picpay.desafio.android.data.model.User

interface UserRepository {
    suspend fun getUsersRemoteDataSource() : List<User>
}