package cloud.runningpig.bearnote.ui.chart

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import cloud.runningpig.bearnote.BearNoteApplication
import cloud.runningpig.bearnote.logic.BearNoteRepository
import cloud.runningpig.bearnote.databinding.FragmentChartSpendingBinding
import cloud.runningpig.bearnote.logic.dao.BearNoteDatabase
import cloud.runningpig.bearnote.logic.model.ChartMonthBean
import cloud.runningpig.bearnote.ui.detail.DetailViewModel
import cloud.runningpig.bearnote.ui.detail.DetailViewModelFactory
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener

private const val ARG_PARAM1 = "page"

class ChartSpendingFragment : Fragment() {

    private var _binding: FragmentChartSpendingBinding? = null
    private val binding get() = _binding!!
    private var page: Int = 0
    private lateinit var adapter: CSFListAdapter

    val viewModel: DetailViewModel by viewModels {
        val repository = BearNoteRepository.getInstance(
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteCategoryDao(),
            BearNoteDatabase.getDatabase(BearNoteApplication.context).noteDao()
        )
        DetailViewModelFactory(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            page = it.getInt(ARG_PARAM1, 0)
//            viewModel.page.value = param1
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartSpendingBinding.inflate(inflater, container, false)
        initPieChart()
        binding.fcsRecyclerView.layoutManager = LinearLayoutManager(context)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.queryByMonth(page).observe(this.viewLifecycleOwner) {
            viewModel.dataList.clear()
            viewModel.amount = 0.0
            viewModel.topFiveAmount = 0.0
            viewModel.otherCountCategoryId = 0
            for (i in it.indices) {
                if (i <= 4) { // 前5种类别单独显示出来
                    viewModel.topFiveAmount += it[i].sumNoteAmount
                    viewModel.dataList.add(it[i]) // listView展示的数据源
                } else {
                    viewModel.otherCountCategoryId += it[i].countCategoryId
                }
                viewModel.amount += it[i].sumNoteAmount
            }
            if (it.size > 5) {
                val chartMonthBean = ChartMonthBean("其他", "ic22", viewModel.otherCountCategoryId, viewModel.amount - viewModel.topFiveAmount)
                viewModel.dataList.add(chartMonthBean)
            }
            setData()
            // 相反，这里重新创建adapter屏幕不会闪，由于后台删除会闪退的缘故，以后解决
            adapter = CSFListAdapter()
            binding.fcsRecyclerView.adapter = adapter
            adapter.setAmount(viewModel.amount)
            adapter.submitList(null)
            adapter.submitList(viewModel.dataList)
        }
    }

    private fun initPieChart() {
        // 标签的设置(标签就是扇形图里的文字)
        binding.chart1.setEntryLabelColor(Color.WHITE)
        binding.chart1.setEntryLabelTextSize(14f)
        binding.chart1.setDrawEntryLabels(true)
        // 启动以百分比绘制
        binding.chart1.setUsePercentValues(true)
        binding.chart1.description.isEnabled = true
        binding.chart1.description.text = viewModel.generateDescriptionText(page)
        binding.chart1.description.textSize = 12f
        binding.chart1.description.textColor = Color.rgb(159, 159, 159)
        binding.chart1.description.xOffset = -30f
        // 饼心的设置
        // 显示饼心，默认显示
        binding.chart1.isDrawHoleEnabled = true
        // 设置饼心的颜色
        binding.chart1.setHoleColor(Color.WHITE)
        // 设置饼心的半径，默认为50%
        binding.chart1.holeRadius = 58f
        // 是否显示在饼心的文字
        binding.chart1.setDrawCenterText(true)
        // 设置饼心显示的文字
        binding.chart1.centerText = viewModel.generateCenterSpannableText()
        // 设置饼心字体大小
        binding.chart1.setCenterTextSize(14f)
        // 设置中心文本的偏移量
        binding.chart1.setCenterTextOffset(0f, 0f)
        // 透明圆的设置(即饼心旁边的的圆环)
        // 启动透明圆
        binding.chart1.isDrawHoleEnabled = true
        // 设置透明圆的半径，默认为比饼心的半径大5%
        binding.chart1.transparentCircleRadius = 61f
        // 设置透明圆的透明度，默认为100，255=不透明，0=全透明
        binding.chart1.setTransparentCircleAlpha(110)
        // 设置透明圆的颜色
        binding.chart1.setTransparentCircleColor(Color.WHITE)
        binding.chart1.rotationAngle = 0f
        // enable rotation of the chart by touch
        binding.chart1.isRotationEnabled = true
        binding.chart1.isHighlightPerTapEnabled = true
        // chart.setUnit(" €");
        // chart.setDrawUnitsInChart(true);
        // 设置图标变化监听
        binding.chart1.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
            }

            override fun onNothingSelected() {
            }
        })
        // 设置图例
        val legend: Legend = binding.chart1.legend
        // 设置图例的实际对齐方式
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        // 设置图例水平对齐方式
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
        // 设置图例方向
        legend.orientation = Legend.LegendOrientation.VERTICAL
        // 设置图例是否在图表内绘制
        legend.setDrawInside(false)
        // 设置水平图例之间的空间
        legend.xEntrySpace = 5f
        // 设置垂直轴上图例条目间的空间
        legend.yEntrySpace = 0f
        // 设置x轴偏移量
        legend.xOffset = 0f
        // 设置此轴上的标签使用的y轴偏移量。对于图例，*高偏移量意味着整个图例将被放置在离顶部*更远的地方。
        legend.yOffset = 0f
        // 设置字体大小
        legend.textSize = 20f
        legend.isEnabled = false
        // 设置动画
