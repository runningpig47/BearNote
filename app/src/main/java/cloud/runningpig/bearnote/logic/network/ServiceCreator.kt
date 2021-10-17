package cloud.runningpig.bearnote.logic.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object ServiceCreator {

    private const val BASE_URL = "http://192.168.1.103:8080/user/"

    private val mGson = GsonBuilder()
//        .serializeNulls()
//        .serializeSpecialFloatingPointValues()
//        .disableHtmlEscaping()
        .setLenient()
        .create()

    private val retrofit = Retrofit.Builder()
        .client(genericClient())
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(mGson))
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)

    inline fun <reified T> create(): T = create(T::class.java)

    private fun genericClient() =
        OkHttpClient.Builder().addInterceptor(LoggingInterceptor()).build()

}