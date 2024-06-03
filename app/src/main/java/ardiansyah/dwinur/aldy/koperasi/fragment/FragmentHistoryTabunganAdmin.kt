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
import ardiansyah.dwinur.aldy.koperasi.admin.AdapterTabunganAdmin
import ardiansyah.dwinur.aldy.koperasi.anggota.AdapterTabungan
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryTabunganAdminBinding
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryTabunganBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.text.NumberFormat
import java.util.Locale

class FragmentHistoryTabunganAdmin : Fragment() {
    lateinit var b : FragHistoryTabunganAdminBinding
    lateinit var thisParent : FragmentHistoryAdmin
    lateinit var v : View

    val url = "http://192.168.241.103/koperasi/koperasi.php"
    val daftarTabunganAdmin = mutableListOf<HashMap<String, String>>()
    lateinit var adapter: AdapterTabunganAdmin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = parentFragment as FragmentHistoryAdmin
        b = FragHistoryTabunganAdminBinding.inflate(inflater, container, false)
        v = b.root

        adapter = AdapterTabunganAdmin(daftarTabunganAdmin, b)
        b.rvTabunganAdmin.adapter = adapter
        b.rvTabunganAdmin.layoutManager = LinearLayoutManager(thisParent.requireContext())

        showTabungan()
        return v
    }

    private fun showTabungan() {
        val result = object : StringRequest(
            Method.POST, url,
            Response.Listener {
                daftarTabunganAdmin.clear()
                val arrayBrg = JSONArray(it)
                for (x in 0 until arrayBrg.length()) {
                    val jsonObject = arrayBrg.getJSONObject(x)
                    val hm = HashMap<String, String>()
                    hm["id"] = jsonObject.getString("id")
                    hm["nama_lengkap"] = jsonObject.getString("nama_lengkap")
                    hm["tgl_tabung"] = jsonObject.getString("tgl_tabung")
                    val nominalTabungan = jsonObject.getDouble("nominal_tabung")
                    hm["nominal_tabung"] = formatCurrency(nominalTabungan)
                    daftarTabunganAdmin.add(hm)
                }
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener {
                Toast.makeText(thisParent.requireContext(), it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                hm["command"] = "selectTabunganAdmin"
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