package ardiansyah.dwinur.aldy.koperasi.fragment

import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import ardiansyah.dwinur.aldy.koperasi.anggota.HomeAnggotaActivity
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.anggota.AdapterPinjam
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.text.NumberFormat
import java.util.Locale

class FragmentHistory : Fragment(), View.OnClickListener {
    lateinit var b : FragHistoryBinding
    lateinit var thisParent : HomeAnggotaActivity
    lateinit var v : View

    lateinit var fragHistoryBayar : FragmentHistoryBayar
    lateinit var fragHistoryTabungan : FragmentHistoryTabungan
    lateinit var ft : FragmentTransaction

    private lateinit var sharedPref: SharedPreferences

    val url = "http://192.168.241.103/koperasi/koperasi.php"
    val daftarPinjam = mutableListOf<HashMap<String,String>>()
    lateinit var adapter : AdapterPinjam

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Inisialisasi shared preferences
        sharedPref = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        thisParent = activity as HomeAnggotaActivity
        b = FragHistoryBinding.inflate(layoutInflater)
        v = b.root

        val idAnggota = sharedPref.getString("id", "") ?: ""

        adapter= AdapterPinjam(daftarPinjam,b)
        b.rvPinjam.adapter = adapter
        b.rvPinjam.layoutManager = LinearLayoutManager(thisParent)

        showPinjam(idAnggota)

        b.btnHistoryPinjam.setOnClickListener(this)
        b.btnHistoryBayar.setOnClickListener(this)
        b.btnHistoryTabungan.setOnClickListener(this)
        fragHistoryBayar = FragmentHistoryBayar()
        fragHistoryTabungan = FragmentHistoryTabungan()

        return v
    }

    private fun showPinjam(idAnggota: String) {
        val  result = object : StringRequest(
            Method.POST,url,
            Response.Listener {
                daftarPinjam.clear()
                val arrayBrg = JSONArray(it)
                for (x in 0 until arrayBrg.length()){
                    val jsonObject  = arrayBrg.getJSONObject(x)
                    val hm = HashMap<String,String>()
                    hm.put("id",jsonObject.getString("id"))
                    hm.put("tgl_pinjam",jsonObject.getString("tgl_pinjam"))
                    hm.put("waktu_pinjam",jsonObject.getString("waktu_pinjam"))
                    val nominalPinjam = jsonObject.getDouble("nominal_pinjam")
                    hm["nominal_pinjam"] = formatCurrency(nominalPinjam)
                    daftarPinjam.add(hm)
                }
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener {
                Toast.makeText(thisParent,"${it.message}",
                    Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("command","selectPinjam")
                hm.put("idAnggota", idAnggota)
                return hm
            }
        }
        val q = Volley.newRequestQueue(thisParent)
        q.add(result)
    }

    fun formatCurrency(amount: Double): String {
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        return formatter.format(amount)
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btnHistoryPinjam -> {

                // Mengubah warna teks dan backgroundTint btnHistoryBayar menjadi biru
                b.btnHistoryBayar.setTextColor(Color.parseColor("#2196F3"))
                b.btnHistoryBayar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1A1A1A")))

                // Mengubah warna teks dan backgroundTint btnHistoryTabungan menjadi biru
                b.btnHistoryTabungan.setTextColor(Color.parseColor("#2196F3"))
                b.btnHistoryTabungan.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1A1A1A")))

                // Mengubah warna teks dan backgroundTint btnHistoryPinjam menjadi hijau
                b.btnHistoryPinjam.setTextColor(Color.parseColor("#FFFFFF"))
                b.btnHistoryPinjam.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00CC08")))

                b.frameLayout.visibility = View.GONE
            }
            R.id.btnHistoryBayar -> {

                b.btnHistoryPinjam.setTextColor(Color.parseColor("#2196F3"))
                b.btnHistoryPinjam.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1A1A1A")))

                b.btnHistoryTabungan.setTextColor(Color.parseColor("#2196F3"))
                b.btnHistoryTabungan.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1A1A1A")))

                b.btnHistoryBayar.setTextColor(Color.parseColor("#FFFFFF"))
                b.btnHistoryBayar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00CC08")))

                ft = childFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, fragHistoryBayar)
                ft.addToBackStack(null)
                ft.commit()
                b.frameLayout.visibility = View.VISIBLE
            }
            R.id.btnHistoryTabungan -> {

                // Mengubah warna teks dan backgroundTint btnHistoryPinjam menjadi biru
                b.btnHistoryPinjam.setTextColor(Color.parseColor("#2196F3"))
                b.btnHistoryPinjam.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1A1A1A")))

                // Mengubah warna teks dan backgroundTint btnHistoryBayar menjadi biru
                b.btnHistoryBayar.setTextColor(Color.parseColor("#2196F3"))
                b.btnHistoryBayar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1A1A1A")))

                // Mengubah warna teks dan backgroundTint btnHistoryTabungan menjadi hijau
                b.btnHistoryTabungan.setTextColor(Color.parseColor("#FFFFFF"))
                b.btnHistoryTabungan.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00CC08")))

                ft = childFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, fragHistoryTabungan)
                ft.addToBackStack(null) // Tambahkan transaksi ke dalam back stack
                ft.commit()
                b.frameLayout.visibility = View.VISIBLE
            }
        }
    }
}