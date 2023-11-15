package at.interactivecuriosity.imagedownload

import android.content.Context
import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class BitmapUtils {
    companion object {
        fun saveBitmapLocally(bitmap: Bitmap, fileName: String, context: Context): String {
            val directory = context.getExternalFilesDir(null)
            val file = File(directory, fileName)

            try {
                val stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                stream.flush()
                stream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return file.absolutePath
        }
    }
}