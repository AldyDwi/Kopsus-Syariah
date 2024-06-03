package ardiansyah.dwinur.aldy.koperasi.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivityDataAdminBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray


class DataAdminActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var b : ActivityDataAdminBinding

    val url = "http://192.168.241.103/koperasi/koperasi.php"
    val daftarAdmin = mutableListOf<HashMap<String,String>>()
    lateinit var adapter : AdapterAdmin


    fun showDataAdmin(){
        val  result = object : StringRequest(
            Method.POST,url,
            Response.Listener {
                daftarAdmin.clear()
                val arrayBrg = JSONArray(it)
                for (x in 0 until arrayBrg.length()){
                    val jsonObject  = arrayBrg.getJSONObject(x)
                    val hm = HashMap<String,String>()
                    hm.put("id",jsonObject.getString("id"))
                    hm.put("nama_lengkap",jsonObject.getString("nama_lengkap"))
                    hm.put("nama_jabatan",jsonObject.getString("nama_jabatan"))
                    hm.put("tgl_lahir",jsonObject.getString("tgl_lahir"))
                    hm.put("no_hp",jsonObject.getString("no_hp"))
                    hm.put("email",jsonObject.getString("email"))
                    hm.put("password",jsonObject.getString("password"))
                    hm.put("jenis_kelamin",jsonObject.getString("jenis_kelamin"))
                    hm.put("alamat",jsonObject.getString("alamat"))
                    hm.put("url",jsonObject.getString("url"))
                    daftarAdmin.add(hm)
                }
                adapter.notifyDataSetChanged()
            },
            Response.ErrorListener {
                Toast.makeText(this,"${it.message}",
                    Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("command","selectKaryawan")
                return hm
            }
        }
        val q = Volley.newRequestQueue(this)
        q.add(result)
    }

    override fun onStart() {
        super.onStart()
        showDataAdmin()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityDataAdminBinding.inflate(layoutInflater)
        setContentView(b.root)

        adapter= AdapterAdmin(daftarAdmin,b)
        b.rvAdmin.adapter = adapter
        b.rvAdmin.layoutManager = LinearLayoutManager(this)

        b.btnAnggotaBack.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAnggotaBack -> {
                val intent = Intent(this, KelolaAdminActivity::class.java)
                startActivity(intent)
            }
        }
    }
}