package ardiansyah.dwinur.aldy.koperasi.anggota

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryTabunganBinding

class AdapterTabungan(
    val dataTabungan : List<HashMap<String,String>>,
    val b : FragHistoryTabunganBinding
) : RecyclerView.Adapter<AdapterTabungan.HolderDataTabungan>() {
    inner class  HolderDataTabungan(v : View): RecyclerView.ViewHolder(v){
        val txItemIdHistoryTabunganAnggota = v.findViewById<TextView>(R.id.txItemIdHistoryTabunganAnggota)
        val txItemTglHistoryTabunganAnggota = v.findViewById<TextView>(R.id.txItemTglHistoryTabunganAnggota)
        val txItemNominalHistoryTabunganAnggota = v.findViewById<TextView>(R.id.txItemNominalHistoryTabunganAnggota)
    }

    val url = "http://192.168.241.103/koperasi/koperasi.php"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataTabungan {
        val v  = LayoutInflater.from(parent.context).inflate(R.layout.row_tabungan,parent,false)
        return HolderDataTabungan(v)
    }

    override fun getItemCount(): Int {
        return  dataTabungan.size
    }

    override fun onBindViewHolder(h: AdapterTabungan.HolderDataTabungan, position: Int) {
        val data = dataTabungan[position]
        h.txItemIdHistoryTabunganAnggota.text = data["id"]
        h.txItemTglHistoryTabunganAnggota.text = data["tgl_tabung"]
        h.txItemNominalHistoryTabunganAnggota.text = data["nominal_tabung"]
    }
}