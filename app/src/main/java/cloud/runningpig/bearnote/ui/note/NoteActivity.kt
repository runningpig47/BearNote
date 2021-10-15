package cloud.runningpig.bearnote.ui.note

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ActivityNoteBinding
import cloud.runningpig.bearnote.logic.utils.Injector
import cloud.runningpig.bearnote.ui.BaseActivity
import com.google.android.material.tabs.TabLayoutMediator

class NoteActivity : BaseActivity() {

    private val viewModel: SpendingViewModel by viewModels {
        Injector.providerSpendingViewModelFactory(this)
    }

    private lateinit var binding: ActivityNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        viewModel.noteId = intent.getIntExtra("noteId", -1)
        val categorySort = intent.getIntExtra("categorySort", 0)
        viewModel.categorySort = categorySort
        binding.noteViewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> SpendingFragment.newInstance(0)
                else -> SpendingFragment.newInstance(1)
            }
        }
        binding.noteViewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.page.value = position
            }
        })
        binding.noteViewPager2.offscreenPageLimit = 1
        TabLayoutMediator(binding.noteTabLayout, binding.noteViewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.spending)
                else -> tab.text = getString(R.string.income)
            }
        }.attach()
        if (categorySort == 0) {
            binding.noteViewPager2.currentItem = 0
        } else {
            binding.noteViewPager2.currentItem = 1
        }
    }

}