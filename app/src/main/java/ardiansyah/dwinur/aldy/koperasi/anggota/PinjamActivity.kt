package ardiansyah.dwinur.aldy.koperasi.anggota


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivityPinjamBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject
import java.util.Calendar
import java.util.Random
import java.text.NumberFormat
import java.util.*

class PinjamActivity: AppCompatActivity(), View.OnClickListener {

    lateinit var b : ActivityPinjamBinding
    val url = "http://192.168.241.103/koperasi/koperasi.php"
    private lateinit var idAnggota: String

    var idPinjam = ""
//    var idAnggota = ""
    var namaAnggota = ""
    var noRek = ""
    var tglPinjam = ""
    var nominalPinjam = 0.0
    var jangkaWaktu = 0
    var cicilan = 0.0
    var biayaAdmin = 25000
    var totalPinjaman = 0
    var sql = ""


    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAnggotaBack -> {
                val intent = Intent(this, HomeAnggotaActivity::class.java)
                startActivity(intent)
            }
            R.id.btnPinjamAnggota -> {
                insertPinjam("insertPinjam", idAnggota)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityPinjamBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Dapatkan data yang dikirim dari intent
        nominalPinjam = intent.getDoubleExtra("nominal_pinjam", 0.0)
        jangkaWaktu = intent.getIntExtra("jangka_waktu", 0)

        // Menginisialisasi idPinjam
        idPinjam = Random(System.currentTimeMillis()).nextInt(999999).toString()

        // Menampilkan nomor nota
        b.txNoNotaPinjam.text = "$idPinjam"

        // Ambil data anggota dari SharedPreferences
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        namaAnggota = sharedPref.getString("nama_lengkap", "") ?: ""
        b.txNamaAnggotaPinjam.text = namaAnggota

        noRek = sharedPref.getString("no_rekening", "") ?: ""
        b.txNoRekPinjam.text = noRek

        // cari idAnggota berdasarkan nama anggota yang login
        idAnggota = sharedPref.getString("id", "") ?: ""

        // bagian tanggal
        val cal = Calendar.getInstance()
        tglPinjam = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}"
        b.txTglPinjam.text = tglPinjam

        // bagian nominal
        b.txNominalPinjam.text = "${formatCurrency(nominalPinjam)}"

        // bagian total pinjaman
        totalPinjaman = (nominalPinjam + biayaAdmin).toInt()
        val totalPinjam = totalPinjaman.toDouble()
        b.txTotalPinjaman.text = "${formatCurrency(totalPinjam)}"

        // bagian cicilan
        cicilan = totalPinjam / jangkaWaktu
        b.txCicilanPinjam.text = "${formatCurrency(cicilan)}"

        // bagian jangka waktu
        b.txWaktuPinjam.text = "$jangkaWaktu bulan"

        // Menetapkan listener untuk tombol tambah
        b.btnPinjamAnggota.setOnClickListener(this)
        b.btnAnggotaBack.setOnClickListener(this)
    }

    fun insertPinjam(command : String, idAnggota: String) {
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
                val hm = HashMap<String,String>()
                val cal = Calendar.getInstance()
                val tglPinjam = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}"

                // nominal
                nominalPinjam = intent.getDoubleExtra("nominal_pinjam", 0.0)
                totalPinjaman = (nominalPinjam + biayaAdmin).toInt()
                val totalPinjam = totalPinjaman.toDouble()

                // waktu pinjam
                jangkaWaktu = intent.getIntExtra("jangka_waktu", 0)

                // cicilan
                val cicilan = totalPinjaman / jangkaWaktu

                when(command) {
                    "insertPinjam" -> {
                        hm.put("command", "insertPinjam")
                        hm.put("id", Random(System.currentTimeMillis()).nextInt(999999).toString())
                        hm.put("idAnggota", idAnggota)
                        hm.put("tgl_pinjam", tglPinjam)
                        hm.put("nominal_pinjam", totalPinjam.toString())
                        hm.put("waktu_pinjam", jangkaWaktu.toString())
                        hm.put("cicilan", cicilan.toString())
                        hm.put("biaya_admin", biayaAdmin.toString())
                    }
                }
                return hm
            }
        }
        val q = Volley.newRequestQueue(this)
        q.add(result)
    }

    fun formatCurrency(amount: Double): String {
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        return formatter.format(amount)
    }
}