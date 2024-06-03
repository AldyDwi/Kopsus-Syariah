package ardiansyah.dwinur.aldy.koperasi.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import ardiansyah.dwinur.aldy.koperasi.MainActivity
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivityHomeAdminBinding
import ardiansyah.dwinur.aldy.koperasi.fragment.FragmentHistoryAdmin
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException


class HomeAdminActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var b : ActivityHomeAdminBinding

    val url = "http://192.168.241.103/koperasi/koperasi.php"
    lateinit var adapter : AdapterAdmin

    lateinit var fragHistoryAdmin : FragmentHistoryAdmin
    lateinit var ft : FragmentTransaction

    fun showJumlahAdmin() {
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    val jsonObject = jsonArray.getJSONObject(0)
                    val totalAdmin = jsonObject.getString("total_karyawan")
                    b.txJmlAdmin.text = totalAdmin
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["command"] = "jumlahKaryawan"
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun showJumlahAnggota() {
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    val jsonObject = jsonArray.getJSONObject(0)
                    val totalAnggota = jsonObject.getString("total_anggota")
                    b.txJmlAnggota.text = totalAnggota
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["command"] = "jumlahAnggota"
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    fun showJumlahTransaksi() {
        val stringRequest = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    val jsonObject = jsonArray.getJSONObject(0)
                    val totalTransaksi = jsonObject.getString("total_transaksi")
                    b.txJmlTransaksi.text = totalTransaksi
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["command"] = "jumlahTransaksi"
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(stringRequest)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityHomeAdminBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Ambil jumlah data dari masing-masing tabel
        val countAnggota = showJumlahAnggota()
        val countAdmin = showJumlahAdmin()
        val countTransaksi = showJumlahTransaksi()

        // Tampilkan jumlah data pada TextView yang sesuai
        b.txJmlAnggota.text = "$countAnggota"
        b.txJmlAdmin.text = "$countAdmin"
        b.txJmlTransaksi.text = "$countTransaksi"

        fragHistoryAdmin = FragmentHistoryAdmin()
        b.btnKelAnggota.setOnClickListener(this)
        b.btnKelAdmin.setOnClickListener(this)
        b.btnKelJabatan.setOnClickListener(this)
        b.btnHstPinjam.setOnClickListener(this)
        b.btnLogoutAdmin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnKelAdmin -> {
                val intent = Intent(this, KelolaAdminActivity::class.java)
                startActivity(intent)
            }
            R.id.btnKelJabatan -> {
                val intent = Intent(this, KelolaJabatanActivity::class.java)
                startActivity(intent)
            }
            R.id.btnKelAnggota -> {
                val intent = Intent(this, KelolaAnggotaActivity::class.java)
                startActivity(intent)
            }
            R.id.btnHstPinjam -> {
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, fragHistoryAdmin)
                ft.addToBackStack(null) // Tambahkan transaksi ke dalam back stack
                ft.commit()
                b.frameLayout.visibility = View.VISIBLE
            }
            R.id.btnLogoutAdmin -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}