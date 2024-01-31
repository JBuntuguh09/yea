package com.dawolf.yea.utils

import android.app.Activity
import android.net.Uri
import android.provider.OpenableColumns
import androidx.constraintlayout.widget.ConstraintLayout
import com.dawolf.yea.resources.ShortCut_To
import com.dawolf.yea.resources.Storage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.net.HttpURLConnection
import java.net.URL

class API {
//


    public fun POSTLOGIN(path: String, body: String) {
        val url = URL(path)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "multipart/form-data")

        val requestBody = body
        println(body)

        connection.outputStream.write(requestBody.toByteArray())

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().readText()
            println(response)
        } else {
            println("Error: HTTP status code $responseCode")
        }

        connection.disconnect()
    }

    public fun POST(path: String, body: String) {
        val url = URL(path)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")

        val requestBody = body
        println(body)

        connection.outputStream.write(requestBody.toByteArray())

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val response = connection.inputStream.bufferedReader().readText()
            println(response)
        } else {
            println("Error: HTTP status code $responseCode")
        }

        connection.disconnect()
    }

    fun postAPI(URL : String, BODY: Map<String, String>, activity: Activity, linear: ConstraintLayout):String{

        var res = "[]"
        try {


            val client = OkHttpClient()
            val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
            for ((key, value) in BODY) {
                builder.addFormDataPart(key, value)
            }

            val requestBody: RequestBody = builder.build()
            val request = Request.Builder()
                .url(URL)
                .post(requestBody)

                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    // Handle the error
                    val responseBody = response.body?.string()
                    println("Error: $responseBody")
                    if (responseBody != null) {
                        res = responseBody
                    }
                } else {
                    // Read the response if needed
                    val responseBody = response.body?.string()
                    println("Response: $responseBody")
                    if (responseBody != null) {
                        res = responseBody
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()

        }


        return res

    }

    fun getAPI(URL: String, activity: Activity):String{
        var res = "[]"
        val storage = Storage(activity)

        try {
            val client = OkHttpClient()


            val request = Request.Builder()
                .url(URL)
                .addHeader("Authorization", storage.tokenId!!)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    // Handle the error
                    val responseBody = response.body?.string()
                    println("Response: $responseBody")
                    if (responseBody != null) {
                        res = responseBody
                    }
                } else {
                    // Read the response if needed
                    val responseBody = response.body?.string()
                  //  println("Response: $responseBody")
                    if (responseBody != null) {
                        res = responseBody
                    }
                }
            }
            // dialog.dismiss()
        }catch (e:Exception){
            e.printStackTrace()
            // dialog.dismiss()
        }


        return res
    }

    fun putAPIWithHeader(URL: String, BODY: Map<String, String>, activity: Activity):String{
        val storage = Storage(activity)
        var res = "[]"
        try {


            val client = OkHttpClient()
            val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
            for ((key, value) in BODY) {
                builder.addFormDataPart(key, value)
            }

            val requestBody: RequestBody = builder.build()
            val request = Request.Builder()
                .url(URL)
                .put(requestBody)
                .header("Authorization", storage.tokenId!!)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    // Handle the error
                    println("Error: ${response.code}")
                } else {
                    // Read the response if needed
                    val responseBody = response.body?.string()
                    println("Response: $responseBody")
                    if (responseBody != null) {
                        res = responseBody
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()

        }


        return res

    }

    fun postAPIWithHeader(URL: String, BODY: Map<String, String>, activity: Activity):String{
        val storage = Storage(activity)
        var res = "[]"
        try {


            val client = OkHttpClient()
            val builder = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
            for ((key, value) in BODY) {
                builder.addFormDataPart(key, value)
            }

            val requestBody: RequestBody = builder.build()
            val request = Request.Builder()
                .url(URL)
                .post(requestBody)

                .addHeader("Authorization", storage.tokenId!!)

                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    // Handle the error
                    val responseBody = response.body?.string()
                    println("Error: $responseBody")
                    if (responseBody != null) {
                        res = responseBody
                    }
                } else {
                    // Read the response if needed
                    val responseBody = response.body?.string()
                    //println("Responses: ${response.body?.string()}")
                    if (responseBody != null) {
                        res = responseBody
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()

        }


        return res

    }

    fun uploadWithHeader(URL : String, fileUri: Uri, activity: Activity, linear: ConstraintLayout):String{
        val storage = Storage(activity)
        var res = "[]"
        try {
            val client = OkHttpClient()
            val mediaType = "image/*".toMediaTypeOrNull()
            val fileName = getFileName(activity, fileUri)
            val file = ShortCut_To.uriToFile(activity, fileUri)
            // val requestBody = file.asRequestBody(mediaType)
            //val imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestBody)
//            val requestBody = MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart("image", file.name,
//                    file.asRequestBody("image/*".toMediaTypeOrNull())
//                ).build()
            val requestBody = file?.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", fileName, requestBody!!)
                .build()

            val request = Request.Builder()
                .url(URL)
                .post(multipartBody)
                .addHeader("Authorization", storage.tokenId!!)
                .build()


            val response = client.newCall(request).execute()

            res= (response.body?.string().toString())
            // dialog.dismiss()
            ShortCut_To.deleteFile(file)
        }catch (e:Exception){
            e.printStackTrace()
            // dialog.dismiss()
        }


        return res

    }

    fun getFileName(activity: Activity, uri: Uri): String {
        var fileName = ""
        val cursor = activity.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                fileName = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        }
        return fileName
    }



    fun deleteAPI(URL : String, activity: Activity, linear: ConstraintLayout):String{
        var res = "[]"
        val storage = Storage(activity)

        try {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url(URL)
                .delete()
                .addHeader("content-type", "application/json")
                .addHeader("Authorization", storage.tokenId!!)
                .build()


            val response = client.newCall(request).execute()

            res= (response.body?.string().toString())
            // dialog.dismiss()
        }catch (e:Exception){
            e.printStackTrace()
            // dialog.dismiss()
        }


        return res
    }
}