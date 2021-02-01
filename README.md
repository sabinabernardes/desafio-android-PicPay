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

