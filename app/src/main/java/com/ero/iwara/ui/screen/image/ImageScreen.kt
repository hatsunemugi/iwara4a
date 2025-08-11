package com.ero.iwara.ui.screen.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import com.ero.iwara.R
import com.ero.iwara.model.detail.image.ImageDetail
import com.ero.iwara.ui.public.FullScreenTopBar
import com.ero.iwara.util.noRippleClickable


@Composable
fun ImageScreen(
    navController: NavController,
    imageId: String,
    imageViewModel: ImageViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        imageViewModel.load(imageId)
    }
    Scaffold(topBar = {
        FullScreenTopBar(
            modifier = Modifier.height(32.dp),
            title = {
                Text(text = if (imageViewModel.imageDetail != ImageDetail.LOADING && !imageViewModel.isLoading && !imageViewModel.error) imageViewModel.imageDetail.title else "浏览图片")
            },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                }
            }
        )
    }) {
        if (imageViewModel.error) {
            Box(modifier = Modifier.fillMaxSize().padding(it), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier
                        .size(160.dp)
                        .noRippleClickable {
                            imageViewModel.load(imageId)
                        }
                        .padding(10.dp)
                        .clip(CircleShape)) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(R.drawable.anime_3),
                            contentDescription = null
                        )
                    }
                    Text(text = "加载失败，点击重试~ （土豆服务器日常）", fontWeight = FontWeight.Bold)
                }
            }
        } else if (imageViewModel.isLoading || imageViewModel.imageDetail == ImageDetail.LOADING) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text(text = "加载中", fontWeight = FontWeight.Bold)
                }
            }
        } else {
            ImagePage(imageViewModel.imageDetail)
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun ImagePage(imageDetail: ImageDetail) {
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(initialPage = 0, pageCount = { imageDetail.imageLinks.size })
    Column(Modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
            ){
                AsyncImage(model =  imageDetail.imageLinks[pagerState.currentPage], modifier = Modifier.fillMaxWidth(), contentDescription = null, contentScale = ContentScale.FillWidth)
            }
        }
        Card(modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(16.dp)) {
            Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                ) {
                    AsyncImage(
                        model = imageDetail.authorProfilePic,
                        modifier = Modifier.fillMaxSize(),
                        contentDescription = null
                    )
                }

                Text(modifier = Modifier.padding(horizontal = 16.dp), text = imageDetail.authorId, fontWeight = FontWeight.Bold, fontSize = 25.sp)
            }
        }
    }
}