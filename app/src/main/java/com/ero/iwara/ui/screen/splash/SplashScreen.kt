package com.ero.iwara.ui.screen.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ero.iwara.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController, splashViewModel: SplashViewModel = hiltViewModel()){
    Box(modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colorScheme.onPrimaryContainer), contentAlignment = Alignment.Center){

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)){
                Image(modifier = Modifier.fillMaxSize(), painter = painterResource(R.drawable.logo), contentDescription = null)
            }
            Text(text = "IWARA", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = "ecchi.iwara.tv", fontSize = 20.sp, color = Color.White)
        }
    }
    LaunchedEffect(Unit){
        delay(1000L)

        // 前往主页
        if(splashViewModel.isLogin()) {
            navController.navigate("index") {
                popUpTo("splash") {
                    inclusive = true
                }
            }
        } else {
            // 登录
            navController.navigate("login") {
                popUpTo("splash") {
                    inclusive = true
                }
            }
        }
    }
}