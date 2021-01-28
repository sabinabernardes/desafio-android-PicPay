package com.picpay.desafio.android

import android.content.ContentValues
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.picpay.desafio.android.data.service.PicPayService
import com.picpay.desafio.android.data.model.User
import com.picpay.desafio.android.data.repository.UserRepository
import com.picpay.desafio.android.data.repository.UserRepositoryImplementation
import com.picpay.desafio.android.results.ResultUsers
import com.picpay.desafio.android.viewmodel.UserViewModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: UserListAdapter

    override fun onResume() {
        super.onResume()

        recyclerViewConfig()
        progressBarConfig()

        val viewModel = ViewModelProvider(this,
            UserViewModel.UserViewModelFactory(UserRepositoryImplementation())) //
            .get(UserViewModel::class.java)

        viewModel.usersMutableLiveData.observe(this, Observer { users ->
            users?.let {
                adapter.notifyDataSetChanged()
                when (it) {
                    is ResultUsers.AddUsers -> addUsers(it.resultUserList)
                    is ResultUsers.SetErroDispay-> setErroDispay()

                }
            }
        })
        viewModel.getUsersCoroutines()
    }
    private fun recyclerViewConfig(){
        recyclerView = findViewById(R.id.recyclerView)
        adapter = UserListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
    private fun progressBarConfig(){
        progressBar = findViewById(R.id.user_list_progress_bar)
        progressBar.visibility = View.VISIBLE
    }
    private fun addUsers(usersAdd: List<User>) {
        progressBar.visibility = View.GONE
        adapter.users = usersAdd
    }
    private fun setErroDispay() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE

        Toast.makeText(this@MainActivity, getString(R.string.error), Toast.LENGTH_SHORT)
            .show()
    }

}
