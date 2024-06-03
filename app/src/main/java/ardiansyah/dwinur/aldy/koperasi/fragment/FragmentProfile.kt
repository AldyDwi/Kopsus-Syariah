package ardiansyah.dwinur.aldy.koperasi.fragment

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ardiansyah.dwinur.aldy.koperasi.anggota.HomeAnggotaActivity
import ardiansyah.dwinur.aldy.koperasi.MainActivity
import ardiansyah.dwinur.aldy.koperasi.R
import ardiansyah.dwinur.aldy.koperasi.databinding.FragProfileBinding
import java.text.NumberFormat
import java.util.Locale

class FragmentProfile : Fragment(), View.OnClickListener {
    lateinit var b : FragProfileBinding
    lateinit var thisParent : HomeAnggotaActivity
    lateinit var v : View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {

        thisParent = activity as HomeAnggotaActivity
        b = FragProfileBinding.inflate(layoutInflater)
        v = b.root

        b.btnLogoutProfile.setOnClickListener(this)

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil data anggota dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val namaLengkap = sharedPref.getString("nama_lengkap", "")
        val tglLahir = sharedPref.getString("tgl_lahir", "")
        val jenisKelamin = sharedPref.getString("jenis_kelamin", "")
        val alamat = sharedPref.getString("alamat", "")
        val noHp = sharedPref.getString("no_hp", "")
        val noRek = sharedPref.getString("no_rekening", "")
        val nik = sharedPref.getString("nik", "")
        val email = sharedPref.getString("email", "")
        val pass = sharedPref.getString("password", "")
        val totalPeminjaman = sharedPref.getDouble("total_peminjaman")
        val totalTabungan = sharedPref.getDouble("total_tabungan")

        // Tampilkan data anggota pada tampilan XML FragmentProfile
        val txNama: TextView = view.findViewById(R.id.txNamaProfile)
        val txTglLahir: TextView = view.findViewById(R.id.txTglLahirProfile)
        val txJenisKel: TextView = view.findViewById(R.id.txJenisKelProfile)
        val txAlamat: TextView = view.findViewById(R.id.txAlamatProfile)
        val txNoHp: TextView = view.findViewById(R.id.txNoHpProfile)
        val txNoRek: TextView = view.findViewById(R.id.txNoRekProfile)
        val txNik: TextView = view.findViewById(R.id.txNikProfile)
        val txEmail: TextView = view.findViewById(R.id.txEmailProfile)
        val txPass: TextView = view.findViewById(R.id.txPassProfile)
        val txPinjam: TextView = view.findViewById(R.id.txPinjamProfile)
        val txTabungan: TextView = view.findViewById(R.id.txTabunganProfile)

        txNama.text = "$namaLengkap"
        txTglLahir.text = "$tglLahir"
        txJenisKel.text = "$jenisKelamin"
        txAlamat.text = "$alamat"
        txNoHp.text = "$noHp"
        txNoRek.text = "$noRek"
        txNik.text = "nik. $nik"
        txEmail.text = "$email"
        txPass.text = "$pass"
        txPinjam.text = "${formatCurrency(totalPeminjaman)}"
        txTabungan.text = "${formatCurrency(totalTabungan)}"



        // Temukan button di layout FragmentProfile
//        val btnLogoutProfile: Button = view.findViewById(R.id.btnLogoutProfile)
//        // Tambahkan listener pada button
//        btnLogoutProfile.setOnClickListener {
//            // Buat intent untuk menuju MainActivity
//            val intent = Intent(requireContext(), MainActivity::class.java)
//            // Mulai aktivitas MainActivity
//            startActivity(intent)
//        }
    }

    fun SharedPreferences.getDouble(key: String): Double {
        return java.lang.Double.longBitsToDouble(getLong(key, java.lang.Double.doubleToRawLongBits(0.0)))
    }

    fun formatCurrency(amount: Double): String {
        val formatter: NumberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
        return formatter.format(amount)
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnLogoutProfile -> {
                val intent = Intent(thisParent, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }
}