package org.macho.beforeandafter.graphe2

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import kotlinx.android.synthetic.main.fragment_graphe.*
import org.macho.beforeandafter.shared.BeforeAndAfterConst
import org.macho.beforeandafter.R
import org.macho.beforeandafter.shared.data.RecordDao
import java.text.SimpleDateFormat
import java.util.*

class GrapheFragment: Fragment() {
    private lateinit var frameLayout: FrameLayout
    private lateinit var scrollView: HorizontalScrollView
    private lateinit var grapheView: LineGrapheView

    private lateinit var format: SimpleDateFormat

    private var fromTime = 0L
    private var toTime = 0L
    private var unitX = 0L
    private var dataList: MutableList<Data> = mutableListOf()

    private var mode = GrapheFragment.GrapheMode.MONTH

    private var postLazyScroll = false

    companion object {
        fun getInstance(): Fragment {
            return GrapheFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = layoutInflater.inflate(R.layout.fragment_graphe, container, false)
        val lang = Locale.getDefault().language
        if ("ja".equals(lang)) {
            format = SimpleDateFormat("yyyy年MM月dd日")
        } else {
            format = SimpleDateFormat("MM/dd/yyyy")
        }

        loadData()

        toTime = System.currentTimeMillis() + mode.span / 2
        fromTime = toTime - mode.span

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        frameLayout = FrameLayout(context)

        refresh()

        linearLayout.addView(frameLayout)

        currentDate.text = format.format(Date())
    }

    private fun refresh() {
        unitX = mode.unit

        frameLayout.removeAllViews()
        scrollView = CustomHorizontalScrollView(context)
        grapheView = LineGrapheView(context!!, fromTime, toTime, unitX, dataList)
        scrollView.addView(grapheView)
        frameLayout.addView(scrollView)
        frameLayout.addView(ScaleView(context, dataList))

        // scrollview.post(scrollView::scrollTo(x, y))だと、ガクンとなるため以下の実装
        frameLayout.viewTreeObserver.addOnPreDrawListener(object: ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                scrollView.viewTreeObserver.removeOnPreDrawListener(this)

                val initialPositionX = (grapheView.width - scrollView.width) / 2
                scrollView.scrollX = initialPositionX
                return false
            }
        })
    }

    private fun lazyScroll(currentDateTime: Long) {
        fromTime = currentDateTime - mode.span / 2
        toTime = currentDateTime + mode.span / 2
        postLazyScroll = true
        refresh()
    }

    private fun loadData() {
        dataList = mutableListOf()

        // 体重データと体脂肪率データを作る
        var weightPoints: MutableList<Poin2> = mutableListOf()

        var ratePoints: MutableList<Poin2> = mutableListOf()

        for (record in RecordDao.findAll()) {
            if (record.weight > 0) {
                weightPoints.add(Poin2(record.date, record.weight))
            }
            if (record.rate > 0) {
                ratePoints.add(Poin2(record.date, record.rate))
            }
        }

        val weightData = Data(weightPoints, true, Color.rgb(229, 57, 53))
        dataList.add(weightData)

        val rateData = Data(ratePoints, false, Color.rgb(67, 160, 71))
        dataList.add(rateData)

        val fromTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * 365 * 25
        val toTime = System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 365 * 25

        val preferences = PreferenceManager.getDefaultSharedPreferences(activity)

        // 体重の目標を表すデータを作る
        val weightGoal = preferences.getFloat("GOAL_WEIGHT", 50f)
        var weightGoalPoints: MutableList<Poin2> = mutableListOf()
        weightGoalPoints.add(Poin2(fromTime, weightGoal))
        weightGoalPoints.add(Poin2(toTime, weightGoal))
        val weightGoalData = Data(weightGoalPoints, true, Color.rgb(229, 83, 80), true)
        dataList.add(weightGoalData)

        // 体脂肪率の目標を表すデータ作る
        val rateGoal = preferences.getFloat("GOAL_RATE", 20f)
        var rateGoalPoints: MutableList<Poin2> = mutableListOf()
        rateGoalPoints.add(Poin2(fromTime, rateGoal))
        rateGoalPoints.add(Poin2(toTime, rateGoal))
        val rateGoalData = Data(rateGoalPoints, false, Color.rgb(129, 199, 132), true)
        dataList.add(rateGoalData)
    }

    enum class GrapheMode(val span: Long, val unit: Long) {
        MONTH(1000L *60 * 60 * 24 * 31 * 3, 1000L * 60 * 60 * 24)
    }

    inner class CustomHorizontalScrollView(context: Context?): HorizontalScrollView(context) {
        override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
            // scrollViewの端まで行ったときに、次の分を読み込む。
            val child = getChildAt(0) ?: return

            val position = l + width / 2
            val dateTime = (fromTime + (toTime - fromTime) * (position.toFloat() / child.width)).toLong()

            if (postLazyScroll) { // NOTE: GrapheFragment.lazyScroll直後のonScrollChangedイベントで、再びlazyScrollが実行されるのを防ぐため。
                postLazyScroll = false
            } else if (l == 0 || l == child.width - width) {
                scrollView.post { this@GrapheFragment.lazyScroll(dateTime) }
            }


            // ラベルをupdateする
            val text = format.format(Date(dateTime))
            currentDate.text = text

            var currentWeightValue = 0f
            val weightData = dataList.get(0)
            val weightPoints = weightData.poin2s
            for (i in 0 until weightPoints.size) {
                val point = weightPoints.get(i)
                if (dateTime == point.dateTime) {
                    currentWeightValue = point.value
                }
                if (i < weightPoints.size - 1) {
                    val point2 = weightPoints.get(i + 1)
                    if (point.dateTime < dateTime && dateTime < point2.dateTime) {
                        currentWeightValue = point.value + (point2.value - point.value) * (dateTime - point.dateTime) / (point2.dateTime - point.dateTime)
                    }
                }
            }
            currentWeight.setText(BeforeAndAfterConst.WEIGHT_FORMAT.format(currentWeightValue))

            var currentRateValue = 0f
            val rateData = dataList.get(1)
            val ratePoints = rateData.poin2s
            for (i in 0 until ratePoints.size) {
                val point = ratePoints.get(i)
                if (dateTime == point.dateTime) {
                    currentRateValue = point.value
                }
                if (i < ratePoints.size - 1) {
                    val point2 = ratePoints.get(i + 1)
                    if (point.dateTime < dateTime && dateTime < point2.dateTime) {
                        currentRateValue = point.value + (point2.value - point.value) * (dateTime - point.dateTime) / (point2.dateTime - point.dateTime)
                    }
                }
            }
            currentRate.setText(BeforeAndAfterConst.RATE_FORMAT.format(currentRateValue))
        }
    }
}