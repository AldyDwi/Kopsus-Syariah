package ardiansyah.dwinur.aldy.koperasi.admin

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivityKelolaJabatanBinding
import com.squareup.picasso.Picasso

class AdapterJabatan(
    val dataJabatan : List<HashMap<String,String>>,
    val b : ActivityKelolaJabatanBinding
) : RecyclerView.Adapter<AdapterJabatan.HolderDataJabatan>(){
    inner class  HolderDataJabatan(v : View): RecyclerView.ViewHolder(v){
        val txItemIdJabatan = v.findViewById<TextView>(R.id.txItemIdJabatan)
        val txItemNamaJabatan = v.findViewById<TextView>(R.id.txItemNamaJabatan)
        val cllayout = v.findViewById<ConstraintLayout>(R.id.clRowJabatan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderDataJabatan {
        val v  = LayoutInflater.from(parent.context).inflate(R.layout.row_jabatan,parent,false)
        return HolderDataJabatan(v)
    }

    override fun getItemCount(): Int {
        return  dataJabatan.size
    }

    override fun onBindViewHolder(h: AdapterJabatan.HolderDataJabatan, position: Int) {
        val data = dataJabatan.get(position)
        h.txItemIdJabatan.setText(data.get("id").toString())
        h.txItemNamaJabatan.setText(data.get("nama_jabatan").toString())
        h.cllayout.setOnClickListener(View.OnClickListener {
            b.txIdJabatan.setText(data.get("id").toString())
            b.edNamaJabatan.setText(data.get("nama_jabatan").toString())
        })
    }
}