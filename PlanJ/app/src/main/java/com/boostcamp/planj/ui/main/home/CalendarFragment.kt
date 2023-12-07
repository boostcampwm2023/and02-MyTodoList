package com.boostcamp.planj.ui.main.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.boostcamp.planj.data.model.SaveDate
import com.boostcamp.planj.databinding.FragmentCalendarBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CalendarFragment(private val onClickListener: OnClickListener) : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    val binding get() = _binding!!

    var pageIndex = 0
    lateinit var date: Date
    lateinit var adapter: CalendarAdapter

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initAdapter()

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.selectDate.collectLatest {
                    val calendar = Calendar.getInstance()
                    val day = calendar.get(Calendar.DAY_OF_WEEK)
                    val getArray = getWeek(day, it)
                    adapter.submitList(getArray)
                }
            }
        }

    }

    fun initView() {
        pageIndex -= Int.MAX_VALUE / 2
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_MONTH, pageIndex)
        date = calendar.time
        calendar.add(Calendar.DATE, 1 - (calendar.get(Calendar.DAY_OF_WEEK)))

    }

    fun initAdapter() {
        val calendar = Calendar.getInstance()
        calendar.time = date
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        val getArray = getWeek(
            day,
            "${calendar.get(Calendar.YEAR)}-${
                String.format(
                    "%02d",
                    calendar.get(Calendar.MONTH) + 1
                )
            }-${String.format("%02d", calendar.get(Calendar.DATE))}"
        )

        adapter = CalendarAdapter(onClickListener)
        binding.calendarView.adapter = adapter
        binding.calendarView.itemAnimator = null
        adapter.submitList(getArray)
    }

    private fun getWeek(day: Int, selectDate: String): List<SaveDate> {
        val c = Calendar.getInstance()
        c.time = date
        c.add(Calendar.DATE, 1 - day)
        val list = mutableListOf<SaveDate>()
        repeat(7) {
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH) + 1
            val dayOfMonth = c.get(Calendar.DAY_OF_MONTH)
            c.add(Calendar.DATE, 1)
            list.add(
                SaveDate(
                    year,
                    month,
                    dayOfMonth,
                    "${year}-${String.format("%02d", month)}-${
                        String.format(
                            "%02d",
                            dayOfMonth
                        )
                    }" == selectDate
                )
            )
        }

        return list

    }

}