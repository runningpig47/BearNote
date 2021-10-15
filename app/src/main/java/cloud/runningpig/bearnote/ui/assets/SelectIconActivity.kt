package cloud.runningpig.bearnote.ui.assets

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ActivitySelectIconBinding
import cloud.runningpig.bearnote.logic.model.Icon
import cloud.runningpig.bearnote.ui.BaseActivity

class SelectIconActivity : BaseActivity() {

    private var _binding: ActivitySelectIconBinding? = null
    private val binding get() = _binding!!

    var icon: List<Icon> = listOf(
        Icon("ic100", "现金"),
        Icon("ic101", "支付宝"),
        Icon("ic102", "微信钱包"),
        Icon("ic103", "储蓄卡"),
        Icon("ic104", "信用卡"),
        Icon("ic105", "公交卡"),
        Icon("ic106", "饭卡"),
        Icon("ic107", "其他"),
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivitySelectIconBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.siaTitleLayout.findViewById<TextView>(R.id.title_textView).text = "选择账户图标"
        val startActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                finish()
            }
        }
        val adapter = SIAListAdapter {
            val intent = Intent(this, AddAssetActivity::class.java)
            intent.putExtra("iconName", icon[it].iconName)
            startActivity.launch(intent)
        }
        binding.siaRecyclerView.adapter = adapter
        adapter.submitList(icon)
    }

}