package ardiansyah.dwinur.aldy.koperasi.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import ardiansyah.dwinur.aldy.koperasi.anggota.AdapterTabungan
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryTabunganBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.text.NumberFormat
import java.util.Locale

class FragmentHistoryTabungan : Fragment() {
    lateinit var b : FragHistoryTabunganBinding
    lateinit var thisParent : FragmentHistory
    lateinit var v : View

    private lateinit var sharedPref: SharedPreferences

    val url = "http://192.168.241.103/koperasi/koperasi.php"
    val daftarTabungan = mutableListOf<HashMap<String, String>>()
    lateinit var adapter: AdapterTabungan

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Inisialisasi shared preferences
        sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = parentFragment as FragmentHistory
        b = FragHistoryTabunganBinding.inflate(inflater, container, false)
        v = b.root

        val idAnggota = sharedPref.getString("id", "") ?: ""

        adapter = AdapterTabungan(daftarTabungan, b)
        b.rvTabungan.adapter = adapter
        b.rvTabungan.layoutManager = LinearLayoutManager(thisParent.requireContext())

        showTabungan(idAnggota)
        return v
    }

    private fun showTabungan(idAnggota: String) {
        val result = object : StringRequest(
            Method.POST, url,
            Response.Listener {
                daftarTabungan.clear()
                val arrayBrg = JSONArray(it)
                for (x in 0 until arrayBrg.length()) {
                    val jsonObject = arrayBrg.getJSONObject(x)
                    val hm = HashMap<String, String>()
                    hm["id"] = jsonObject.getString("id")
                    hm["tgl_tabung"] = jsonObject.getString("tgl_tabung")
                    val nominalTabungan = jsonObject.getDouble("nominal_tabung")
                    hm["nominal_tabung"] = formatCurrency(nominalTabungan)
                    daftarTabungan.add(hm)
                }
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener {
                Toast.makeText(thisParent.requireContext(), it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                hm["command"] = "selectTabungan"
                hm["idAnggota"] = idAnggota
                return hm
            }
        }
        val q = Volley.newRequestQueue(thisParent.requireContext())
        q.add(result)
    }

    fun formatCurrency(amount: Double): String {
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        return formatter.format(amount)
    }
}