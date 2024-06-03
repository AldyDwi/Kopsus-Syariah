package ardiansyah.dwinur.aldy.koperasi.anggota

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.FragmentTransaction
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.admin.AdapterAdmin
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivityHomeAnggotaBinding
import ardiansyah.dwinur.aldy.koperasi.fragment.FragmentHistory
import ardiansyah.dwinur.aldy.koperasi.fragment.FragmentProfile
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationBarView
import org.json.JSONException
import java.text.NumberFormat
import java.util.Locale

class HomeAnggotaActivity : AppCompatActivity(), View.OnClickListener, NavigationBarView.OnItemSelectedListener {
    lateinit var b : ActivityHomeAnggotaBinding

    val url = "http://192.168.241.103/koperasi/koperasi.php"
    lateinit var adapter : AdapterAdmin
    lateinit var sharedPreferences: SharedPreferences

    lateinit var fragHistory : FragmentHistory
    lateinit var fragProfile : FragmentProfile
    lateinit var ft : FragmentTransaction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityHomeAnggotaBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Mengambil data dari SharedPreferences yang disimpan di MainActivity
        val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val namaAnggota = sharedPref.getString("nama_lengkap", "") ?: ""
        val idAnggota = sharedPref.getString("id", "") ?: ""

        showTotalPinjam(idAnggota)
        showTotalTabungan(idAnggota)

        // Menampilkan nama anggota pada TextView
        b.namaHomeAnggota.text = "Hi, $namaAnggota"

        b.btnPinjam.setOnClickListener(this)
        b.btnBayar.setOnClickListener(this)
        b.btnSetoran.setOnClickListener(this)

        b.navBawah.setOnItemSelectedListener(this)
        fragHistory = FragmentHistory()
        fragProfile = FragmentProfile()
    }

    fun SharedPreferences.getDouble(key: String): Double {
        return java.lang.Double.longBitsToDouble(getLong(key, java.lang.Double.doubleToRawLongBits(0.0)))
    }

    fun SharedPreferences.Editor.putDouble(key: String, double: Double) {
        putLong(key, java.lang.Double.doubleToRawLongBits(double))
    }

    fun formatCurrency(amount: Double): String {
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        return formatter.format(amount)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnSetoran -> {
                val intent = Intent(this, TabunganActivity::class.java)
                startActivity(intent)
            }
            R.id.btnPinjam -> {
                val intent = Intent(this, AnggotaPinjamActivity::class.java)
                startActivity(intent)
            }
            R.id.btnBayar -> {
                val intent = Intent(this, BayarActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId) {
            R.id.navHome -> {
                b.frameLayout.visibility = View.GONE
            }
            R.id.navHistory -> {
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, fragHistory)
                ft.addToBackStack(null) // Tambahkan transaksi ke dalam back stack
                ft.commit()
                b.frameLayout.visibility = View.VISIBLE
            }
            R.id.navProfile -> {
                ft = supportFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, fragProfile)
                ft.addToBackStack(null) // Tambahkan transaksi ke dalam back stack
                ft.commit()
                b.frameLayout.visibility = View.VISIBLE
            }
        }
        return true
    }

    fun showTotalPinjam(idAnggota: String) {
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val totalPinjam = response.toDoubleOrNull() ?: 0.0

                    val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putDouble("total_peminjaman", totalPinjam)
                    editor.apply()

                    b.txTotalPinjam.text = "${formatCurrency(totalPinjam)}"
                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["command"] = "selectTotalPinjam"
                params["idAnggota"] = idAnggota
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }

    fun showTotalTabungan(idAnggota: String) {
        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val totalTabungan = response.toDoubleOrNull() ?: 0.0

                    val sharedPref = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                    val editor = sharedPref.edit()
                    editor.putDouble("total_tabungan", totalTabungan)
                    editor.apply()

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["command"] = "selectTotalTabungan"
                params["idAnggota"] = idAnggota
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }
}