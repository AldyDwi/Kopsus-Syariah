package ardiansyah.dwinur.aldy.koperasi.admin

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivityDataAdminBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject

class AdapterAdmin(
    val dataAdmin : List<HashMap<String,String>>,
    val b : ActivityDataAdminBinding
) : RecyclerView.Adapter<AdapterAdmin.HolderDataAdmin>() {
    inner class  HolderDataAdmin(v : View):RecyclerView.ViewHolder(v){
        val txItemIdAdmin = v.findViewById<TextView>(R.id.txItemIdAdmin)
        val txItemNamaAdmin = v.findViewById<TextView>(R.id.txItemNamaLengkapAdmin)
        val txItemJabatanAdmin = v.findViewById<TextView>(R.id.txItemJabatanAdmin)
        val txItemTglLahirAdmin = v.findViewById<TextView>(R.id.txItemTglLahirAdmin)
        val txItemJenisKelAdmin = v.findViewById<TextView>(R.id.txItemJenisKelaminAdmin)
        val txItemEmailAdmin = v.findViewById<TextView>(R.id.txItemEmailAdmin)
        val txItemPassAdmin = v.findViewById<TextView>(R.id.txItemPassAdmin)
        val txItemNoHpAdmin = v.findViewById<TextView>(R.id.txItemNoHpAdmin)
        val txItemAlamatAdmin = v.findViewById<TextView>(R.id.txItemAlamatAdmin)
        val photo = v.findViewById<ImageView>(R.id.imageView)
        val cllayout = v.findViewById<ConstraintLayout>(R.id.clRowAdmin)
    }

    val url = "http://192.168.241.103/koperasi/koperasi.php"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataAdmin {
        val v  = LayoutInflater.from(parent.context).inflate(R.layout.row_admin,parent,false)
        return HolderDataAdmin(v)
    }

    override fun getItemCount(): Int {
        return  dataAdmin.size
    }

    override fun onBindViewHolder(h: HolderDataAdmin, position: Int) {
        val data = dataAdmin[position]
        h.txItemIdAdmin.text = data["id"]
        h.txItemNamaAdmin.text = data["nama_lengkap"]
        h.txItemJabatanAdmin.text = data["nama_jabatan"]
        h.txItemTglLahirAdmin.text = data["tgl_lahir"]
        h.txItemJenisKelAdmin.text = data["jenis_kelamin"]
        h.txItemEmailAdmin.text = data["email"]
        h.txItemPassAdmin.text = data["password"]
        h.txItemNoHpAdmin.text = data["no_hp"]
        h.txItemAlamatAdmin.text = data["alamat"]
        if(!data.get("url").equals("")) {
            Picasso.get().load(data.get("url")).into(h.photo)
        }

        h.cllayout.setOnClickListener {
            val popupMenu = PopupMenu(h.cllayout.context, h.cllayout)
            popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.mnItemEdit -> {
                        val idAdmin = data["id"]?.toLong() ?: -1 // default value jika idAdmin tidak valid
                        val namaAdmin = data["nama_lengkap"] ?: ""
                        val namaJabatan = data["nama_jabatan"] ?: ""
                        val tglLahirAdmin = data["tgl_lahir"] ?: ""
                        val jenisKelamin = data["jenis_kelamin"] ?: ""
                        val alamatAdmin = data["alamat"] ?: ""
                        val noHpAdmin = data["no_hp"] ?: ""
                        val emailAdmin = data["email"] ?: ""
                        val passAdmin = data["password"] ?: ""
                        val urlGambar = data["url"] ?: ""

                        val intent = Intent(h.cllayout.context, KelolaAdminActivity::class.java).apply {
                            putExtra("idAdmin", idAdmin)
                            putExtra("namaAdmin", namaAdmin)
                            putExtra("namaJabatan", namaJabatan)
                            putExtra("tglLahirAdmin", tglLahirAdmin)
                            putExtra("jenisKelamin", jenisKelamin)
                            putExtra("alamatAdmin", alamatAdmin)
                            putExtra("noHpAdmin", noHpAdmin)
                            putExtra("emailAdmin", emailAdmin)
                            putExtra("passAdmin", passAdmin)
                            putExtra("gambar", urlGambar)
                        }
                        h.cllayout.context.startActivity(intent)
                        true
                    }
                    R.id.mnItemDelete -> {
                        val result = object : StringRequest(
                            Method.POST, url,
                            Response.Listener {
                                val jsonObject = JSONObject(it)
                                val kode = jsonObject.getString("kode")
                                if (kode == "200") {
                                    Toast.makeText(h.cllayout.context, "Karyawan dihapus", Toast.LENGTH_SHORT).show()
                                    // Refresh data setelah menghapus admin jika diperlukan
                                } else {
                                    Toast.makeText(h.cllayout.context, "Gagal menghapus Karyawan", Toast.LENGTH_SHORT).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(h.cllayout.context, it.message, Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            override fun getParams(): MutableMap<String, String>? {
                                val hm = HashMap<String, String>()
                                hm["command"] = "deleteKaryawan"
                                hm.put("id", h.txItemIdAdmin.text.toString())
                                return hm
                            }
                        }
                        val q = Volley.newRequestQueue(h.cllayout.context)
                        q.add(result)

                        val intent = Intent(h.cllayout.context, KelolaAnggotaActivity::class.java).apply {

                        }
                        h.cllayout.context.startActivity(intent)

                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

}