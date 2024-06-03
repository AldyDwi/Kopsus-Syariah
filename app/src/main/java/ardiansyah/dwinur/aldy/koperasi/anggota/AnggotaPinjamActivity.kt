package ardiansyah.dwinur.aldy.koperasi.anggota

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.ActivityAnggotaPinjamBinding

class AnggotaPinjamActivity: AppCompatActivity(), View.OnClickListener {

    lateinit var b : ActivityAnggotaPinjamBinding

    private val arrayPinjam = arrayOf("6", "12", "18", "24", "30", "36", "42", "48", "54", "60")
    private lateinit var adapterSpin : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAnggotaPinjamBinding.inflate(layoutInflater)
        setContentView(b.root)


        // menyiapkan adapter untuk Spinner
        adapterSpin = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, arrayPinjam)

        b.spPinjam.adapter = adapterSpin

        // menambahkan event OnItemSelected pada spinner
        b.spPinjam.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Toast.makeText(baseContext,"Tidak ada yang dipilih",
                    Toast.LENGTH_SHORT).show()
            }
        }

        b.btnAnggotaBack.setOnClickListener(this)
        b.btnLanjutkanPinjam.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnAnggotaBack -> {
                val intent = Intent(this, HomeAnggotaActivity::class.java)
                startActivity(intent)
            }
            R.id.btnLanjutkanPinjam -> {
                val nominalInput = b.edNominalPinjam.text.toString()
                if (nominalInput.isNotEmpty()) {

                    // Dapatkan nilai dari edNominalPinjam
                    val nominalPinjam = nominalInput.toDouble()

                    if (nominalPinjam >= 1000000) {
                        // Dapatkan nilai dari spBulanInt
                        val spBulanInt = adapterSpin.getItem(b.spPinjam.selectedItemPosition)!!.toInt()

                        // Buat intent untuk berpindah ke PinjamActivity
                        val intent = Intent(this, PinjamActivity::class.java)

                        // Tambahkan data ke intent
                        intent.putExtra("nominal_pinjam", nominalPinjam)
                        intent.putExtra("jangka_waktu", spBulanInt)

                        // Mulai aktivitas PinjamActivity dengan intent yang telah dibuat
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Minimal peminjaman Rp1.000.000,00", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "Masukkan nominal peminjaman!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}