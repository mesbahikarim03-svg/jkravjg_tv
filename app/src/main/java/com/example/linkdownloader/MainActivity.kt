package com.example.linkdownloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val urlInput = findViewById<EditText>(R.id.urlInput)
        val downloadButton = findViewById<Button>(R.id.downloadButton)

        downloadButton.setOnClickListener {
            val url = urlInput.text.toString().trim()

            if (url.isEmpty()) {
                Toast.makeText(this, "أدخل الرابط أولاً", Toast.LENGTH_SHORT).show()
            } else {
                downloadFile(url)
            }
        }
    }

    private fun downloadFile(url: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setTitle("Downloading file")
            request.setDescription("Downloading from link")
            request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                Uri.parse(url).lastPathSegment
            )

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)

            Toast.makeText(this, "بدأ التحميل...", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "رابط غير صالح", Toast.LENGTH_SHORT).show()
        }
    }
}