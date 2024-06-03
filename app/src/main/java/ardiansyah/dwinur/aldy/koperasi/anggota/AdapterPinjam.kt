package ardiansyah.dwinur.aldy.koperasi.anggota

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.admin.AdapterAdmin
import ardiansyah.dwinur.aldy.koperasi.admin.KelolaAdminActivity
import ardiansyah.dwinur.aldy.koperasi.databinding.FragHistoryBinding
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class AdapterPinjam(
    val dataPinjam : List<HashMap<String,String>>,
    val b : FragHistoryBinding
) : RecyclerView.Adapter<AdapterPinjam.HolderDataPinjam>() {
    inner class  HolderDataPinjam(v : View): RecyclerView.ViewHolder(v){
        val txItemIdHistoryPinjamAnggota = v.findViewById<TextView>(R.id.txItemIdHistoryPinjamAnggota)
        val txItemTglHistoryPinjamAnggota = v.findViewById<TextView>(R.id.txItemTglHistoryPinjamAnggota)
        val txItemWaktuHistoryPinjamAnggota = v.findViewById<TextView>(R.id.txItemWaktuHistoryPinjamAnggota)
        val txItemNominalHistoryPinjamAnggota = v.findViewById<TextView>(R.id.txItemNominalHistoryPinjamAnggota)
//        val cllayout = v.findViewById<ConstraintLayout>(R.id.clRowPinjam)
    }

    val url = "http://192.168.241.103/koperasi/koperasi.php"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataPinjam {
        val v  = LayoutInflater.from(parent.context).inflate(R.layout.row_pinjam,parent,false)
        return HolderDataPinjam(v)
    }

    override fun getItemCount(): Int {
        return  dataPinjam.size
    }

    override fun onBindViewHolder(h: AdapterPinjam.HolderDataPinjam, position: Int) {
        val data = dataPinjam[position]
        h.txItemIdHistoryPinjamAnggota.text = data["id"]
        h.txItemTglHistoryPinjamAnggota.text = data["tgl_pinjam"]
        h.txItemWaktuHistoryPinjamAnggota.text = data["waktu_pinjam"]
        h.txItemNominalHistoryPinjamAnggota.text = data["nominal_pinjam"]
    }
}