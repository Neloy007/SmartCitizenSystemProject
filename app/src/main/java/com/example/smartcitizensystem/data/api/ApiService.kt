package com.example.smartcitizensystem.data.api

import com.example.smartcitizensystem.data.models.Comment
import com.example.smartcitizensystem.data.models.Photo
import com.example.smartcitizensystem.data.models.Post
import com.example.smartcitizensystem.data.models.Todo
import com.example.smartcitizensystem.data.models.User
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<Post>

    @GET("posts/{id}")
    suspend fun getPost(@Path("id") id: Int): Post

    @GET("posts/{postId}/comments")
    suspend fun getComments(@Path("postId") postId: Int): List<Comment>

    @GET("todos")
    suspend fun getTodos(): List<Todo>

    @GET("todos/{id}")
    suspend fun getTodo(@Path("id") id: Int): Todo

    @GET("users")
    suspend fun getUsers(): List<User>

    // ✅ Add this endpoint
    @GET("photos")
    suspend fun getPhotos(): List<Photo>
}