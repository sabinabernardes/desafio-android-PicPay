package com.picpay.desafio.android.results

import com.picpay.desafio.android.data.model.User

sealed class ResultUsers{
    data class AddUsers(val resultUserList: List<User>): ResultUsers()
    data class SetErroDispay(val error : Boolean): ResultUsers()
}