//        binding.chart1.animateXY(2000,2000)
        binding.chart1.animateY(500, Easing.EaseInOutQuad)
        // 其他属性的设置
        // 设置图表偏移量
        binding.chart1.setExtraOffsets(20f, 0f, 20f, 0f)
        // 设置可触摸
        binding.chart1.isRotationEnabled = false
        // *减速摩擦系数为[o];1] interval，数值越高*表示速度下降越慢，例如设置为o，则*立即停止。1为无效值，将自动转换为*0.999f。
        binding.chart1.dragDecelerationFrictionCoef = 0.95f
        binding.chart1.setCenterTextTypeface(Typeface.createFromAsset(context?.assets, "fonts/OpenSans-Light.ttf"))
    }

    private fun setData() {
        val entries = ArrayList<PieEntry>()
        for (i in 0 until viewModel.dataList.size) {
            entries.add(PieEntry((viewModel.dataList[i].sumNoteAmount / viewModel.amount).toFloat(), viewModel.dataList[i].categoryName))
        }
        val dataSet = PieDataSet(entries, "小熊饼状图")
        val colors = ArrayList<Int>()
        val colorsArray = intArrayOf(
            Color.rgb(192, 255, 140), Color.rgb(254, 149, 7), Color.rgb(255, 208, 140),
            Color.rgb(140, 234, 255), Color.rgb(255, 140, 157), Color.rgb(148, 212, 212)
        )
        for (i in colorsArray) {
            colors.add(i)
        }
        dataSet.colors = colors
        // 百分比文字设置
        dataSet.valueFormatter = PercentFormatter()
        dataSet.valueTextSize = 11f
        dataSet.valueTextColor = Color.BLACK
        dataSet.valueTypeface = Typeface.createFromAsset(context?.assets, "fonts/OpenSans-Regular.ttf")
        // 设置折现饼图
        // 设置折线的颜色
        dataSet.valueLineColor = Color.BLACK
        // 设置数据线距离图像内部园心的距离，以百分比来计算
        dataSet.valueLinePart1OffsetPercentage = 80f
        // 当valuePosition在外部时，表示行前半部分的长度(即折线靠近圆的那端长度)
        dataSet.valueLinePart1Length = 0.2f
        // 当valuePosition位于外部时，表示行后半部分的长度*(即折线靠近百分比那端的长度)
        dataSet.valueLinePart2Length = 0.4f
        // 设置Y值的位置在圆外
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        // 設置突出时的间距
        // 选中时突出的长度
        dataSet.selectionShift = 5f
        // 饼块之间的间隔
        dataSet.sliceSpace = 3f
        val data = PieData(dataSet)
        binding.chart1.data = data
        // 取消高亮显示
        binding.chart1.highlightValue(null)
        binding.chart1.invalidate()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: Int) =
            ChartSpendingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }

}