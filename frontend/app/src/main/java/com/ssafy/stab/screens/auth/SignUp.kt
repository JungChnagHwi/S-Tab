package com.ssafy.stab.screens.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.ssafy.stab.R
import com.ssafy.stab.apis.auth.checkNickName
import com.ssafy.stab.apis.auth.s3uri
import com.ssafy.stab.apis.auth.signUp
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import com.ssafy.stab.BuildConfig
import java.io.IOException
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import coil.compose.rememberAsyncImagePainter

@Composable
fun SignUp(onNavigate: (String) -> Unit) {
    var nickname by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var nickNameAvailable by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.signup_bg),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // 프로필 이미지와 사진선택 칸 분리
            Column (
                modifier = Modifier
                    .offset(x = (-200).dp, y = (100).dp)
            ){
                val basicProfileUrl = BuildConfig.BASE_S3 + "/image/2024/05/08/3454673260/profileImage.png"
                val basicProfileImg = rememberAsyncImagePainter(model = basicProfileUrl)
                
                if (imageUri != null) {
                    ImagePreview(imageUri = imageUri, modifier = Modifier.size(200.dp))                } else {
                    Image(
                        painter = basicProfileImg,
                        contentDescription = "기본 프로필 이미지",
                        modifier = Modifier
                            .size(200.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val intent = Intent(Intent.ACTION_PICK)
                        intent.type = "image/*"
                        pickImageLauncher.launch(intent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray),
                    modifier = Modifier
                        .width(200.dp)  // 버튼 너비 조절
                        .height(50.dp)

                ) {
                    Text(
                        "프로필 사진 선택",
                        color = Color.Black,
                        fontSize = 20.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .offset(x = (-155).dp, y = (150).dp)
            ) {

                OutlinedTextField(
                    value = nickname,
                    onValueChange = {newValue ->
                        if (newValue.isEmpty() || newValue.first() != ' ') {
                            nickname = newValue
                        }
                    },
                    placeholder = { Text("닉네임", fontSize = 16.sp, textAlign = TextAlign.Center) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .background(Color.White)
                        .border(0.dp, SolidColor(Color.White), RoundedCornerShape(8.dp))
                        .width(200.dp)
                        .height(56.dp)
                        .offset(x = (0).dp, y = (0).dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.padding(5.dp))

                Button(
                    onClick = {
                        checkNickName(nickname) { available ->
                            nickNameAvailable = available
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF86E2FF)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .width(86.dp)
                        .height(56.dp)
                        .offset(x = (0).dp, y = (0).dp)
                        .padding(horizontal = 0.dp, vertical = 0.dp)
                ) {
                    Text(
                        "중복 확인",
                        color = Color.Black,
                        fontSize = 8.sp,
                        maxLines = 1,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF86E2FF),
//                    disabledContainerColor = Color(0xFF86E2FF)
                ),
                onClick = {
                    s3uri(context, Uri.parse(imageUri.toString()), nickname)
                    onNavigate("space")
                },
                enabled = nickNameAvailable,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(300.dp)  // 너비 조절
                    .height(56.dp)
                    .offset(x = (-150).dp, y = (150).dp)
            ) {
                Text("회원가입 완료", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}



fun uploadFile(context: Context, url: String, imageUri: Uri, nickname: String) {
    val client = OkHttpClient()
    Log.d("a", imageUri.toString())
    if (imageUri.toString() != "null") {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val mediaType = "image/jpeg".toMediaType()

        val body = inputStream?.use { stream ->
            // inputStream에서 바이트 배열을 읽고 RequestBody를 생성
            RequestBody.create(mediaType, stream.readBytes())
        }

        if (body != null) {  // Body가 null이 아니면 요청 실행
            val request = Request.Builder()
                .url(url)
                .put(body)  // HTTP PUT 요청을 사용
                .build()

            client.newCall(request).enqueue(object : okhttp3.Callback {
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    Log.e("Upload", "Upload failed", e)
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    if (response.isSuccessful) {
                        val fullUrl = response.request.url.toString()
                        val baseUrl = fullUrl.split("?").first()
                        Log.d("Upload", "Base URL: $baseUrl")
                        Log.d("Upload", "Upload was successful")
                        signUp(nickname, baseUrl)
                    } else {
                        Log.e("Upload", "Upload failed: ${response.message}")
                    }
                    response.close()  // 응답 리소스 해제
                }
            })
        } else {
            Log.e("UploadError", "Failed to create request body from the input stream.")
        }
    } else {
        val baseImage = BuildConfig.BASE_S3 + "/image/2024/05/08/3454673260/profileImage.png"
        signUp(nickname, baseImage)
    }
}

@Composable
fun ImagePreview(imageUri: Uri?, modifier: Modifier = Modifier) {
    imageUri?.let {
        val painter = rememberImagePainter(
            data = it,
            builder = {
                crossfade(true)
            }
        )
        Image(
            painter = painter,
            contentDescription = null,  // 이미지에 대한 설명이 필요 없으므로 null을 할당
            modifier = modifier
                .size(200.dp)
                .clip(CircleShape),  // 이미지 원형으로 설정
            contentScale = ContentScale.Crop
        )
    }
}
