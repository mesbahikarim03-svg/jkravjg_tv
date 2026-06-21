package com.example.internetdownloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.internetdownloader.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.downloadButton.setOnClickListener {
            val url = binding.urlInput.text.toString().trim()

            if (url.isEmpty()) {
                Toast.makeText(this, "أدخل رابط صحيح", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            downloadFile(url)
        }
    }

    private fun downloadFile(url: String) {
        try {
            val request = DownloadManager.Request(Uri.parse(url))
            request.setTitle("Downloading File")
            request.setDescription("Downloading...")
            request.setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            request.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                Uri.parse(url).lastPathSegment
            )

            val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            manager.enqueue(request)

            Toast.makeText(this, "بدأ التحميل ✅", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "خطأ في الرابط ❌", Toast.LENGTH_LONG).show()
        }
    }
}