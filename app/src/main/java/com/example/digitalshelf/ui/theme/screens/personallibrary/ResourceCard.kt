package com.example.digitalshelf.ui.theme.screens.personallibrary

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentComposer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.digitalshelf.R
import com.example.digitalshelf.models.Resource
import com.example.digitalshelf.viewmodels.PersonalLibraryViewModel





@Composable
fun ResourceCard(resource: Resource) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                resource.fileUrl?.let { url ->
                    Image(
                        painter = rememberImagePainter(url),
                        contentDescription = "Resource Image",
                        modifier = Modifier.fillMaxSize()
                    )
                } ?: run {
                    Text("Image Placeholder", color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = resource.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = resource.description,
                fontSize = 14.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { handleDownload(resource.fileUrl, context) },
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("Download")
            }
        }
    }
}

fun handleDownload(fileUrl: String?, context: Context) {
    if (fileUrl != null) {
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(fileUrl)
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to open file", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Invalid file URL", Toast.LENGTH_SHORT).show()
    }
}
