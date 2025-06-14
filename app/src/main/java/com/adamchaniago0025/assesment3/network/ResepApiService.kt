package com.adamchaniago0025.assesment3.network

import com.adamchaniago0025.assesment3.model.Resep
import com.adamchaniago0025.assesment3.model.OpStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

private const val BASE_URL = "https://resep-api.michael-kaiser.my.id/api/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface ResepApiService {
    @GET("resep")
    suspend fun getResep(
        @Header("Authorization") token: String
    ): List<Resep>

    @Multipart
    @POST("resep")
    suspend fun postResep(
        @Header("Authorization") token: String,
        @Part("judul") judul: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part image: MultipartBody.Part
    ): OpStatus

    @Multipart
    @POST("resep/{id_resep}")
    suspend fun updateResep(
        @Header("Authorization") token: String,
        @Part("_method") method: RequestBody,
        @Path("id_resep") id_resep: Long,
        @Part("judul") judul: RequestBody,
        @Part("kategori") kategori: RequestBody,
        @Part("deskripsi") deskripsi: RequestBody,
        @Part image: MultipartBody.Part? = null
    ): OpStatus

    @DELETE("resep/{id_resep}")
    suspend fun deleteResep(
        @Header("Authorization") token: String,
        @Path("id_resep") id_resep: Long
    ): OpStatus

    @FormUrlEncoded
    @POST("register")
    suspend fun postRegister(
        @Field("name") nama: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): OpStatus
}

object ResepApi {
    val service: ResepApiService by lazy {
        retrofit.create(ResepApiService::class.java)
    }
    fun getResepUrl(imageId: Long) :String {
        return "${BASE_URL}resep/image/$imageId?timestamp=${System.currentTimeMillis()}"
    }
}

enum class ApiStatus {
    LOADING,
    SUCCESS,
    FAILED
}
