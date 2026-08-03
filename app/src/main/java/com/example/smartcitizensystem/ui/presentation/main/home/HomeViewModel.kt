package com.example.smartcitizensystem.ui.presentation.main.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcitizensystem.data.api.RetrofitInstance
import com.example.smartcitizensystem.data.models.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    // Using mutableStateOf for Compose integration without Flow
    private val _posts = mutableStateOf<List<Post>>(emptyList())
    val posts: State<List<Post>> = _posts

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitInstance.api.getPosts()
                _posts.value = response.take(10) // Get first 10 posts
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to fetch posts"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchTodos() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitInstance.api.getTodos()
                _posts.value = response.map { todo ->
                    Post(
                        userId = todo.userId,
                        id = todo.id,
                        title = todo.title,
                        body = if (todo.completed) "✅ Completed" else "❌ Pending"
                    )
                }.take(10)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to fetch todos"
            } finally {
                _isLoading.value = false
            }
        }
    }
}