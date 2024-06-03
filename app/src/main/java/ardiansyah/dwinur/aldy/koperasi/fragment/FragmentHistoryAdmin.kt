package ardiansyah.dwinur.aldy.koperasi.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.admin.AdapterPinjamAdmin
import ardiansyah.dwinur.aldy.koperasi.admin.HomeAdminActivity
import ardiansyah.dwinur.aldy.koperasi.admin.KelolaAdminActivity
import ardiansyah.dwinur.aldy.koperasi.anggota.AdapterPinjam
import ardiansyah.dwinur.aldy.koperasi.anggota.HomeAnggotaActivity
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryAdminBinding
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import java.text.NumberFormat
import java.util.Locale

class FragmentHistoryAdmin : Fragment(), View.OnClickListener {
    lateinit var b : FragHistoryAdminBinding
    lateinit var thisParent : HomeAdminActivity
    lateinit var v : View

    lateinit var fragHistoryBayarAdmin : FragmentHistoryBayarAdmin
    lateinit var fragHistoryTabunganAdmin : FragmentHistoryTabunganAdmin
    lateinit var ft : FragmentTransaction

    val url = "http://192.168.241.103/koperasi/koperasi.php"
    val daftarPinjamAdmin = mutableListOf<HashMap<String,String>>()
    lateinit var adapter : AdapterPinjamAdmin

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        thisParent = activity as HomeAdminActivity
        b = FragHistoryAdminBinding.inflate(layoutInflater)
        v = b.root

        adapter= AdapterPinjamAdmin(daftarPinjamAdmin,b)
        b.rvPinjamAdmin.adapter = adapter
        b.rvPinjamAdmin.layoutManager = LinearLayoutManager(thisParent)

        showPinjam()

        b.btnAdminBack.setOnClickListener(this)
        b.btnHistoryPinjamAdmin.setOnClickListener(this)
        b.btnHistoryBayarAdmin.setOnClickListener(this)
        b.btnHistoryTabunganAdmin.setOnClickListener(this)
        fragHistoryBayarAdmin = FragmentHistoryBayarAdmin()
        fragHistoryTabunganAdmin = FragmentHistoryTabunganAdmin()

        return v
    }

    private fun showPinjam() {
        val  result = object : StringRequest(
            Method.POST,url,
            Response.Listener {
                daftarPinjamAdmin.clear()
                val arrayBrg = JSONArray(it)
                for (x in 0 until arrayBrg.length()){
                    val jsonObject  = arrayBrg.getJSONObject(x)
                    val hm = HashMap<String,String>()
                    hm.put("id",jsonObject.getString("id"))
                    hm.put("nama_lengkap",jsonObject.getString("nama_lengkap"))
                    hm.put("tgl_pinjam",jsonObject.getString("tgl_pinjam"))
                    hm.put("waktu_pinjam",jsonObject.getString("waktu_pinjam"))
                    val nominalPinjam = jsonObject.getDouble("nominal_pinjam")
                    hm["nominal_pinjam"] = formatCurrency(nominalPinjam)
                    daftarPinjamAdmin.add(hm)
                }
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener {
                Toast.makeText(thisParent,"${it.message}",
                    Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("command","selectPinjamAdmin")
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
            R.id.btnAdminBack -> {
                val intent = Intent(thisParent, HomeAdminActivity::class.java)
                startActivity(intent)
            }
            R.id.btnHistoryPinjamAdmin -> {

                // Mengubah warna teks dan backgroundTint btnHistoryBayar menjadi biru
                b.btnHistoryBayarAdmin.setTextColor(Color.parseColor("#2196F3"))
                b.btnHistoryBayarAdmin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1A1A1A")))

                // Mengubah warna teks dan backgroundTint btnHistoryTabungan menjadi biru
                b.btnHistoryTabunganAdmin.setTextColor(Color.parseColor("#2196F3"))
                b.btnHistoryTabunganAdmin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1A1A1A")))

                // Mengubah warna teks dan backgroundTint btnHistoryPinjam menjadi hijau
                b.btnHistoryPinjamAdmin.setTextColor(Color.parseColor("#FFFFFF"))
                b.btnHistoryPinjamAdmin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00CC08")))

                b.frameLayout.visibility = View.GONE
            }
            R.id.btnHistoryBayarAdmin -> {

                b.btnHistoryPinjamAdmin.setTextColor(Color.parseColor("#2196F3"))
                b.btnHistoryPinjamAdmin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1A1A1A")))

                b.btnHistoryTabunganAdmin.setTextColor(Color.parseColor("#2196F3"))
                b.btnHistoryTabunganAdmin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1A1A1A")))

                b.btnHistoryBayarAdmin.setTextColor(Color.parseColor("#FFFFFF"))
                b.btnHistoryBayarAdmin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00CC08")))

                ft = childFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, fragHistoryBayarAdmin)
                ft.addToBackStack(null)
                ft.commit()
                b.frameLayout.visibility = View.VISIBLE
            }
            R.id.btnHistoryTabunganAdmin -> {

                // Mengubah warna teks dan backgroundTint btnHistoryPinjam menjadi biru
                b.btnHistoryPinjamAdmin.setTextColor(Color.parseColor("#2196F3"))
                b.btnHistoryPinjamAdmin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1A1A1A")))

                // Mengubah warna teks dan backgroundTint btnHistoryBayar menjadi biru
                b.btnHistoryBayarAdmin.setTextColor(Color.parseColor("#2196F3"))
                b.btnHistoryBayarAdmin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1A1A1A")))

                // Mengubah warna teks dan backgroundTint btnHistoryTabungan menjadi hijau
                b.btnHistoryTabunganAdmin.setTextColor(Color.parseColor("#FFFFFF"))
                b.btnHistoryTabunganAdmin.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00CC08")))

                ft = childFragmentManager.beginTransaction()
                ft.replace(R.id.frameLayout, fragHistoryTabunganAdmin)
                ft.addToBackStack(null) // Tambahkan transaksi ke dalam back stack
                ft.commit()
                b.frameLayout.visibility = View.VISIBLE
            }
        }
    }
}