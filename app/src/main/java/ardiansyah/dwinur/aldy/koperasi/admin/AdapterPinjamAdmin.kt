package ardiansyah.dwinur.aldy.koperasi.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.anggota.AdapterPinjam
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryAdminBinding
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryBinding

class AdapterPinjamAdmin(
    val dataPinjamAdmin : List<HashMap<String,String>>,
    val b : FragHistoryAdminBinding
) : RecyclerView.Adapter<AdapterPinjamAdmin.HolderDataPinjamAdmin>() {
    inner class  HolderDataPinjamAdmin(v : View): RecyclerView.ViewHolder(v){
        val txItemIdHistoryPinjamAdmin = v.findViewById<TextView>(R.id.txItemIdHistoryPinjamAdmin)
        val txItemNamaHistoryPinjamAdmin = v.findViewById<TextView>(R.id.txItemNamaHistoryPinjamAdmin)
        val txItemTglHistoryPinjamAdmin = v.findViewById<TextView>(R.id.txItemTglHistoryPinjamAdmin)
        val txItemWaktuHistoryPinjamAdmin = v.findViewById<TextView>(R.id.txItemWaktuHistoryPinjamAdmin)
        val txItemNominalHistoryPinjamAdmin = v.findViewById<TextView>(R.id.txItemNominalHistoryPinjamAdmin)
//        val cllayout = v.findViewById<ConstraintLayout>(R.id.clRowPinjam)
    }

    val url = "http://192.168.241.103/koperasi/koperasi.php"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataPinjamAdmin {
        val v  = LayoutInflater.from(parent.context).inflate(R.layout.row_pinjam_admin,parent,false)
        return HolderDataPinjamAdmin(v)
    }

    override fun getItemCount(): Int {
        return  dataPinjamAdmin.size
    }

    override fun onBindViewHolder(h: AdapterPinjamAdmin.HolderDataPinjamAdmin, position: Int) {
        val data = dataPinjamAdmin[position]
        h.txItemIdHistoryPinjamAdmin.text = data["id"]
        h.txItemTglHistoryPinjamAdmin.text = data["tgl_pinjam"]
        h.txItemNamaHistoryPinjamAdmin.text = data["nama_lengkap"]
        h.txItemWaktuHistoryPinjamAdmin.text = data["waktu_pinjam"]
        h.txItemNominalHistoryPinjamAdmin.text = data["nominal_pinjam"]
    }
}