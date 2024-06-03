package ardiansyah.dwinur.aldy.koperasi

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MediaHelper(context : Context) {
    val context = context
    var namaFile = ""
    var fileUri = Uri.parse("")
    val RC_CAMERA = 100

    fun getRcGallery() : Int {
        return REQ_CODE_GALLERY
    }

    fun getMyFileName() : String {
        return this.namaFile
    }

    fun getRcCamera() : Int {
        return this.RC_CAMERA
    }

    // buat direktori bila belum ada
    fun getOutputMediaFile() : File {
        val mediaStorageDir = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "appx0b")
        if(!mediaStorageDir.exists()) { // cek apakah direktori belum ada
            if(!mediaStorageDir.mkdirs()) { // coba buat direktori
                Log.e("mkdir", "Gagal membuat direktori")
            }
        }
        val mediaFile = File(mediaStorageDir.path + File.separator + "${this.namaFile}")
        return mediaFile
    }

    // siapkan nama file
    fun getOutputMediaFileUri() : Uri {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmss",
            Locale.getDefault()).format(Date())
        this.namaFile = "DC_${timeStamp}.jpg"
        this.fileUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", getOutputMediaFile())
        Log.i("fileUri", this.fileUri.path.toString())
        return this.fileUri
    }

    fun bitmapToString(bmp : Bitmap) : String {
        val outputStream = ByteArrayOutputStream()

        // kompresi gambar sampai 60%
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun getBitmapToString(uri : Uri, imv : ImageView) : String {
        var bmp = MediaStore.Images.Media.getBitmap(
            this.context.contentResolver, uri)
        var dim = 720
        if(bmp.height > bmp.width) {
            bmp = Bitmap.createScaledBitmap(bmp,
                (bmp.width*dim).div(bmp.height), dim, true)
        } else {
            bmp = Bitmap.createScaledBitmap(bmp,
                dim, (bmp.height*dim).div(bmp.width), true)
        }
        imv.setImageBitmap(bmp)
        return bitmapToString(bmp)
    }

    companion object {
        const val REQ_CODE_GALLERY = 100
    }
}