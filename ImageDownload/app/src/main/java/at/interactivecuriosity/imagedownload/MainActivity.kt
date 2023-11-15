package at.interactivecuriosity.imagedownload

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import at.interactivecuriosity.imagedownload.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var downloadButton: Button
    private lateinit var deleteButton: Button
    private val imageUrl = "https://www.markusmaurer.at/fhj/eyecatcher.jpg" // URL des herunterzuladenden Bildes
    private val fileName = "downloadedImage.jpg"
    private lateinit var receiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        downloadButton = findViewById(R.id.downloadButton)
        deleteButton = findViewById(R.id.deleteButton)

        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    "DownloadComplete" -> {
                        val filePath = intent.getStringExtra("bitmapFilePath")
                        if (filePath != null) {
                            val bitmap = BitmapFactory.decodeFile(filePath)
                            imageView.setImageBitmap(bitmap)
                        } else {
                            Toast.makeText(this@MainActivity, "Fehler beim Herunterladen", Toast.LENGTH_LONG).show()
                        }
                        Toast.makeText(this@MainActivity, "Bild heruntergeladen", Toast.LENGTH_SHORT).show()
                    }
                    "DownloadFailed" -> {
                        Toast.makeText(this@MainActivity, "Fehler beim Herunterladen", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        downloadButton.setOnClickListener {
            downloadImage(imageUrl, fileName)
        }

        deleteButton.setOnClickListener {
            deleteImage(fileName)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun downloadImage(urlString: String, fileName: String) {
        Toast.makeText(this, "Bild wird heruntergeladen", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DownloadService::class.java)
        intent.putExtra("urlString", urlString)
        intent.putExtra("fileName", fileName)
        startService(intent)

        val filter = IntentFilter().apply {
            addAction("DownloadComplete")
            addAction("DownloadFailed")
        }

        registerReceiver(receiver, filter)
    }

    private fun deleteImage(fileName: String) {
        val file = File(getExternalFilesDir(null), fileName)
        if (file.exists()) {
            file.delete()
            runOnUiThread {
                imageView.setImageBitmap(null)
                Toast.makeText(this, "Bild gelöscht", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
