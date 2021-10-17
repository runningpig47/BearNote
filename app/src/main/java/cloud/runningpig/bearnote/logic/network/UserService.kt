package cloud.runningpig.bearnote.logic.network

import cloud.runningpig.bearnote.logic.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface UserService {

    @POST("register")
    fun addUser(@Body user: User): Call<String>

    @FormUrlEncoded
    @POST("find")
    fun login(@Field("username") username: String, @Field("password") password: String): Call<User>

}