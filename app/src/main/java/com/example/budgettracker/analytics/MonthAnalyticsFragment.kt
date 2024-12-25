package com.example.budgettracker.analytics

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgettracker.MainViewModel
import com.example.budgettracker.R
import com.example.budgettracker.databinding.FragmentCategoryAnalyticsBinding
import com.example.budgettracker.operations.OperationsAdapter
import com.example.budgettracker.operations.OperationsData
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Calendar


class MonthAnalyticsFragment : Fragment() {

    private var _binding : FragmentCategoryAnalyticsBinding? = null
    private val binding get() = _binding!!
    private var calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        _binding = FragmentCategoryAnalyticsBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.GONE
        binding.categoryOperationsRV.layoutManager = LinearLayoutManager(context)

        val months = resources.getStringArray(R.array.months)

        binding.categoryName.text = ""
        binding.monthText.text = "Total for ${months[viewModel.analyzedMonthIndexForBar]}"


        viewModel.operationsList.observe(viewLifecycleOwner, Observer {
            val monthOperations = ArrayList<OperationsData>()
            var operationsByMonth = ArrayList<ArrayList<OperationsData>>()
            when (viewModel.typeOfAnalyzedOperation) {
                3 -> { operationsByMonth = viewModel.divideOperationsByMonth(viewModel.allIncomes) }
                2 -> { operationsByMonth = viewModel.divideOperationsByMonth(viewModel.allExpenses) }
            }
            for (operationsForMonth in operationsByMonth) {
                calendar.time = operationsForMonth[0].date
                if (calendar.get(Calendar.MONTH) == viewModel.analyzedMonthIndexForBar
                    && calendar.get(Calendar.YEAR) == viewModel.selectedYear) {
                    for (element in operationsForMonth) {
                        monthOperations.add(element)
                    }
                }
            }
            var totalAmount = monthOperations.sumOf { it.amount.toDouble() }
            binding.monthTotal.text = totalAmount.toString()
            binding.categoryOperationsRV.adapter = OperationsAdapter(monthOperations, findNavController(), viewModel)
        })

        binding.back.setOnClickListener {
            findNavController().popBackStack()
        }

        return root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}