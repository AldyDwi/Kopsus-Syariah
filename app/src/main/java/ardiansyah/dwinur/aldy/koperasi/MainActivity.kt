package ardiansyah.dwinur.aldy.koperasi

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import android.content.Intent
import android.content.SharedPreferences
import ardiansyah.dwinur.aldy.koperasi.admin.AdapterAdmin
import ardiansyah.dwinur.aldy.koperasi.admin.HomeAdminActivity
import ardiansyah.dwinur.aldy.koperasi.anggota.HomeAnggotaActivity
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivityMainBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale


class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var b: ActivityMainBinding

    val url = "http://192.168.241.103/koperasi/koperasi.php"
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnLogin.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnLogin -> {
                val email = b.edEmailLogin.text.toString()
                val password = b.edPasswordLogin.text.toString()

//                val intent = Intent(this, HomeAdminActivity::class.java)
//                startActivity(intent)

                if (LoginPengguna("selectKaryawanLogin", email, password)) {

                } else if (LoginPengguna("selectAnggotaLogin", email, password)) {

                } else {
                    // Tampilkan notifikasi bahwa email atau password salah
//                    Toast.makeText(this, "Email atau password salah", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun LoginPengguna(command: String, email: String, password: String): Boolean {
        val result = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                try {
                    val jsonArray = JSONArray(response)
                    if (jsonArray.length() > 0) {
                        val sharedPreferences = getSharedPreferences("user_data", Context.MODE_PRIVATE)
                        val userData = jsonArray.getJSONObject(0)

                        // Simpan data ke SharedPreferences berdasarkan jenis pengguna
                        val editor = sharedPreferences.edit()
                        if (command == "selectAnggotaLogin") {
                            editor.putString("id", userData.getString("id"))
                            editor.putString("nama_lengkap", userData.getString("nama_lengkap"))
                            editor.putString("tgl_lahir", userData.getString("tgl_lahir"))
                            editor.putString("jenis_kelamin", userData.getString("jenis_kelamin"))
                            editor.putString("alamat", userData.getString("alamat"))
                            editor.putString("no_hp", userData.getString("no_hp"))
                            editor.putString("nik", userData.getString("nik"))
                            editor.putString("no_rekening", userData.getString("no_rekening"))
                            editor.putString("email", userData.getString("email"))
                            editor.putString("password", userData.getString("password"))
                        }

                        // Commit perubahan ke SharedPreferences
                        editor.apply()

                        // Lakukan navigasi ke halaman selanjutnya
                        if (command == "selectKaryawanLogin") {
                            val intent = Intent(this, HomeAdminActivity::class.java)
                            startActivity(intent)
                            finish() // Selesai dengan activity login

                            b.edEmailLogin.setText("")
                            b.edPasswordLogin.setText("")
                        } else if (command == "selectAnggotaLogin") {
                            val intent = Intent(this, HomeAnggotaActivity::class.java)
                            startActivity(intent)
                            finish() // Selesai dengan activity login

                            b.edEmailLogin.setText("")
                            b.edPasswordLogin.setText("")
                        }


                    } else {
//                        Toast.makeText(this, "Login Gagal", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: JSONException) {
                    // Tangani kesalahan parsing JSON
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                // Tangani kesalahan jaringan atau kesalahan server
                Toast.makeText(this, "${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(command) {
                    "selectKaryawanLogin" -> {
                        hm.put("command", "selectKaryawanLogin")
                        hm.put("email", email)
                        hm.put("password", password)

                    } "selectAnggotaLogin" -> {
                    hm.put("command", "selectAnggotaLogin")
                    hm.put("email", email)
                    hm.put("password", password)

                    }
                }
                return hm
            }
        }
        val q = Volley.newRequestQueue(this)
        q.add(result)

        // Return false by default
        return false
    }
}
