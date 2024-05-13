package com.ssafy.stab.components

import android.util.Log
import android.webkit.WebView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.ssafy.stab.R
import com.ssafy.stab.apis.space.share.getMarkdown
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer


fun parseMarkdownToHtml(markdown: String, textAlign: String): String {
    val parser = Parser.builder().build()
    val document = parser.parse(markdown)
    val renderer = HtmlRenderer.builder().build()
    val htmlContent = renderer.render(document)
    return "<div style='text-align:$textAlign;'>$htmlContent</div>"
}

@Composable
fun MarkdownViewer(htmlContent: String) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
        }
    )
}

@Composable
fun MarkdownScreen(spaceId: String) {
    var markdownText by remember { mutableStateOf("") }
    var textAlign by remember { mutableStateOf("left") }
    var isEditing by remember { mutableStateOf(false) } // 편집 상태 관리
    val scrollState = rememberScrollState() // 스크롤 상태를 기억
    val leftImg = painterResource(id = R.drawable.left_align)
    val centerImg = painterResource(id = R.drawable.center_align)
    val rightImg = painterResource(id = R.drawable.right_align)
    
    LaunchedEffect(key1 = true) {
        getMarkdown(spaceId){
            res -> Log.d("BB", res.data)
            markdownText = res.data
        }
    }


    Column(modifier = Modifier.fillMaxSize()) {
        if (isEditing) {
            Column(
                modifier = Modifier
                    .height(500.dp) // 높이 고정
                    .verticalScroll(scrollState) // 세로 스크롤 활성화
            ) {
                MarkdownEditor(
                    onMarkdownChange = { markdownText = it },
                    markdownText = markdownText
                )
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp),
                    horizontalArrangement = Arrangement.End) {
                    Box(modifier = Modifier
                        .clickable { isEditing = false }
                        .clip(RoundedCornerShape(10.dp))
                        .background(color = Color(0xFFDCE3F1))
                        .padding(5.dp)
                    ) {
                        Text("완료")
                    }
                    Spacer(modifier = Modifier.width(40.dp))
                }
            }
        } else {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.width(20.dp))
                    Row {
                        Image(painter = leftImg, contentDescription = null,
                            modifier = Modifier
                                .clickable { textAlign = "left" }
                                .height(20.dp)
                                .width(20.dp))
                        Spacer(modifier = Modifier.width(20.dp))
                        Image(painter = centerImg, contentDescription = null,
                            modifier = Modifier
                                .clickable { textAlign = "center" }
                                .height(20.dp)
                                .width(20.dp))
                        Spacer(modifier = Modifier.width(20.dp))
                        Image(painter = rightImg, contentDescription = null,
                            modifier = Modifier
                                .clickable { textAlign = "right" }
                                .height(20.dp)
                                .width(20.dp))
                        Spacer(modifier = Modifier.width(50.dp))
                    }
                }
                Column(modifier = Modifier
                    .clickable { isEditing = true }
                    .height(300.dp) // 높이 고정
                    .verticalScroll(scrollState)
                ) {
                    MarkdownViewer(htmlContent = parseMarkdownToHtml(markdownText, textAlign),)
                }
            }
        }
        Divider(color = Color.Gray, thickness = 1.dp)
    }
}

@Composable
fun MarkdownEditor(onMarkdownChange: (String) -> Unit, markdownText: String) {
    TextField(
        value = markdownText,
        onValueChange = onMarkdownChange,
        label = { Text("표지 작성") },
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
    )
}


