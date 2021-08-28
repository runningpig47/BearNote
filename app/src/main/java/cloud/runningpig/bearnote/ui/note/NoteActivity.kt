package cloud.runningpig.bearnote.ui.note

import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ActivityNoteBinding
import cloud.runningpig.bearnote.logic.utils.Injector
import com.google.android.material.tabs.TabLayoutMediator

class NoteActivity : AppCompatActivity() {

    private val viewModel: SpendingViewModel by viewModels {
        Injector.providerSpendingViewModelFactory(this)
    }

    private lateinit var binding: ActivityNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.noteViewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 3

            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> SpendingFragment.newInstance(0)
                1 -> SpendingFragment.newInstance(1)
                else -> TransferFragment()
            }
        }
        binding.noteViewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.page.value = position
            }
        })
        TabLayoutMediator(binding.noteTabLayout, binding.noteViewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.spending)
                1 -> tab.text = getString(R.string.income)
                else -> tab.text = getString(R.string.transfer)
            }
        }.attach()
    }

}