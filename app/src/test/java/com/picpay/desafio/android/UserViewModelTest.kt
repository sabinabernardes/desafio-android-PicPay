package com.picpay.desafio.android

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.picpay.desafio.android.data.model.User
import com.picpay.desafio.android.data.repository.UserRepository
import com.picpay.desafio.android.data.results.ResultUsers
import com.picpay.desafio.android.viewmodel.UserViewModel
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.internal.matchers.Equals
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class UserViewModelTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private lateinit var viewModel: UserViewModel

    @Mock
    lateinit var repository: UserRepository

    @Before
    fun setup() {
        viewModel = UserViewModel(repository)
    }


    @Test
    fun plusOperationTest() {
        assertEquals("Test", 4, 2 + 2)
    }

    @Test
    fun `test must get a user`() {
        suspend fun testUserResponseApi() = runBlocking {

            val userA: List<User> =
                listOf(User(img = "", name = "name", id = 1, username = "username"))

            val users = MutableLiveData<ResultUsers>()

            viewModel.getUsersCoroutines()

            doReturn(userA)
                .`when`(repository).getUsersRemoteDataSource()

            assertEquals(
                "Test", userA, users.value
            )
        }
    }

    @Test
    fun `Erro return to api`() = runBlocking {
        val returned: ResultUsers.SetErroDispay
        = ResultUsers.SetErroDispay(error = true)
        val users = MutableLiveData<ResultUsers>()
        viewModel.getUsersCoroutines()

        doReturn(returned)
            .`when`(repository).getUsersRemoteDataSource()
        Assert.assertEquals("Test", returned, users.value)
        Assert.assertTrue("Test", returned)

    }
}