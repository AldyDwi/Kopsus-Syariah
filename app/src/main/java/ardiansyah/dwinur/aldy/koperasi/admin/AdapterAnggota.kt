package ardiansyah.dwinur.aldy.koperasi.admin

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivityDataAnggotaBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject

class AdapterAnggota(
    val dataAnggota : List<HashMap<String,String>>,
    val b : ActivityDataAnggotaBinding
) : RecyclerView.Adapter<AdapterAnggota.HolderDataAnggota>() {
    inner class  HolderDataAnggota(v : View): RecyclerView.ViewHolder(v){
        val txItemIdAnggota = v.findViewById<TextView>(R.id.txItemIdAnggota)
        val txItemNamaAnggota = v.findViewById<TextView>(R.id.txItemNamaLengkapAnggota)
        val txItemTglLahirAnggota = v.findViewById<TextView>(R.id.txItemTglLahirAnggota)
        val txItemJenisKelAnggota = v.findViewById<TextView>(R.id.txItemJenisKelaminAnggota)
        val txItemEmailAnggota = v.findViewById<TextView>(R.id.txItemEmailAnggota)
        val txItemPassAnggota = v.findViewById<TextView>(R.id.txItemPassAnggota)
        val txItemNoHpAnggota = v.findViewById<TextView>(R.id.txItemNoHpAnggota)
        val txItemAlamatAnggota = v.findViewById<TextView>(R.id.txItemAlamatAnggota)
        val txItemNikAnggota = v.findViewById<TextView>(R.id.txItemNikAnggota)
        val txItemNoRekAnggota = v.findViewById<TextView>(R.id.txItemNoRekAnggota)
        val photo = v.findViewById<ImageView>(R.id.imageView)
        val cllayout = v.findViewById<ConstraintLayout>(R.id.clRowAnggota)
    }

    val url = "http://192.168.241.103/koperasi/koperasi.php"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataAnggota {
        val v  = LayoutInflater.from(parent.context).inflate(R.layout.row_anggota,parent,false)
        return HolderDataAnggota(v)
    }

    override fun getItemCount(): Int {
        return  dataAnggota.size
    }

    override fun onBindViewHolder(h: HolderDataAnggota, position: Int) {
        val data = dataAnggota[position]
        h.txItemIdAnggota.text = data["id"]
        h.txItemNamaAnggota.text = data["nama_lengkap"]
        h.txItemTglLahirAnggota.text = data["tgl_lahir"]
        h.txItemJenisKelAnggota.text = data["jenis_kelamin"]
        h.txItemEmailAnggota.text = data["email"]
        h.txItemPassAnggota.text = data["password"]
        h.txItemNoHpAnggota.text = data["no_hp"]
        h.txItemAlamatAnggota.text = data["alamat"]
        h.txItemNikAnggota.text = data["nik"]
        h.txItemNoRekAnggota.text = data["no_rekening"]
        if(!data.get("url").equals("")) {
            Picasso.get().load(data.get("url")).into(h.photo)
        }

        h.cllayout.setOnClickListener {
            val popupMenu = PopupMenu(h.cllayout.context, h.cllayout)
            popupMenu.menuInflater.inflate(R.menu.menu_popup, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.mnItemEdit -> {
                        val idAnggota = data["id"]?.toLong() ?: -1 // default value jika idAdmin tidak valid
                        val namaAnggota = data["nama_lengkap"] ?: ""
                        val tglLahirAnggota = data["tgl_lahir"] ?: ""
                        val jenisKelamin = data["jenis_kelamin"] ?: ""
                        val alamatAnggota = data["alamat"] ?: ""
                        val noHpAnggota = data["no_hp"] ?: ""
                        val emailAnggota = data["email"] ?: ""
                        val passAnggota = data["password"] ?: ""
                        val nikAnggota = data["nik"] ?: ""
                        val noRekAnggota = data["no_rekening"] ?: ""
                        val urlGambar = data["url"] ?: ""

                        val intent = Intent(h.cllayout.context, KelolaAnggotaActivity::class.java).apply {
                            putExtra("idAnggota", idAnggota)
                            putExtra("namaAnggota", namaAnggota)
                            putExtra("tglLahirAnggota", tglLahirAnggota)
                            putExtra("jenisKelamin", jenisKelamin)
                            putExtra("alamatAnggota", alamatAnggota)
                            putExtra("noHpAnggota", noHpAnggota)
                            putExtra("emailAnggota", emailAnggota)
                            putExtra("passAnggota", passAnggota)
                            putExtra("nikAnggota", nikAnggota)
                            putExtra("noRekAnggota", noRekAnggota)
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
                                    Toast.makeText(h.cllayout.context, "Anggota dihapus", Toast.LENGTH_SHORT).show()
                                    // Refresh data setelah menghapus admin jika diperlukan
                                } else {
                                    Toast.makeText(h.cllayout.context, "Gagal menghapus anggota", Toast.LENGTH_SHORT).show()
                                }
                            },
                            Response.ErrorListener {
                                Toast.makeText(h.cllayout.context, it.message, Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            override fun getParams(): MutableMap<String, String>? {
                                val hm = HashMap<String, String>()
                                hm["command"] = "deleteAnggota"
                                hm.put("id", h.txItemIdAnggota.text.toString())
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