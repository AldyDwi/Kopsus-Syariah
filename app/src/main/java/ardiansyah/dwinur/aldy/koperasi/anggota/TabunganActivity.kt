package ardiansyah.dwinur.aldy.koperasi.anggota

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivityTabunganBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random

class TabunganActivity: AppCompatActivity(), View.OnClickListener {

    lateinit var b : ActivityTabunganBinding
    val url = "http://192.168.241.103/koperasi/koperasi.php"

    private lateinit var idAnggota: String
    var idTabungan = ""
    var namaAnggota = ""
    var nominalTabungan = 0.0
    var noRekTabungan = ""



    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnTambahItemSetoran -> {
                insertTabungan("insertTabungan", idAnggota)
            }
            R.id.btnAnggotaBack -> {
                val intent = Intent(this, HomeAnggotaActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityTabunganBinding.inflate(layoutInflater)
        setContentView(b.root)


        // Menginisialisasi idSetoran
        idTabungan = Random(System.currentTimeMillis()).nextInt(999999).toString()

        // Menampilkan nomor nota
        b.txNoNotaSetoran.text = "$idTabungan"

        // Ambil nama anggota dari SharedPreferences
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        namaAnggota = sharedPref.getString("nama_lengkap", "") ?: ""
        b.txNamaAnggotaSetoran.text = namaAnggota

        // cari idAnggota berdasarkan nama anggota yang login
        idAnggota = sharedPref.getString("id", "") ?: ""

        val cal = Calendar.getInstance()
        val tglTabungan = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}"
        b.txTglTabungan.text = tglTabungan

        // cari no rekening
        noRekTabungan = sharedPref.getString("no_rekening", "") ?: ""
        b.txNoRekTabungan.text = noRekTabungan

        // Menetapkan listener untuk tombol tambah dan simpan
        b.btnTambahItemSetoran.setOnClickListener(this)
        b.btnAnggotaBack.setOnClickListener(this)
    }

    fun formatCurrency(amount: Double): String {
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        return formatter.format(amount)
    }

    fun insertTabungan(command : String, idAnggota : String) {
        val result = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    // Bersihkan respons dari karakter tambahan yang tidak valid
                    val cleanResponse = response.replace("<br>", "").replace(Regex(".*<html>.*</html>"), "").trim()

                    // Coba parsing JSON
                    val jsonObject = JSONObject(cleanResponse)
                    val kode = jsonObject.getString("kode")
                    if (kode == "200") {
                        Toast.makeText(this, "Transaksi Berhasil", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, HomeAnggotaActivity::class.java)
                        startActivity(intent)
                        finish() // Selesai dengan activity saat ini
                    } else {
                        Toast.makeText(this, "Transaksi Gagal", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String>? {
                val hm = java.util.HashMap<String, String>()
                val cal = Calendar.getInstance()
                val tglTabungan = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}"

                when(command) {
                    "insertTabungan" -> {
                        hm.put("command", "insertTabungan")
                        hm.put("id", Random(System.currentTimeMillis()).nextInt(999999).toString())
                        hm.put("id_anggota", idAnggota)
                        hm.put("tgl_tabung", tglTabungan)
                        hm.put("nominal_tabung", b.edNominalSetoran.text.toString())
                    }
                }
                return hm
            }
        }
        val q = Volley.newRequestQueue(this)
        q.add(result)
    }

}