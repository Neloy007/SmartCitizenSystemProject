package com.example.smartcitizensystem.ui.presentation.main.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartcitizensystem.data.api.RetrofitInstance
import com.example.smartcitizensystem.data.models.MinistryPost
import com.example.smartcitizensystem.data.models.Photo
import com.example.smartcitizensystem.data.models.Post
import com.example.smartcitizensystem.data.models.SocialPost
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    // Regular posts
    private val _posts = mutableStateOf<List<Post>>(emptyList())
    val posts: State<List<Post>> = _posts

    // Social feed posts with images
    private val _socialPosts = mutableStateOf<List<SocialPost>>(emptyList())
    val socialPosts: State<List<SocialPost>> = _socialPosts

    // Ministry posts from your backend
    private val _ministryPosts = mutableStateOf<List<MinistryPost>>(emptyList())
    val ministryPosts: State<List<MinistryPost>> = _ministryPosts

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

//    //  Fetch from  backend
//    fun fetchMinistryFeed() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _error.value = null
//            try {
//                Log.d(TAG, "Fetching ministry feed...")
//                val response = RetrofitInstance.ministryApi.getMinistryPosts()
//                Log.d(TAG, "Response: success=${response.success}, count=${response.count}")
//                if (response.success) {
//                    _ministryPosts.value = response.data
//                } else {
//                    _error.value = "Failed to fetch ministry posts"
//                }
//            } catch (e: Exception) {
//                Log.e(TAG, "Error fetching ministry feed: ${e.message}", e)
//                _error.value = e.message ?: "Failed to fetch feed"
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }



    fun fetchSocialFeed() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                Log.d(TAG, "Fetching social feed from JSONPlaceholder...")

                // Fetch posts and photos in parallel
                val postsDeferred = async { RetrofitInstance.api.getPosts() }
                val photosDeferred = async { RetrofitInstance.api.getPhotos() }

                val posts = postsDeferred.await()
                val photos = photosDeferred.await()

                // Combine posts with photos
                val combinedPosts = posts.mapIndexed { index, post ->
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
                Log.d(TAG, "Fetched ${combinedPosts.size} posts with images")

            } catch (e: Exception) {
                Log.e(TAG, "Error fetching social feed: ${e.message}", e)
                _error.value = e.message ?: "Failed to fetch feed"
            } finally {
                _isLoading.value = false
            }
        }
    }

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