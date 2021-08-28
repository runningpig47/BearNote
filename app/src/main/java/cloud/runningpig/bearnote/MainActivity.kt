package cloud.runningpig.bearnote


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import cloud.runningpig.bearnote.databinding.ActivityMainBinding
import cloud.runningpig.bearnote.ui.assets.AssetsFragment
import cloud.runningpig.bearnote.ui.chart.ChartFragment
import cloud.runningpig.bearnote.ui.detail.DetailFragment
import cloud.runningpig.bearnote.ui.note.NoteActivity
import cloud.runningpig.bearnote.ui.personal.PersonalFragment

class MainActivity : AppCompatActivity() {

    private lateinit var fragments: ArrayList<Fragment>
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        initData()
        initView()
        initEvent()
    }

    private fun initData() {
        fragments = ArrayList()
        val detailFragment = DetailFragment()
        val bundle = Bundle()
        bundle.putString("title", getString(R.string.detail))
        detailFragment.arguments = bundle

        val chartFragment = ChartFragment()
        bundle.clear()
        bundle.putString("title", getString(R.string.chart))
        chartFragment.arguments = bundle

        val assetsFragment = AssetsFragment()
        bundle.clear()
        bundle.putString("title", getString(R.string.assets))
        assetsFragment.arguments = bundle

        val personalFragment = PersonalFragment()
        bundle.clear()
        bundle.putString("title", getString(R.string.personal))
        personalFragment.arguments = bundle

        fragments.add(detailFragment)
        fragments.add(chartFragment)
        fragments.add(assetsFragment)
        fragments.add(personalFragment)
    }

    private fun initView() {
        binding.bottomNavigationView.itemIconTintList = null
        val fragmentAdapter = FragmentAdapter(supportFragmentManager, lifecycle, fragments)
        binding.viewPager2.adapter = fragmentAdapter
    }

    private fun initEvent() {
        var previousPosition = 0
        var currentPosition: Int
        binding.bottomNavigationView.setOnItemSelectedListener {
            currentPosition = when (it.itemId) {
                R.id.i_detail -> 0
                R.id.i_chart -> 1
                R.id.i_assets -> 2
                R.id.i_personal -> 3
                else -> return@setOnItemSelectedListener false
            }
            if (previousPosition != currentPosition) {
                binding.viewPager2.setCurrentItem(currentPosition, false)
                previousPosition = currentPosition
            }
            true
        }
        binding.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                var position2 = position
                if (position2 >= 2) {
                    position2 += 1
                }
                binding.bottomNavigationView.selectedItemId =
                    binding.bottomNavigationView.menu.getItem(position2).itemId
            }
        })
        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
        }
    }

    class FragmentAdapter(
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle,
        private val fragments: MutableList<Fragment>
    ) : FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int = fragments.size
        override fun createFragment(position: Int): Fragment = fragments[position]
    }

}