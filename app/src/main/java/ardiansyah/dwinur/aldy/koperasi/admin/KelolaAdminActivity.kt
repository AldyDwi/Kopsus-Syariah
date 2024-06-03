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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ardiansyah.dwinur.aldy.koperasi.MediaHelper
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivityKelolaAdminBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.permissionx.guolindev.PermissionX
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class KelolaAdminActivity: AppCompatActivity(), View.OnClickListener {
    lateinit var b : ActivityKelolaAdminBinding

    val url = "http://192.168.241.103/koperasi/koperasi.php"
    lateinit var adapter : AdapterAdmin
    lateinit var mediaHelper : MediaHelper

    private val PERMISSION_REQUEST_CODE = 101

    val daftarJabatan = mutableListOf<String>()
    lateinit var jabatanAdapter: ArrayAdapter<String>
    var pilihJabatan = ""

    var imStr = ""
    var fileUri = Uri.parse("")
    var namafile = ""

    // Mengatur Tanggal dan Jam menggunakan Calendar
    val cal: Calendar = Calendar.getInstance()

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnTglLahirAdmin -> showDialog(100)
            R.id.btnInsAdmin -> {
                insertUpdateDelete("insertKaryawan")
            }
            R.id.btnUpdAdmin -> {
                insertUpdateDelete("updateKaryawan")
            }
            R.id.imUpload -> {
                showUploadDialog()
            }
            R.id.btnAnggotaBack -> {
                val intent = Intent(this, HomeAdminActivity::class.java)
                startActivity(intent)
            }
            R.id.btnDataAdmin -> {
                val intent = Intent(this, DataAdminActivity::class.java)
                startActivity(intent)
            }
        }
    }

    val dateChangeDialog = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month)
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        // Set tanggal yang dipilih ke dalam TextView txTglLahirAdmin
        val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
        b.txTglLahirAdmin.text = selectedDate
    }

    override fun onCreateDialog(id: Int): Dialog {
        return when (id) {
            100 -> DatePickerDialog(this, dateChangeDialog, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
            else -> super.onCreateDialog(id)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityKelolaAdminBinding.inflate(layoutInflater)
        setContentView(b.root)

        try {
            val m = StrictMode::class.java.getMethod("disableDeathOnFileUriExposure")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        mediaHelper = MediaHelper(this)

        // Set default tanggal
        b.txTglLahirAdmin.text = ""

        jabatanAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, daftarJabatan)
        b.spinJabatan.adapter = jabatanAdapter
        b.spinJabatan.onItemSelectedListener = itemSelected

        // Mendapatkan data yang dikirimkan dari Intent
        var idAdmin = intent.getLongExtra("idAdmin", 0)

        // Jika idAdmin tidak valid, atur ke nilai default 0
        if (idAdmin == 0L) {
            // Misalnya, Anda bisa menggunakan nilai default seperti 0
            idAdmin = 0L
        }

        val namaAdmin = intent.getStringExtra("namaAdmin") ?: ""
        val namaJabatan = intent.getStringExtra("namaJabatan") ?: ""
        val tglLahirAdmin = intent.getStringExtra("tglLahirAdmin") ?: ""
        val jenisKelamin = intent.getStringExtra("jenisKelamin") ?: ""
        val alamatAdmin = intent.getStringExtra("alamatAdmin") ?: ""
        val noHpAdmin = intent.getStringExtra("noHpAdmin") ?: ""
        val emailAdmin = intent.getStringExtra("emailAdmin") ?: ""
        val passAdmin = intent.getStringExtra("passAdmin") ?: ""
        val urlGambar = intent.getStringExtra("gambar")

        // Tampil Gambar sesuai data yang dipilih
        if (urlGambar != null && urlGambar.isNotEmpty()) {
            Picasso.get().load(urlGambar).into(b.imUpload)
        }

        // Mengatur tanggal lahir admin jika data tersedia
        if (tglLahirAdmin.isNotEmpty()) {
            // Jika tanggal lahir tersedia, set ke dalam Calendar
            val dateParts = tglLahirAdmin.split("-").map { it.toInt() }
            cal.set(dateParts[0], dateParts[1] - 1, dateParts[2])
        }

        // Mengisi formulir dengan data yang diterima dari Intent
        b.txIdAdmin.text = idAdmin.toString()
        b.edNamaAdmin.setText(namaAdmin)
        b.txTglLahirAdmin.text = tglLahirAdmin

        if (jenisKelamin == "Laki-laki") {
            b.rbLakiLakiAdmin.isChecked = true
            b.rbPerempuanAdmin.isChecked = false
        } else if (jenisKelamin == "Perempuan") {
            b.rbPerempuanAdmin.isChecked = true
            b.rbLakiLakiAdmin.isChecked = false
        } else {
            b.rbPerempuanAdmin.isChecked = false
            b.rbLakiLakiAdmin.isChecked = false
        }

        b.edAlamatAdmin.setText(alamatAdmin)
        b.edNoHpAdmin.setText(noHpAdmin)
        b.edEmailAdmin.setText(emailAdmin)
        b.edPassAdmin.setText(passAdmin)

        // Set listener pada radio button untuk mematikan radio button lainnya
        b.rbLakiLakiAdmin.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Jika rbLakiLakiAdmin ditekan, matikan rbPerempuanAdmin
                b.rbPerempuanAdmin.isChecked = false
            }
        }

        b.rbPerempuanAdmin.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                // Jika rbPerempuanAdmin ditekan, matikan rbLakiLakiAdmin
                b.rbLakiLakiAdmin.isChecked = false
            }
        }

        // Load jabatan data
        getNamaJabatan(namaJabatan)

        // Set listener pada tombol
        b.imUpload.setOnClickListener(this)
        b.btnTglLahirAdmin.setOnClickListener(this)
        b.btnInsAdmin.setOnClickListener(this)
        b.btnUpdAdmin.setOnClickListener(this)
        b.btnAnggotaBack.setOnClickListener(this)
        b.btnDataAdmin.setOnClickListener(this)
        requestPermissionsIfRequired()

    }

    fun insertUpdateDelete(command : String) {
        val result = object : StringRequest(
            Method.POST, url,
            Response.Listener {
                val jsonObject = JSONObject(it)
                val kode = jsonObject.getString("kode")
                if(kode.equals("200")) {
                    Toast.makeText(this, "Operasi Sukses", Toast.LENGTH_SHORT).show()

                    b.txIdAdmin.setText("0")
                    b.edNamaAdmin.setText("")
                    b.txTglLahirAdmin.text = ""
                    b.rbLakiLakiAdmin.isChecked = false
                    b.rbPerempuanAdmin.isChecked = false
                    b.edAlamatAdmin.setText("")
                    b.edNoHpAdmin.setText("")
                    b.edEmailAdmin.setText("")
                    b.edPassAdmin.setText("")
                } else {
                    Toast.makeText(this, "Operasi Gagal", Toast.LENGTH_SHORT).show()
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
                    "insertKaryawan" -> {
                        hm.put("command", "insertKaryawan")
                        hm.put("nama_lengkap", b.edNamaAdmin.text.toString())
                        hm.put("tgl_lahir", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time))
                        hm.put("jenis_kelamin", if (b.rbLakiLakiAdmin.isChecked) "Laki-laki" else "Perempuan")
                        hm.put("alamat", b.edAlamatAdmin.text.toString())
                        hm.put("no_hp", b.edNoHpAdmin.text.toString())
                        hm.put("email", b.edEmailAdmin.text.toString())
                        hm.put("password", b.edPassAdmin.text.toString())
                        hm.put("image", imStr)
                        hm.put("file", nmFile)
                        hm.put("nama_jabatan", pilihJabatan)

                    } "updateKaryawan" -> {
                        hm.put("command", "updateKaryawan")
                        hm.put("id", b.txIdAdmin.text.toString())
                        hm.put("nama_lengkap", b.edNamaAdmin.text.toString())
                        hm.put("tgl_lahir", SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time))
                        hm.put("jenis_kelamin", if (b.rbLakiLakiAdmin.isChecked) "Laki-laki" else "Perempuan")
                        hm.put("alamat", b.edAlamatAdmin.text.toString())
                        hm.put("no_hp", b.edNoHpAdmin.text.toString())
                        hm.put("email", b.edEmailAdmin.text.toString())
                        hm.put("password", b.edPassAdmin.text.toString())
                        hm.put("image", imStr)
                        hm.put("file", nmFile)
                        hm.put("nama_jabatan", pilihJabatan)

                    }
                }
                return hm
            }
        }
        val q = Volley.newRequestQueue(this)
        q.add(result)
    }

    fun getNamaJabatan(namaJabatan: String) {
        val result = object : StringRequest(Method.POST,url,
            Response.Listener {
                daftarJabatan.clear()
                val arrayJenis = JSONArray(it)
                for (x in 0 until arrayJenis.length()){
                    val jsonObject  = arrayJenis.getJSONObject(x)
                    daftarJabatan.add(jsonObject.getString("nama_jabatan"))
                }
                jabatanAdapter.notifyDataSetChanged()

                // Pilih item yang sesuai dengan namaJabatan jika tidak kosong
                if (namaJabatan.isNotEmpty()) {
                    val index = daftarJabatan.indexOf(namaJabatan)
                    if (index != -1) {
                        b.spinJabatan.setSelection(index)
                    }
                }
            },
            Response.ErrorListener {
                Toast.makeText(this,"${it.message}",
                    Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                hm.put("command", "selectNamaJabatan")
                return hm
            }
        }
        val q = Volley.newRequestQueue(this)
        q.add(result)
    }

    override fun onStart() {
        super.onStart()
        getNamaJabatan("")
    }

    val itemSelected = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            pilihJabatan = daftarJabatan.get(position)
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            b.spinJabatan.setSelection(0)
            pilihJabatan = daftarJabatan.get(0)
        }

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