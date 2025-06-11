package com.adamchaniago0025.assesment3.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adamchaniago0025.assesment3.model.Resep
import com.adamchaniago0025.assesment3.network.ApiStatus
import com.adamchaniago0025.assesment3.network.ResepApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {
    var data = mutableStateOf(emptyList<Resep>())
        private set

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
            private set

    fun retrieveData(token: String) {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = ResepApi.service.getResep(token)
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }
    fun saveData(token: String, judul: String, kategori: String, deskripsi: String, bitmap: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmapPart = bitmap?.toMultipartBody() ?: MultipartBody.Part.createFormData("image", "")
                val result = ResepApi.service.postResep(
                    token,
                    judul.toRequestBody("text/plain".toMediaTypeOrNull()),
                    kategori.toRequestBody("text/plain".toMediaTypeOrNull()),
                    deskripsi.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmapPart
                )
                if (result.status == "success")
                    retrieveData(token)
                else
                    throw Exception(result.message)
            } catch (e:Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "error: ${e.message}"
            }
        }
    }
    fun updateData(token: String, id_resep: Long, judul: String, kategori: String, deskripsi: String, bitmap: Bitmap?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bitmapPart = bitmap?.toMultipartBody() ?: MultipartBody.Part.createFormData("image", "")
                val result = ResepApi.service.updateResep(
                    token,
                    "PUT".toRequestBody("text/plain".toMediaTypeOrNull()),
                    id_resep,
                    judul.toRequestBody("text/plain".toMediaTypeOrNull()),
                    kategori.toRequestBody("text/plain".toMediaTypeOrNull()),
                    deskripsi.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmapPart
                )
                if (result.status == "success")
                    retrieveData(token)
                else
                    throw Exception(result.message)
            } catch (e:Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "error: ${e.message}"
            }
        }
    }

    private fun Bitmap.toMultipartBody(): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(), 0, byteArray.size)
        return MultipartBody.Part.createFormData("image", "image.jpg", requestBody)

    }

    fun deleteData(token: String, hewanId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = ResepApi.service.deleteResep(
                    token,
                    hewanId
                )
                if (result.status == "success")
                    retrieveData(token)
                else
                    throw Exception(result.message)
            } catch (e:Exception) {
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "error: ${e.message}"
            }
        }
    }

    fun clearMessage() { errorMessage.value = null}
}


