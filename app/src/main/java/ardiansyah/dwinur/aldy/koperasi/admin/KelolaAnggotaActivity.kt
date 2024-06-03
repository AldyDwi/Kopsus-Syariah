package ardiansyah.dwinur.aldy.koperasi.admin

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ardiansyah.dwinur.aldy.koperasi.MediaHelper
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivityKelolaAnggotaBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.permissionx.guolindev.PermissionX
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class KelolaAnggotaActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var b: ActivityKelolaAnggotaBinding

    val url = "http://192.168.241.103/koperasi/koperasi.php"
    lateinit var adapter : AdapterAnggota
    lateinit var mediaHelper : MediaHelper

    private val PERMISSION_REQUEST_CODE = 101

    var imStr = ""
    var fileUri = Uri.parse("")
    var namafile = ""

    // Mengatur Tanggal dan Jam menggunakan Calendar
    val cal: Calendar = Calendar.getInstance()

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnTglLahirAnggota -> showDialog(100)
            R.id.btnInsAnggota -> {
                insertUpdateDelete("insertAnggota")
            }
            R.id.btnUpdAnggota -> {
                insertUpdateDelete("updateAnggota")
            }
            R.id.imUpload -> {
                showUploadDialog()
            }
            R.id.btnAnggotaBack -> {
                val intent = Intent(this, HomeAdminActivity::class.java)
                startActivity(intent)
            }
            R.id.btnDataAnggota -> {
                val intent = Intent(this, DataAnggotaActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreateDialog(id: Int): Dialog {
        return when (id) {
            100 -> DatePickerDialog(
                this,
                dateChangeDialog,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            )
            else -> super.onCreateDialog(id)
        }
    }

    val dateChangeDialog = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        // Set tanggal yang dipilih ke dalam TextView txTglLahirAnggota
        val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
        b.txTglLahirAnggota.text = selectedDate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityKelolaAnggotaBinding.inflate(layoutInflater)
        setContentView(b.root)

        try {
            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mediaHelper = MediaHelper(this)

        // Set default tanggal
        b.txTglLahirAnggota.text = ""

        // Mendapatkan data yang dikirimkan dari Intent
        var idAnggota = intent.getLongExtra("idAnggota", 0)

        // Jika idAdmin tidak valid, atur ke nilai default 0
        if (idAnggota == 0L) {
            // Misalnya, Anda bisa menggunakan nilai default seperti 0
            idAnggota = 0L
        }

        // Jika idAnggota tidak sama dengan Int.MAX_VALUE, artinya data telah diterima
        val namaAnggota = intent.getStringExtra("namaAnggota") ?: ""
        val tglLahirAnggota = intent.getStringExtra("tglLahirAnggota") ?: ""
        val jenisKelamin = intent.getStringExtra("jenisKelamin") ?: ""
        val alamatAnggota = intent.getStringExtra("alamatAnggota") ?: ""
        val noHpAnggota = intent.getStringExtra("noHpAnggota") ?: ""
        val nikAnggota = intent.getStringExtra("nikAnggota") ?: ""
        val noRekAnggota = intent.getStringExtra("noRekAnggota") ?: ""
        val emailAnggota = intent.getStringExtra("emailAnggota") ?: ""
        val passAnggota = intent.getStringExtra("passAnggota") ?: ""
        val urlGambar = intent.getStringExtra("gambar")

        // Tampil Gambar sesuai data yang dipilih
        if (urlGambar != null && urlGambar.isNotEmpty()) {
            Picasso.get().load(urlGambar).into(b.imUpload)
        }

        // Mengatur tanggal lahir anggota
        if (tglLahirAnggota.isNotEmpty()) {
            // Jika tanggal lahir tersedia, set ke dalam Calendar
            val dateParts = tglLahirAnggota.split("-").map { it.toInt() }
            cal.set(dateParts[0], dateParts[1] - 1, dateParts[2])
        }

        // Mengisi formulir dengan data yang diterima
        b.txIdAnggota.text = idAnggota.toString()
        b.edNamaAnggota.setText(namaAnggota)
        b.txTglLahirAnggota.text = tglLahirAnggota

        if (jenisKelamin == "Laki-laki") {
            b.rbLakiLakiAnggota.isChecked = true
            b.rbPerempuanAnggota.isChecked = false
        } else if (jenisKelamin == "Perempuan") {
            b.rbPerempuanAnggota.isChecked = true
            b.rbLakiLakiAnggota.isChecked = false
        } else {
            b.rbPerempuanAnggota.isChecked = false
            b.rbLakiLakiAnggota.isChecked = false
        }

        b.edAlamatAnggota.setText(alamatAnggota)
        b.edNoHpAnggota.setText(noHpAnggota)
        b.edNikAnggota.setText(nikAnggota)
        b.edNoRekeningAnggota.setText(noRekAnggota)
        b.edEmailAnggota.setText(emailAnggota)
        b.edPassAnggota.setText(passAnggota)

        // Set listener pada radio button untuk mematikan radio button lainnya
        b.rbLakiLakiAnggota.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Jika rbLakiLakiAdmin ditekan, matikan rbPerempuanAdmin
                b.rbPerempuanAnggota.isChecked = false
            }
        }

        b.rbPerempuanAnggota.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Jika rbPerempuanAdmin ditekan, matikan rbLakiLakiAdmin
                b.rbLakiLakiAnggota.isChecked = false
            }
        }

        b.imUpload.setOnClickListener(this)
        b.btnTglLahirAnggota.setOnClickListener(this)
        b.btnInsAnggota.setOnClickListener(this)
        b.btnUpdAnggota.setOnClickListener(this)
        b.btnAnggotaBack.setOnClickListener(this)
        b.btnDataAnggota.setOnClickListener(this)
        requestPermissionsIfRequired()
    }

    fun insertUpdateDelete(command : String) {
        val result = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    // Bersihkan respons dari karakter tambahan yang tidak valid
                    val cleanResponse = response.replace("<br>", "").replace(Regex(".*<html>.*</html>"), "").trim()

                    // Coba parsing JSON
                    val jsonObject = JSONObject(cleanResponse)
                    val kode = jsonObject.getString("kode")
                    if(kode.equals("200")) {
                        Toast.makeText(this, "Operasi Sukses", Toast.LENGTH_SHORT).show()

                        b.txIdAnggota.setText("0")
                        b.edNamaAnggota.setText("")
                        b.txTglLahirAnggota.text = ""
                        b.rbLakiLakiAnggota.isChecked = false
                        b.rbPerempuanAnggota.isChecked = false
                        b.edAlamatAnggota.setText("")
                        b.edNoHpAnggota.setText("")
                        b.edEmailAnggota.setText("")
                        b.edPassAnggota.setText("")
                        b.edNikAnggota.setText("")
                        b.edNoRekeningAnggota.setText("")
                    } else {
                        Toast.makeText(this, "Operasi Gagal", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(this,"${it.message}",
                    Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                val nmFile = "DC" + SimpleDateFormat("yyyyMMddHHmmss",
                    Locale.getDefault()).format(Date())+".jpg"
                when(command) {
                    "insertAnggota" -> {
                        hm.put("command", "insertAnggota")
                        hm.put("nama_lengkap", b.edNamaAnggota.text.toString())
                        hm.put("tgl_lahir", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time))
                        hm.put("jenis_kelamin", if (b.rbLakiLakiAnggota.isChecked) "Laki-laki" else "Perempuan")
                        hm.put("alamat", b.edAlamatAnggota.text.toString())
                        hm.put("no_hp", b.edNoHpAnggota.text.toString())
                        hm.put("email", b.edEmailAnggota.text.toString())
                        hm.put("password", b.edPassAnggota.text.toString())
                        hm.put("nik", b.edNikAnggota.text.toString())
                        hm.put("no_rekening", b.edNoRekeningAnggota.text.toString())
                        hm.put("image", imStr)
                        hm.put("file", nmFile)

                    }
                    "updateAnggota" -> {
                        hm.put("command", "updateAnggota")
                        hm.put("id", b.txIdAnggota.text.toString())
                        hm.put("nama_lengkap", b.edNamaAnggota.text.toString())
                        hm.put("tgl_lahir", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time))
                        hm.put("jenis_kelamin", if (b.rbLakiLakiAnggota.isChecked) "Laki-laki" else "Perempuan")
                        hm.put("alamat", b.edAlamatAnggota.text.toString())
                        hm.put("no_hp", b.edNoHpAnggota.text.toString())
                        hm.put("email", b.edEmailAnggota.text.toString())
                        hm.put("password", b.edPassAnggota.text.toString())
                        hm.put("nik", b.edNikAnggota.text.toString())
                        hm.put("no_rekening", b.edNoRekeningAnggota.text.toString())
                        hm.put("image", imStr)
                        hm.put("file", nmFile)

                    }
                }
                return hm
            }
        }
        val q = Volley.newRequestQueue(this)
        q.add(result)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            if (requestCode == mediaHelper.getRcGallery() && data != null) {
                val uri = data.data
                if (uri != null) {
                    imStr = mediaHelper.getBitmapToString(uri, b.imUpload)
                    b.imUpload.setImageURI(uri)
                }
            } else if (requestCode == mediaHelper.getRcCamera()) {
                val uri = fileUri
                if (uri != null) {
                    imStr = mediaHelper.getBitmapToString(uri, b.imUpload)
                    namafile = mediaHelper.getMyFileName()
                    b.imUpload.setImageURI(uri)
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showUploadDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Upload Gambar")
        builder.setMessage("Pilih fitur upload gambar di bawah")
        builder.setPositiveButton("Kamera") { dialog, which ->
            // Do something when user press the positive button
            val permisi = mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.INTERNET
            )
            if (Build.VERSION.SDK_INT < 33) { // Android 13 is API level 33
                permisi.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                permisi.add(Manifest.permission.READ_MEDIA_IMAGES)
            }

            PermissionX.init(this)
                .permissions( permisi )
                .request { allGranted, grantedList, deniedList ->
                    if (allGranted) {
                        fileUri = mediaHelper.getOutputMediaFileUri()
                        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
                        startActivityForResult(intent, mediaHelper.getRcCamera())
                    } else {
                        Toast.makeText(this, "Denied: $deniedList", Toast.LENGTH_LONG).show()
                    }
                }
        }

        builder.setNegativeButton("Galeri") { dialog, which ->
            // Do something when user press the negative button
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, mediaHelper.getRcGallery())
        }
        builder.show()
    }

    private fun requestPermissionsIfRequired() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
//                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}