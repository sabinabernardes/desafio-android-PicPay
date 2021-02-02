# PicPay - Desafio Android
## Ações abordadas para resolução do desafio.

#### Com o objetivo de tornar o aplicativo mais escalável, organizado e testável foi escolhido o padrão de arquitetura MVVM. Permitindo desacoplar o código separando cada parte por suas responsabilidades.
#### As camadas serão configuradas conforme link abaixo da documentação do Android.
https://developer.android.com/jetpack/guide

![](https://github.com/sabinabernardes/desafio-android-PicPay/blob/main/mvvm.png)

### 1-Organização das pastas 
#### Movendo os arquivos existentes e criando novos packages para se enquadrar na arquitetura

#### Dos arquivos existentes foram feitas as mudanças de packages abaixo 
##### -> Classe User  foi para o package data/model
##### -> Interface PicPayService foi para o package data/service

![](https://github.com/sabinabernardes/desafio-android-PicPay/blob/main/packages.png)

##### OBS: Os outros items iriam para classe de view, porem durante os testes quando eu movia para o package view o aplicativo não inicializava.
##### Pergunta: Há algum procedimento especial para fazer a transferência de arquivos para que esse erro não ocorra?

### 2- Classe Remote Data Source
#### Criada a classe RemoteDataSource no package data/service com o objetivo de retirar a chamada da api da MainActivity 

 ```kotlin
 class RemoteDataSource {
 
    private val url = "http://careers.picpay.com/tests/mobdev/"
    private val gson: Gson by lazy { GsonBuilder().create() }

    val service: PicPayService by lazy {
        retrofit.create(PicPayService::class.java)

    }

    private val okHttp: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .build()
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(url)
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    } }
    
```
 



### 3- Repository
#### Camada Responsável na arquitetura MVVM por decidir de onde serão buscados os dados da aplicação. Como neste caso estamos trabalhando com a chamada da Api através da biblioteca do retrofit esses dados serão acessados pela Classe RemoteDataSource.

### 3.1 - Interface UserRepository
#### função getUserRemoteDataSource(Solicita os usuarios para a api ) do tipo Lista de Usuários 

```kotlin
interface UserRepository 
{ 
    suspend fun getUsersRemoteDataSource() : List<User>
}
```




### 3.2 Classe UserRepositoryImplementation
#### Classe que herda as caracteristicas do UserRepository. E implementa a função getUserRemoteDataSource() utilizando o conceito de coroutines , ou seja , requerindo os usuarios da api em uma thread secundaria .

```kotlin
class UserRepositoryImplementation : UserRepository {

    override suspend fun getUsersRemoteDataSource() =
        withContext(Dispatchers.IO) {
            RemoteDataSource().service.getUsers()
        }

}
```
#### Conceitos Utilizados :
##### 3.2.1 Coroutines:
 ###### * São chamados de threads leves e tem o objetivo de de não bloquear a thread principal . 
 ###### * Pode substituir o Callback
 ###### * Há a possibilidade de escrever códigos assíncronos de maneira sequencial mantendo o código mais simples
   
   
##### 3.2.2 Dispatchers.IO: Indicação de Threads secundaria 



#### Pergunta : O nome da função getUserRemoteDataSource seria um nome valído para um projeto , onde esse código seria revisado por outros , ou seja , seria um ideal , ou há outras maneiras de nomeação na hora da aplicação em projeto .

##### Obs : Tive certa dificuldade em utilizar a metodologia do observaveis e callback, por isso decidi utilizar coroutines

### 3.3 PicPayService 
#### Troca do tipo de retorno da função de Call<List<User>> para List<User> com o objetivo ce facilitar a leitura do usuarios no adapter .
 
 ```kotlin
 @GET("users")
    suspend fun getUsers(): List<User>
 ```


### 4 UserViewModel
#### Fornece os dados para um componente de UI, como a MainActivity, e contém lógica de negócios de manipulação de dados para se comunicar com o repository

 ```kotlin
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
   ``` 
### 4.1 Herda da classe ViewModel () 
#### Trás as caracteristicas necessarias para funcionar como viewModel
``
:ViewModel()
``

### 4.2 LiveData
#### Dados que vao receber as informações da resposta da api ou do db e quando houver alterções enviam para o componente de view que estará observando o conforme o ciclo de vida da Activity

#### Para utiliza-los nessaria a biblioteca lifecycle. Localizada no build.gradle(Module)

#####  `` implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version" ``
#####  ``implementation "androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version" ``
#####  ``implementation "androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version" ``

##### Pergunta: Eu pude verificar que no build.gradle há varias biblioteca implemetadas porem nem todas sao utilizadas. Seria uma boa prática limpar essa bibliotecas? Isso ajuda de alguma forma no desempenho do aplicativo ?

#### Neste caso foi usado um MutableLiveData uma lista mutável que retornará o tipo ResultUsers 
 ```kotlin
 val usersMutableLiveData = MutableLiveData<ResultUsers>()
 ```
### 4.3 Classe ResultUsers

#### Classe selada (Classe fechada ), que dependendo do valor que é recebido passa para o viewModel uma lista de usuários .A vantagem de trabalhar com uma classe selada e nao precisar validar com if
```kotlin
sealed class ResultUsers{


    data class AddUsers(val resultUserList: List<User>): ResultUsers()
    data class SetErroDispay(val error : Boolean): ResultUsers()
}
```


### 4.4 fun getUsersCoroutines()
#### Função que é chamada na thread principal e testa se a resposta da api passando pelo repositorio for correta o valor é atribuido ao live Data usersMutableLiveData
#### Caso o valor retorne uma exception o valor atribuido vai para a função de erro .

```kotlin
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
```

   
### 4.5 UserViewModelFactory
####  Cria uma nova instancia do viewModel e passa para o repositorio 

```kotlin
class UserViewModelFactory(
        private val repository: UserRepositoryImplementation
    ):ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return UserViewModel(repository) as T
        }

    }
 ```
#### a variavel repositorio foi declarada atraves do conceito de injeção de dependencias  como mostrado abaixo 

```kotlin
class UserViewModel(private val repository: UserRepository)
 ```
 
### 5 MainActivity(View)

#### Classe que possui as configurações de view 

```kotlin
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

```

### 5.1 recyclerViewConfig()
#### Função para configurar a recyclerView 
  ```kotlin
  private fun recyclerViewConfig(){
        recyclerView = findViewById(R.id.recyclerView)
        adapter = UserListAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
  ```
### 5.2 progressBarConfig()
#### Função para configurar a progressBar
 ```kotlin
  private fun progressBarConfig(){
        progressBar = findViewById(R.id.user_list_progress_bar)     
        progressBar.visibility = View.VISIBLE
    }
   ```
   
### 5.3 viewModelProvider
#### variavel que instancia o viewModel referente a esta Activity

 ```kotlin
 val viewModel = ViewModelProvider(this,
            UserViewModel.UserViewModelFactory(UserRepositoryImplementation())) //
            .get(UserViewModel::class.java)
 ```
 
### 5.4 viewModelObservable
#### Observa as alterações do usersMultableLiveData da classe viewModel notifica o adapter
#### Quando a resposta do ResultUsers é para adicionar os usuarios chama a função addUsers
#### Já quando a resposta de ResultUsers vem com erro chama a função de erro 
 ```kotlin
  viewModel.usersMutableLiveData.observe(this, Observer { users ->
            users?.let {
                adapter.notifyDataSetChanged()
                when (it) {
                    is ResultUsers.AddUsers -> addUsers(it.resultUserList)
                    is ResultUsers.SetErroDispay-> setErroDispay()

                }
            }
  ```
### 5.5 fun addUsers
#### Função que passa a lista de usuarios para dentro do adapter 
 ```kotlin
 private fun addUsers(usersAdd: List<User>) {
        progressBar.visibility = View.GONE
        adapter.users = usersAdd
    }
```
 
### 5.6 fun setErroDispay
#### Função para configurar a progressBar
 ```kotlin
 
   private fun setErroDispay() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE

        Toast.makeText(this@MainActivity, getString(R.string.error), Toast.LENGTH_SHORT)
            .show()
    }
    
```
 
### 6 Teste Unitários 



