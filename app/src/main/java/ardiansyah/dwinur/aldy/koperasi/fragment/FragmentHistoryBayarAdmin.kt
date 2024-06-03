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
import ardiansyah.dwinur.aldy.koperasi.admin.AdapterBayarAdmin
import ardiansyah.dwinur.aldy.koperasi.anggota.AdapterBayar
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryBayarAdminBinding
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryBayarBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import java.text.NumberFormat
import java.util.Locale

class FragmentHistoryBayarAdmin : Fragment() {
    lateinit var b: FragHistoryBayarAdminBinding
    lateinit var thisParent: FragmentHistoryAdmin
    lateinit var v: View

    val url = "http://192.168.241.103/koperasi/koperasi.php"
    val daftarBayarAdmin = mutableListOf<HashMap<String, String>>()
    lateinit var adapter: AdapterBayarAdmin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = parentFragment as FragmentHistoryAdmin
        b = FragHistoryBayarAdminBinding.inflate(inflater, container, false)
        v = b.root

        adapter = AdapterBayarAdmin(daftarBayarAdmin, b)
        b.rvBayarAdmin.adapter = adapter
        b.rvBayarAdmin.layoutManager = LinearLayoutManager(thisParent.requireContext())

        showBayar()
        return v
    }

    private fun showBayar() {
        val result = object : StringRequest(
            Method.POST, url,
            Response.Listener {
                daftarBayarAdmin.clear()
                try {
                    val arrayBrg = JSONArray(it)
                    for (x in 0 until arrayBrg.length()) {
                        val jsonObject = arrayBrg.getJSONObject(x)
                        val hm = HashMap<String, String>()
                        hm["id"] = jsonObject.getString("id")
                        hm["nama_lengkap"] = jsonObject.getString("nama_lengkap")
                        hm["tgl_bayar"] = jsonObject.getString("tgl_bayar")
                        val nominalBayar = jsonObject.getDouble("nominal_bayar")
                        hm["nominal_bayar"] = formatCurrency(nominalBayar)
                        val nominalPinjam = jsonObject.getDouble("nominal_pinjam")
                        hm["nominal_pinjam"] = formatCurrency(nominalPinjam)
                        daftarBayarAdmin.add(hm)
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    Toast.makeText(thisParent.requireContext(), "Error parsing data", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener {
                Toast.makeText(thisParent.requireContext(), it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                hm["command"] = "selectBayarAdmin"
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