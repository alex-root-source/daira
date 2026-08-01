package com.daira.circle.ui.screens

import android.content.Intent
import android.net.Uri
import android.text.format.DateFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.daira.circle.data.firestore.ChatMessage
import com.daira.circle.data.firestore.FriendEntry
import com.daira.circle.ui.theme.Peach
import com.daira.circle.ui.theme.Surface
import com.daira.circle.ui.theme.Surface2
import com.daira.circle.ui.theme.TextMuted

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatDetailScreen(
    friend: FriendEntry,
    myUid: String,
    messages: List<ChatMessage>,
    isUploading: Boolean,
    mediaError: String?,
    onDismissError: () -> Unit,
    onBack: () -> Unit,
    onSend: (String) -> Unit,
    onSendMedia: (Uri, String) -> Unit,
    onDelete: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            val mimeType = context.contentResolver.getType(uri) ?: ""
            val mediaType = if (mimeType.startsWith("video")) "video" else "image"
            onSendMedia(uri, mediaType)
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    val snackbarHostState = remember { androidx.compose.material3.SnackbarHostState() }
    LaunchedEffect(mediaError) {
        if (mediaError != null) {
            snackbarHostState.showSnackbar(mediaError)
            onDismissError()
        }
    }

    androidx.compose.material3.Scaffold(
        snackbarHost = { androidx.compose.material3.SnackbarHost(snackbarHostState) },
        containerColor = androidx.compose.ui.graphics.Color.Transparent
    ) { scaffoldPadding ->
    Column(Modifier.fillMaxSize().padding(scaffoldPadding)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowForward, contentDescription = "رجوع", tint = Color.White)
            }
            Box(
                Modifier.size(36.dp).background(Surface2, CircleShape),
                contentAlignment = Alignment.Center
            ) { Text(friend.initials, color = Color.White) }
            Text(
                friend.displayName,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                MessageBubble(
                    message = msg,
                    isMine = msg.senderUid == myUid,
                    onCopy = { clipboard.setText(AnnotatedString(msg.text)) },
                    onDelete = { onDelete(msg.id) }
                )
            }
        }

        if (isUploading) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp, color = Peach)
                Text("  جارٍ رفع الملف...", style = androidx.compose.material3.MaterialTheme.typography.labelSmall, color = TextMuted)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                mediaPicker.launch(
                    androidx.activity.result.PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageAndVideo
                    )
                )
            }) {
                Icon(Icons.Filled.AttachFile, contentDescription = "إرفاق صورة أو فيديو", tint = TextMuted)
            }
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("اكتب رسالة...") },
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Peach, cursorColor = Peach
                )
            )
            IconButton(onClick = {
                if (text.isNotBlank()) {
                    onSend(text.trim())
                    text = ""
                }
            }) {
                Icon(Icons.Filled.Send, contentDescription = "إرسال", tint = Peach)
            }
        }
    }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MessageBubble(
    message: ChatMessage,
    isMine: Boolean,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val timeText = remember(message.timestampMillis) {
        DateFormat.format("h:mm a", message.timestampMillis).toString()
    }
    val hasMedia = message.mediaUrl.isNotBlank()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.Start else Arrangement.End
    ) {
        Column(horizontalAlignment = if (isMine) Alignment.Start else Alignment.End) {
            Box {
                Box(
                    modifier = Modifier
                        .background(
                            if (hasMedia) Color.Transparent else if (isMine) Peach else Surface,
                            RoundedCornerShape(16.dp)
                        )
                        .combinedClickable(
                            onClick = {
                                if (message.mediaType == "video") {
                                    val intent = Intent(Intent.ACTION_VIEW).apply {
                                        setDataAndType(Uri.parse(message.mediaUrl), "video/*")
                                    }
                                    context.startActivity(intent)
                                }
                            },
                            onLongClick = { showMenu = true }
                        )
                        .padding(if (hasMedia) 0.dp else 14.dp, if (hasMedia) 0.dp else 10.dp)
                ) {
                    when {
                        message.mediaType == "image" -> AsyncImage(
                            model = message.mediaUrl,
                            contentDescription = "صورة",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .width(200.dp)
                                .height(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                        message.mediaType == "video" -> Box(
                            modifier = Modifier
                                .width(200.dp)
                                .height(220.dp)
                                .background(Surface2, RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "تشغيل الفيديو", tint = Peach, modifier = Modifier.size(44.dp))
                        }
                        else -> Text(
                            message.text,
                            color = if (isMine) Color(0xFF241A16) else Color.White,
                            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                    if (!hasMedia) {
                        DropdownMenuItem(text = { Text("نسخ") }, onClick = { onCopy(); showMenu = false })
                    }
                    if (isMine) {
                        DropdownMenuItem(text = { Text("حذف") }, onClick = { onDelete(); showMenu = false })
                    }
                }
            }

            Row(
                modifier = Modifier.padding(top = 3.dp, start = 4.dp, end = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(timeText, style = androidx.compose.material3.MaterialTheme.typography.labelSmall, color = TextMuted)
                if (isMine) {
                    Icon(
                        imageVector = if (message.read) Icons.Filled.DoneAll else Icons.Filled.Done,
                        contentDescription = if (message.read) "تمت القراءة" else "تم الإرسال",
                        tint = if (message.read) Peach else TextMuted,
                        modifier = Modifier.padding(start = 4.dp).size(13.dp)
                    )
                }
            }
        }
    }
}
