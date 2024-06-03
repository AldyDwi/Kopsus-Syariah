package ardiansyah.dwinur.aldy.koperasi.anggota


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivityBayarBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.NumberFormat
import java.util.Calendar
import java.util.Locale
import java.util.Random

class BayarActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener, View.OnClickListener {

    lateinit var b: ActivityBayarBinding
    private lateinit var spBayar: Spinner
    private lateinit var pinjamanList: MutableList<Pinjaman>

    val url = "http://192.168.241.103/koperasi/koperasi.php"

    var idBayar = ""
    var idPinjam = ""
    var idAnggota = ""
    var namaAnggota = ""
    var noRek = ""
    var nominalPinjam = 0.0
    var cicilan = 0.0
    var waktuPinjam = 0
    var tglBayar = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityBayarBinding.inflate(layoutInflater)
        setContentView(b.root)

        spBayar = findViewById(R.id.spBayar)
        pinjamanList = mutableListOf()

        idBayar = Random(System.currentTimeMillis()).nextInt(999999).toString()
        b.txNoNotaBayar.text = "$idBayar"

        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        namaAnggota = sharedPref.getString("nama_lengkap", "") ?: ""
        b.txNamaAnggotaBayar.text = namaAnggota

        idAnggota = sharedPref.getString("id", "") ?: ""

        val cal = Calendar.getInstance()
        tglBayar = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}"
        b.txTglBayar.text = tglBayar

        noRek = sharedPref.getString("no_rekening", "") ?: ""
        b.txNoRekBayar.text = noRek

        fetchPinjamanData(idAnggota)

        spBayar.onItemSelectedListener = this
        b.btnBayarAnggota.setOnClickListener(this)
        b.btnAnggotaBack.setOnClickListener(this)
    }

    private fun fetchPinjamanData(idAnggota: String) {
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                val jsonArray = JSONArray(response)
                pinjamanList.clear()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val pinjam = Pinjaman(
                        id = jsonObject.getString("id"),
                        nominalPinjam = jsonObject.getDouble("nominal_pinjam"),
                        cicilan = jsonObject.getDouble("cicilan"),
                        waktuPinjam = jsonObject.getInt("waktu_pinjam")
                    )
                    pinjamanList.add(pinjam)

                }
                updateSpinner(pinjamanList)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["command"] = "selectPinjamB"
                params["idAnggota"] = idAnggota
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    private fun updateSpinner(pinjamanList: List<Pinjaman>) {
        if (pinjamanList.isNotEmpty()) {
            val adapter = PinjamanAdapter(this, pinjamanList)
            spBayar.adapter = adapter
        } else {
            Log.e("updateSpinner", "Pinjaman list is empty")
        }
    }

    inner class PinjamanAdapter(context: Context, private val pinjamanList: List<Pinjaman>) : ArrayAdapter<Pinjaman>(context, android.R.layout.simple_spinner_item, pinjamanList) {
        init {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getView(position, convertView, parent)
            val textView = view.findViewById<TextView>(android.R.id.text1)
            textView.text = formatNominalPinjam(pinjamanList[position].nominalPinjam)
            return view
        }

        override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = super.getDropDownView(position, convertView, parent)
            val textView = view.findViewById<TextView>(android.R.id.text1)
            textView.text = formatNominalPinjam(pinjamanList[position].nominalPinjam)
            return view
        }

        private fun formatNominalPinjam(nominal: Double): String {
            return "Rp ${String.format("%,.2f", nominal)}"
        }
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val selectedPinjam = pinjamanList[position]
        nominalPinjam = selectedPinjam.nominalPinjam
        cicilan = selectedPinjam.cicilan
        idPinjam = selectedPinjam.id
        waktuPinjam = selectedPinjam.waktuPinjam

        b.txNominalPinjamB.text = formatCurrency(nominalPinjam)
        b.txCicilanBayar.text = formatCurrency(cicilan)
        b.txNominalBayar.text = formatCurrency(cicilan)
        b.txWaktuBayar.text = "$waktuPinjam"
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing
    }

    fun insertPinjam(command : String) {
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
                val tglBayar = "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH) + 1}-${cal.get(Calendar.DAY_OF_MONTH)}"

                when(command) {
                    "insertBayar" -> {
                        hm.put("command", "insertBayar")
                        hm.put("id", Random(System.currentTimeMillis()).nextInt(999999).toString())
                        hm.put("tgl_bayar", tglBayar)
                        hm.put("nominal_pinjam", nominalPinjam.toString())
                        hm.put("nominal_bayar", cicilan.toString())
                    }
                }
                return hm
            }
        }
        val q = Volley.newRequestQueue(this)
        q.add(result)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnBayarAnggota -> {
                insertPinjam("insertBayar")
            }
            R.id.btnAnggotaBack -> {
                val intent = Intent(this, HomeAnggotaActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun formatCurrency(amount: Double): String {
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        return formatter.format(amount)
    }
}

data class Pinjaman(
    val id: String,
    val nominalPinjam: Double,
    val cicilan: Double,
    val waktuPinjam: Int
)