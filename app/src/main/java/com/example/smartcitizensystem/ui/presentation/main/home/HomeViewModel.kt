package com.example.smartcitizensystem.ui.presentation.main.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcitizensystem.data.api.RetrofitInstance
import com.example.smartcitizensystem.data.models.Photo
import com.example.smartcitizensystem.data.models.Post
import com.example.smartcitizensystem.data.models.SocialPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    // Regular posts
    private val _posts = mutableStateOf<List<Post>>(emptyList())
    val posts: State<List<Post>> = _posts

    // Social feed posts with images
    private val _socialPosts = mutableStateOf<List<SocialPost>>(emptyList())
    val socialPosts: State<List<SocialPost>> = _socialPosts

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    // Fetch social feed with images
    fun fetchSocialFeed() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Fetch posts and photos in parallel for better performance
                val postsDeferred = async { RetrofitInstance.api.getPosts() }
                val photosDeferred = async { RetrofitInstance.api.getPhotos() }

                val posts = postsDeferred.await()
                val photos = photosDeferred.await()

                // Combine posts with photos
                val combinedPosts = posts.mapIndexed { index, post ->
                    // Use post.id to get a consistent photo
                    val photoIndex = (post.id - 1) % photos.size
                    val photo = if (photos.isNotEmpty()) photos[photoIndex] else null

                    SocialPost(
                        id = post.id,
                        userId = post.userId,
                        title = post.title,
                        body = post.body,
                        imageUrl = photo?.url,
                        thumbnailUrl = photo?.thumbnailUrl,
                        userName = "User ${post.userId}",
                        userAvatar = "https://i.pravatar.cc/150?img=${post.userId}"
                    )
                }

                _socialPosts.value = combinedPosts
                _posts.value = posts
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to fetch feed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Original fetchPosts method
    fun fetchPosts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                val response = RetrofitInstance.api.getPosts()
                _posts.value = response.take(10)
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

