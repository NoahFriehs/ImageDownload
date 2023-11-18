package at.interactivecuriosity.imagedownload

import android.app.IntentService
import android.content.Intent
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DownloadService : IntentService("DownloadService") {

    @Deprecated("Deprecated in Java")
    override fun onHandleIntent(intent: Intent?) {
        val urlString = intent!!.getStringExtra("urlString")!!
        val fileName = intent.getStringExtra("fileName")!!

        Log.i("DownloadService", "DownloadService gestartet mit $urlString und $fileName")

        try {
            val url = URL(urlString)
            val connection = url.openConnection()
            connection.connect()
            val inputStream = connection.getInputStream()
            val file = File(getExternalFilesDir(null), fileName)
            FileOutputStream(file).use { output ->
                inputStream.copyTo(output)
            }
            // Sende Broadcast, um das Ergebnis zurückzugeben
            val resultIntent = Intent("DownloadComplete")
            resultIntent.putExtra("bitmapFilePath", file.absolutePath)
            sendBroadcast(resultIntent)

        } catch (e: Exception) {
            e.printStackTrace()

            // Sende Broadcast, um den Fehler zurückzugeben
            val resultIntent = Intent("DownloadFailed")
            sendBroadcast(resultIntent)
        }
    }
}
