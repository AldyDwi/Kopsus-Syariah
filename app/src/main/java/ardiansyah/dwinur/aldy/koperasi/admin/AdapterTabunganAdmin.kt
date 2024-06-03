package ardiansyah.dwinur.aldy.koperasi.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.anggota.AdapterTabungan
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryTabunganAdminBinding
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryTabunganBinding

class AdapterTabunganAdmin(
    val dataTabunganAdmin : List<HashMap<String,String>>,
    val b : FragHistoryTabunganAdminBinding
) : RecyclerView.Adapter<AdapterTabunganAdmin.HolderDataTabunganAdmin>() {
    inner class  HolderDataTabunganAdmin(v : View): RecyclerView.ViewHolder(v){
        val txItemIdHistoryTabunganAdmin = v.findViewById<TextView>(R.id.txItemIdHistoryTabunganAdmin)
        val txItemNamaHistoryTabunganAdmin = v.findViewById<TextView>(R.id.txItemNamaHistoryTabunganAdmin)
        val txItemTglHistoryTabunganAdmin = v.findViewById<TextView>(R.id.txItemTglHistoryTabunganAdmin)
        val txItemNominalHistoryTabunganAdmin = v.findViewById<TextView>(R.id.txItemNominalHistoryTabunganAdmin)
    }

    val url = "http://192.168.241.103/koperasi/koperasi.php"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataTabunganAdmin {
        val v  = LayoutInflater.from(parent.context).inflate(R.layout.row_tabungan_admin,parent,false)
        return HolderDataTabunganAdmin(v)
    }

    override fun getItemCount(): Int {
        return  dataTabunganAdmin.size
    }

    override fun onBindViewHolder(h: AdapterTabunganAdmin.HolderDataTabunganAdmin, position: Int) {
        val data = dataTabunganAdmin[position]
        h.txItemIdHistoryTabunganAdmin.text = data["id"]
        h.txItemNamaHistoryTabunganAdmin.text = data["nama_lengkap"]
        h.txItemTglHistoryTabunganAdmin.text = data["tgl_tabung"]
        h.txItemNominalHistoryTabunganAdmin.text = data["nominal_tabung"]
    }
}