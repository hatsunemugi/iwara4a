package com.ero.iwara.ui.screen.login

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.ero.iwara.R
import com.ero.iwara.ui.public.FullScreenTopBar
import com.ero.iwara.util.send
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.iconTitle
import com.vanpra.composematerialdialogs.message
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import com.vanpra.composematerialdialogs.title

@Composable
fun LoginScreen(navController: NavController, loginViewModel: LoginViewModel = hiltViewModel()) {
    Scaffold(
        topBar = {
            TopBar(navController)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentAlignment = Alignment.Center
        ) {
            Content(loginViewModel, navController)
        }
    }
}
@Composable
private fun Content(loginViewModel: LoginViewModel, navController: NavController) {
    val context = LocalContext.current
    var showPassword by remember {
        mutableStateOf(false)
    }
    val progressState = rememberMaterialDialogState(false)
    val failureState = rememberMaterialDialogState(false)
    // 登录进度对话框
    MaterialDialog(progressState){
        iconTitle(
            text = "登录中",
            icon = { CircularProgressIndicator(Modifier.size(30.dp)) }
        )
        message("请稍等片刻")
    }
    // 登录失败
    MaterialDialog(failureState){
        title("登录失败")
        message("请检查你的用户名和密码是否正确，如果确定准确，请再次重试登录")
        message("错误内容: ${loginViewModel.errorContent}")
        message("(别忘记挂梯子！)")
        TextButton({ failureState.hide() }) {
            Text("好的")
        }
    }

    // 内容
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        // LOGO
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(R.drawable.miku),
                contentScale = ContentScale.FillWidth,
                contentDescription = null
            )
        }

        // Spacer
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(25.dp)
        )

        // Username
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = loginViewModel.email,
            onValueChange = { loginViewModel.email = it },
            label = {
                Text(
                    text = "用户名"
                )
            },
            singleLine = true
        )

        // Password
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = loginViewModel.password,
            onValueChange = { loginViewModel.password = it },
            label = {
                Text(
                    text = "密码"
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            trailingIcon = {
                Crossfade(targetState = showPassword) {
                    IconButton(onClick = {
                        showPassword = !showPassword
                    }) {
                        Icon(
                            if (it) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            null
                        )
                    }
                }
            }
        )

        // Spacer
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
        )

        // Login
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (loginViewModel.email.isBlank() || loginViewModel.password.isBlank()) {
                    send("用户名或密码不能为空")
                    return@Button
                }

                progressState.show()
                loginViewModel.login {
                    // 处理结果
                    if (it) {
                        // 登录成功
                        send("登录成功-${loginViewModel.token.length}-${loginViewModel.accessToken.length}")
                        navController.navigate("index"){
                            popUpTo("login"){
                                inclusive = true
                            }
                        }
                    } else {
                        // 登录失败
                        failureState.show()
                    }
                    progressState.hide()
                }
            }
        ) {
            Text(text = "登录")
        }

        // Spacer
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )

        Row {
            // Register
            OutlinedButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        "https://www.iwara.tv/user/register".toUri()
                    )
                    context.startActivity(intent)
                }
            ) {
                Text(text = "注册账号")
            }
        }
    }
}


@Composable
private fun TopBar(navController: NavController) {
    FullScreenTopBar(
        modifier = Modifier.statusBarsPadding().height(56.dp),
        title = {
            Text(text = "登录账号")
        }
    )
}