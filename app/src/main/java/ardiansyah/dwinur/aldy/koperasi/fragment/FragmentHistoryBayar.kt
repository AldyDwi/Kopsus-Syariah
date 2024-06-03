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
import ardiansyah.dwinur.aldy.koperasi.anggota.AdapterBayar
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryBayarBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.text.NumberFormat
import java.util.Locale

class FragmentHistoryBayar : Fragment() {
    lateinit var b: FragHistoryBayarBinding
    lateinit var thisParent: FragmentHistory
    lateinit var v: View

    private lateinit var sharedPref: SharedPreferences

    val url = "http://192.168.241.103/koperasi/koperasi.php"
    val daftarBayar = mutableListOf<HashMap<String, String>>()
    lateinit var adapter: AdapterBayar

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
        b = FragHistoryBayarBinding.inflate(inflater, container, false)
        v = b.root

        val idAnggota = sharedPref.getString("id", "") ?: ""

        adapter = AdapterBayar(daftarBayar, b)
        b.rvBayar.adapter = adapter
        b.rvBayar.layoutManager = LinearLayoutManager(thisParent.requireContext())

        showBayar(idAnggota)
        return v
    }

    private fun showBayar(idAnggota: String) {
        val result = object : StringRequest(
            Method.POST, url,
            Response.Listener {
                daftarBayar.clear()
                val arrayBrg = JSONArray(it)
                for (x in 0 until arrayBrg.length()) {
                    val jsonObject = arrayBrg.getJSONObject(x)
                    val hm = HashMap<String, String>()
                    hm["id"] = jsonObject.getString("id")
                    hm["tgl_bayar"] = jsonObject.getString("tgl_bayar")
                    val nominalBayar = jsonObject.getDouble("nominal_bayar")
                    hm["nominal_bayar"] = formatCurrency(nominalBayar)
                    val nominalPinjam = jsonObject.getDouble("nominal_pinjam")
                    hm["nominal_pinjam"] = formatCurrency(nominalPinjam)
                    daftarBayar.add(hm)
                }
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener {
                Toast.makeText(thisParent.requireContext(), it.message, Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                hm["command"] = "selectBayar"
                hm["idAnggota"] = idAnggota
                return hm
            }
        }
        val q = Volley.newRequestQueue(thisParent.requireContext())
        q.add(result)
    }

//    override fun onStart() {
//        super.onStart()
//        showBayar()
//    }

    fun formatCurrency(amount: Double): String {
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        return formatter.format(amount)
    }
}