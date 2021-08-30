package cloud.runningpig.bearnote.ui.note.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ActivityAddCategoryBinding
import cloud.runningpig.bearnote.logic.model.Icon
import cloud.runningpig.bearnote.logic.model.IconMap
import cloud.runningpig.bearnote.logic.utils.Injector
import cloud.runningpig.bearnote.logic.utils.showToast
import cloud.runningpig.bearnote.ui.note.SpendingViewModel
import java.util.*

class AddCategoryActivity : AppCompatActivity() {
    private var page = 0
    private val list = LinkedList<Icon>()
    private var iconName: String = "ic1"
    private var sort: Int = 0
    private var order: Int = Int.MAX_VALUE

    init {
        IconMap.map.keys.forEach {
            val icon = Icon(it)
            list.add(icon)
        }
        list.removeLast()
    }

    private val viewModel: SpendingViewModel by viewModels {
        Injector.providerSpendingViewModelFactory(this)
    }

    private lateinit var binding: ActivityAddCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddCategoryBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        page = intent.getIntExtra("page", 0)
        sort = if (page == 0) 0 else 1
        val titleTextView = binding.aclTitleLayout.findViewById<TextView>(R.id.title_textView)
        val titleTextView2 = binding.aclTitleLayout.findViewById<TextView>(R.id.title_textView2)
        if (page == 0) {
            titleTextView.text = "添加支出类别"
        } else {
            titleTextView.text = "添加收入类别"
        }
        val adapter = ACAListAdapter { position ->
            iconName = list[position].iconName
            binding.imageView4.setImageResource(IconMap.map[iconName] ?: R.drawable.ic_error)
            binding.aacRecyclerView.scrollToPosition(position)
        }
        binding.aacRecyclerView.adapter = adapter
        adapter.submitList(list)
        viewModel.queryMaxOrder(sort).observe(this) {
            order = it ?: 0
            titleTextView2.visibility = View.VISIBLE
            titleTextView2.setOnClickListener {
                addNewItem()
            }
        }
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(binding.aacEditText.text.toString())
    }

    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                binding.aacEditText.text.toString(),
                iconName,
                sort,
                order
            )
            finish()
        } else {
            "填写错误".showToast()
        }
    }

}