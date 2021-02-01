# PicPay - Desafio Android
## Ações abordadas para resolução do desafio.

#### Com o objetivo de tornar o aplicativo mais escalável, organizado e testável foi escolhido o padrão de arquitetura MVVM. Permitindo desacoplar o código separando cada parte por suas responsabilidades.
### As camadas serão configuradas conforme link abaixo da documentação do Android.
https://developer.android.com/jetpack/guide

![](https://github.com/sabinabernardes/desafio-android-PicPay/blob/main/mvvm.png)

### 1-Organização das pastas 
#### Movendo os arquivos existentes e criando novos packages para se enquadrar na arquitetura

-> Classe User  foi para o package data/model
-> Interface PicPayService foi para o package data/service

##### OBS: Os outros items iriam para classe de view, porem durante os testes quando eu movia para o package view o aplicativo não inicializava.
##### Pergunta: Há algum procedimento especial para fazer a transferência de arquivos para que esse erro não ocorra?

### 2- Classe Remote Data Source
#### Criada a classe RemoteDataSource no package data/service com o objetivo de retirar a chamada da api da MainActivity 





 ``
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
 
``


### 3- Repository
#### Camada Responsável na arquitetura MVVM por decidir de onde serão buscados os dados da aplicação. Como neste caso estamos trabalhando com a chamada da Api através da biblioteca do retrofit esses dados serão acessados pela Classe RemoteDataSource.

### 3.1 - Interface UserRepository
#### função getUserRemoteDataSource(Solicita os usuarios para a api ) do tipo Lista de Usuários 


``

interface UserRepository 
{
    suspend fun getUsersRemoteDataSource() : List<User>
}

``
### 3.2 Classe UserRepositoryImplementation
#### Classe que herda do UserRepository. E implementa a função getUserRemoteDataSource() utilizando o conceito de coroutines , ou seja , requerindo os usuarios da api em uma thread secundaria .

class UserRepositoryImplementation : UserRepository {

    override suspend fun getUsersRemoteDataSource() =
        withContext(Dispatchers.IO) {
            RemoteDataSource().service.getUsers()
        }

}
#### Conceitos Utilizados :
Coroutines: São chamados de threads leves e tem o objetivo de de não bloquear a thread principal . 
Pode substituir o Callback
Há a possibilidade de escrever códigos assíncronos de maneira sequencial mantendo o código mais simples
Gerenciamento de threads de background 
Dispatchers.IO:
Threads:
*** Recebe o repositor já construído via injeção de dependências 
viewModelScope : escopo de coroutines na camada
#### Pergunta : O nome da função getUserRemoteDataSource seria um nome valído para um projeto , onde esse código seria revisado por outros , ou seja , seria um ideal , ou há outras maneiras de nomeação na hora da aplicação em projeto .

### 3.3 PicPayService 
#### Troca do tipo de retorno da função de Call<List<User>> para List<User> com o objetivo ce facilitar a leitura do usuarios no adapter .
Obs : Tive certa dificuldade em utilizar a metodologia do observais tanto no 
### 4 UserViewModel
#### fornece os dados para um componente de IU específico, como um fragmento ou atividade, e contém lógica de negócios de manipulação de dados para se comunicar com o modelo.
4.1 Herda da classe ViewModel ()
4.2 MutableLiveData
4.3 função coroutines
4.5 chamada do repositor
4.6 viewModelScope.lauch.Main
4.7 try catch
4.8 Classe ResultUsers
4.9 UserViewModelFactory
### 5 MainActivity(View)
#### função
5.1 recyclerViewConfig()
5.2 progressBarConfig()
5.3 viewModelProvider
5.4 viewModelObservable
5.5 fun addUsers
5.6 fun setErroDispay
### 6 Teste Unitários 



