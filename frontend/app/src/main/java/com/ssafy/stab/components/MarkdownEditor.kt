package com.ssafy.stab.components

import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
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
fun MarkdownScreen() {
    var markdownText by remember { mutableStateOf("") }
    var textAlign by remember { mutableStateOf("left") }
    var isEditing by remember { mutableStateOf(true) } // 편집 상태 관리
    val scrollState = rememberScrollState() // 스크롤 상태를 기억

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
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { isEditing = false }) {
                        Text("완료")
                    }
                }
            }
        } else {
            Column {
                Row {
                    Button(onClick = { textAlign = "left" }) {
                        Text("Left Align")
                    }
                    Button(onClick = { textAlign = "center" }) {
                        Text("Center Align")
                    }
                    Button(onClick = { textAlign = "right" }) {
                        Text("Right Align")
                    }
                }
                Column(modifier = Modifier
                    .height(300.dp) // 높이 고정
                    .verticalScroll(scrollState)) {
                    MarkdownViewer(htmlContent = parseMarkdownToHtml(markdownText, textAlign),)
                }
                Button(onClick = { isEditing = true }) {
                    Text("수정")
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
        label = { Text("Enter Markdown") },
        modifier = Modifier
            .fillMaxWidth()
    )
}


