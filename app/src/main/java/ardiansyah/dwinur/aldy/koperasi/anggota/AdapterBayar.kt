package ardiansyah.dwinur.aldy.koperasi.anggota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryBayarBinding

class AdapterBayar(
    val dataBayar : List<HashMap<String,String>>,
    val b : FragHistoryBayarBinding
) : RecyclerView.Adapter<AdapterBayar.HolderDataBayar>() {
    inner class  HolderDataBayar(v : View): RecyclerView.ViewHolder(v){
        val txItemIdHistoryBayarAnggota = v.findViewById<TextView>(R.id.txItemIdHistoryBayarAnggota)
        val txItemTglHistoryBayarAnggota = v.findViewById<TextView>(R.id.txItemTglHistoryBayarAnggota)
        val txItemNominalHistoryPinjamAnggota = v.findViewById<TextView>(R.id.txItemNominalHistoryPinjamB)
        val txItemNominalHistoryBayarAnggota = v.findViewById<TextView>(R.id.txItemNominalHistoryBayarAnggota)
    }

    val url = "http://192.168.241.103/koperasi/koperasi.php"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataBayar {
        val v  = LayoutInflater.from(parent.context).inflate(R.layout.row_bayar,parent,false)
        return HolderDataBayar(v)
    }

    override fun getItemCount(): Int {
        return  dataBayar.size
    }

    override fun onBindViewHolder(h: AdapterBayar.HolderDataBayar, position: Int) {
        val data = dataBayar[position]
        h.txItemIdHistoryBayarAnggota.text = data["id"]
        h.txItemTglHistoryBayarAnggota.text = data["tgl_bayar"]
        h.txItemNominalHistoryBayarAnggota.text = data["nominal_bayar"]
        h.txItemNominalHistoryPinjamAnggota.text = data["nominal_pinjam"]
    }
}