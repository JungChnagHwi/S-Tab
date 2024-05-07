package com.ssafy.stab.screens.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ssafy.stab.apis.auth.s3uri
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import java.io.IOException
@Composable
fun SignUp(onNavigate: (String) -> Unit) {
    var nickname by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // 이미지 선택을 위한 ActivityResultLauncher
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data.toString()  // URI를 문자열로 저장
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "회원가입 페이지", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("닉네임") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }) {
            Text("프로필 사진 선택")
        }
        imageUri?.let {
            Text("선택된 이미지 URI: $it")
            Log.d("Selected Img", it)
        }
        Button(onClick = {
            if (imageUri != null) {
                s3uri(context, Uri.parse(imageUri))
            }
        }) {
            Text(text = "S3 URI 줘봐")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = { onNavigate("login") }) {
                Text(text = "로그인 페이지로 가기")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = { onNavigate("space") }) {
                Text(text = "개인 스페이스로 가기")
            }
        }
    }
}



fun uploadFile(context: Context, url: String, imageUri: Uri) {
    val client = OkHttpClient()
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
                    Log.d("Upload", "Upload was successful")
                } else {
                    Log.e("Upload", "Upload failed: ${response.message}")
                }
                response.close()  // 응답 리소스 해제
            }
        })
    } else {
        Log.e("UploadError", "Failed to create request body from the input stream.")
    }
}