package ardiansyah.dwinur.aldy.koperasi.admin

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivityKelolaJabatanBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale

class KelolaJabatanActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var b : ActivityKelolaJabatanBinding
    val url = "http://192.168.241.103/koperasi/koperasi.php"
    val daftarJabatan = mutableListOf<HashMap<String,String>>()
    lateinit var adapter : AdapterJabatan

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnInsJabatan -> {
                insertUpdateDelete("insertJabatan")
            }
            R.id.btnUpdJabatan -> {
                insertUpdateDelete("updateJabatan")
            }
            R.id.btnDeleteJabatan -> {
                insertUpdateDelete("deleteJabatan")
            }
            R.id.btnAnggotaBack -> {
                val intent = Intent(this, HomeAdminActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun insertUpdateDelete(command : String) {
        val result = object : StringRequest(
            Method.POST, url,
            Response.Listener {
                val jsonObject = JSONObject(it)
                val kode = jsonObject.getString("kode")
                if(kode.equals("200")) {
                    Toast.makeText(this, "Operasi Sukses", Toast.LENGTH_SHORT).show()

                    b.txIdJabatan.setText("0")
                    b.edNamaJabatan.setText("")
                } else {
                    Toast.makeText(this, "Operasi Gagal", Toast.LENGTH_SHORT).show()
                }
                showDataJabatan()
            },
            Response.ErrorListener {
                Toast.makeText(this,"${it.message}",
                    Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(command) {
                    "insertJabatan" -> {
                        hm.put("command", "insertJabatan")
                        hm.put("nama_jabatan", b.edNamaJabatan.text.toString())
                    }
                    "updateJabatan" -> {
                        hm.put("command", "updateJabatan")
                        hm.put("id", b.txIdJabatan.text.toString())
                        hm.put("nama_jabatan", b.edNamaJabatan.text.toString())
                    }
                    "deleteJabatan" -> {
                        hm.put("command", "deleteJabatan")
                        hm.put("id", b.txIdJabatan.text.toString())
                    }
                }
                return hm
            }
        }
        val q = Volley.newRequestQueue(this)
        q.add(result)
    }

    fun showDataJabatan(){
        val  result = object : StringRequest(
            Method.POST,url,
            Response.Listener {
                daftarJabatan.clear()
                val arrayBrg = JSONArray(it)
                for (x in 0 until arrayBrg.length()){
                    val jsonObject  = arrayBrg.getJSONObject(x)
                    val hm = HashMap<String,String>()
                    hm.put("id",jsonObject.getString("id"))
                    hm.put("nama_jabatan",jsonObject.getString("nama_jabatan"))
                    daftarJabatan.add(hm)
                }
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener {
                Toast.makeText(this,"${it.message}",
                    Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("command","selectJabatan")
                return hm
            }
        }
        val q = Volley.newRequestQueue(this)
        q.add(result)
    }

    override fun onStart() {
        super.onStart()
        showDataJabatan()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityKelolaJabatanBinding.inflate(layoutInflater)
        setContentView(b.root)

        adapter = AdapterJabatan(daftarJabatan,b)
        b.rvJabatan.adapter = adapter
        b.rvJabatan.layoutManager = LinearLayoutManager(this)

        b.btnInsJabatan.setOnClickListener(this)
        b.btnUpdJabatan.setOnClickListener(this)
        b.btnDeleteJabatan.setOnClickListener(this)
        b.btnAnggotaBack.setOnClickListener(this)
    }
}