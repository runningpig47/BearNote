package cloud.runningpig.bearnote.ui.note.category

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cloud.runningpig.bearnote.R
import cloud.runningpig.bearnote.databinding.ActivityCategoryListBinding
import com.google.android.material.tabs.TabLayoutMediator

class CategoryActivity : AppCompatActivity() {

    private var currentPage: Int = 0

    private lateinit var binding: ActivityCategoryListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryListBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.aclViewPager2.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = 2

            override fun createFragment(position: Int): Fragment = when (position) {
                0 -> CategoryListFragment.newInstance(0)
                else -> CategoryListFragment.newInstance(1)
            }
        }
        binding.aclViewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentPage = position
            }
        })
        TabLayoutMediator(binding.aclTabLayout, binding.aclViewPager2) { tab, position ->
            when (position) {
                0 -> tab.text = getString(R.string.spending)
                else -> tab.text = getString(R.string.income)
            }
        }.attach()
        val page = intent.getIntExtra("page", 0)
        binding.aclViewPager2.setCurrentItem(page, false)
        binding.aclTextView1.setOnClickListener {
            val intent = Intent(this, AddCategoryActivity::class.java)
            intent.putExtra("page", currentPage)
            startActivity(intent)
        }
    }
}