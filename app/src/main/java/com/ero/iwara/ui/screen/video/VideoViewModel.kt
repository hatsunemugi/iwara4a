package com.ero.iwara.ui.screen.video

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.ero.iwara.api.paging.CommentSource
import com.ero.iwara.model.detail.video.VideoDetail
import com.ero.iwara.model.index.MediaType
import com.ero.iwara.stroage.Config
import com.ero.iwara.repo.MediaRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class VideoViewModel @Inject constructor(
    private val config: Config,
    private val mediaRepo: MediaRepo
): ViewModel() {
    val videoId = MutableStateFlow("")
    val isLoading = MutableStateFlow(false)
    val error = MutableStateFlow(false)
    val videoDetail = MutableStateFlow(VideoDetail.LOADING)
    val commentPager by lazy {
        Pager(
            config = PagingConfig(
                pageSize = 30,
                initialLoadSize = 30
            )
        ) {
            CommentSource(
                mediaRepo = mediaRepo,
                mediaType = MediaType.VIDEO,
                authorId = videoDetail.value.authorId,
                mediaId = videoDetail.value.id
            )
        }.flow.cachedIn(viewModelScope)
    }

    fun loadVideo(id: String){
        if(videoDetail.value != VideoDetail.LOADING){
            return
        }

        viewModelScope.launch {
            videoId.update { id }
            isLoading.update { true }
            error.update { false }

            val response = mediaRepo.getVideoDetail(id)
            if(response.isSuccess()){
                videoDetail.update { response.read() }
            }else {
                error.update { true }
            }

            isLoading.update { false }
        }
    }

    fun handleLike(result: (action: Boolean, success: Boolean) -> Unit){
        val action = !videoDetail.value.isLike
        viewModelScope.launch {
            val response = mediaRepo.like(action, videoDetail.value.likeLink)
            val success = response.isSuccess()
            if(success){
                videoDetail.update { it.copy(isLike = response.read().status) }
            }
            result(action, success)
        }
    }

    fun handleFollow(result: (action: Boolean, success: Boolean) -> Unit){
        val action = !videoDetail.value.follow
        viewModelScope.launch {
            val response = mediaRepo.follow(action, videoDetail.value.followLink)
            val success = response.isSuccess()
            if(success){
                videoDetail.update { it.copy(follow = response.read().status) }
            }
            result(action, success)
        }
    }
}