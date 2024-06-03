package ardiansyah.dwinur.aldy.koperasi.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.anggota.AdapterBayar
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryBayarAdminBinding
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryBayarBinding

class AdapterBayarAdmin(
    val dataBayarAdmin : List<HashMap<String,String>>,
    val b : FragHistoryBayarAdminBinding
) : RecyclerView.Adapter<AdapterBayarAdmin.HolderDataBayarAdmin>() {
    inner class  HolderDataBayarAdmin(v : View): RecyclerView.ViewHolder(v){
        val txItemIdHistoryBayarAdmin = v.findViewById<TextView>(R.id.txItemIdHistoryBayarAdmin)
        val txItemNamaHistoryBayarAdmin = v.findViewById<TextView>(R.id.txItemNamaHistoryBayarAdmin)
        val txItemTglHistoryBayarAdmin = v.findViewById<TextView>(R.id.txItemTglHistoryBayarAdmin)
        val txItemNominalHistoryPinjamAdmin = v.findViewById<TextView>(R.id.txItemNominalHistoryPinjamB2)
        val txItemNominalHistoryBayarAdmin = v.findViewById<TextView>(R.id.txItemNominalHistoryBayarAdmin)
    }

    val url = "http://192.168.241.103/koperasi/koperasi.php"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataBayarAdmin {
        val v  = LayoutInflater.from(parent.context).inflate(R.layout.row_bayar_admin,parent,false)
        return HolderDataBayarAdmin(v)
    }

    override fun getItemCount(): Int {
        return  dataBayarAdmin.size
    }

    override fun onBindViewHolder(h: AdapterBayarAdmin.HolderDataBayarAdmin, position: Int) {
        val data = dataBayarAdmin[position]
        h.txItemIdHistoryBayarAdmin.text = data["id"]
        h.txItemNamaHistoryBayarAdmin.text = data["nama_lengkap"]
        h.txItemTglHistoryBayarAdmin.text = data["tgl_bayar"]
        h.txItemNominalHistoryBayarAdmin.text = data["nominal_bayar"]
        h.txItemNominalHistoryPinjamAdmin.text = data["nominal_pinjam"]
    }
}