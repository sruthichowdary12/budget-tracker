package com.example.budgettracker.operations.transfer

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.budgettracker.R
import com.example.budgettracker.operations.OperationsData
import com.example.budgettracker.MainViewModel
import com.example.budgettracker.databinding.FragmentTransferBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class TransferFragment : Fragment() {

    private var _binding : FragmentTransferBinding? = null
    private val binding get() = _binding!!
    private lateinit var pickedDate: Date
    private var calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        _binding = FragmentTransferBinding.inflate(inflater, container, false)
        val root : View = binding.root

        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        navBar.visibility = View.GONE

        binding.transferButton.isChecked = true
        binding.transferButton.setTextColor(Color.WHITE)
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.incomeButton -> {
                    binding.incomeButton.setTextColor(Color.WHITE)
                    binding.expenseButton.setTextColor(Color.BLACK)
                    binding.transferButton.setTextColor(Color.BLACK)
                    findNavController().navigate(R.id.action_transferFragment_to_addIncomeFragment)
                }
                R.id.expenseButton -> {
                    binding.incomeButton.setTextColor(Color.BLACK)
                    binding.expenseButton.setTextColor(Color.WHITE)
                    binding.transferButton.setTextColor(Color.BLACK)
                    findNavController().navigate(R.id.action_transferFragment_to_addExpenseFragment)
                }
            }

        }

        var amountValue = "0"
        binding.amount.addTextChangedListener(
            object : TextWatcher{
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isNotEmpty()){
                        amountValue = s.toString()
                    }
                    else { amountValue = "0" }
                }
            }
        )
        var note = ""
        binding.note.addTextChangedListener(
            object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    if (s.toString().isNotEmpty()){
                        note = s.toString()
                    }
                    else { note = "" }
                }
            }
        )
        val today = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        pickedDate = today.time
        binding.dateButton.text = dateFormat.format(pickedDate)
        binding.dateButton.setOnClickListener {
            val datePickerDialog = DatePickerDialog(requireContext(), {DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                pickedDate = selectedDate.time
                binding.dateButton.text = dateFormat.format(pickedDate)
            },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)

            )
            datePickerDialog.show()
        }
        val types = ArrayList<String>()
        viewModel.allAccounts.observe(viewLifecycleOwner, Observer {
            for (element in it){
                types.add(element.name)
            }
        })



        val standatAdapter = ArrayAdapter(requireContext(), R.layout.account_spinner_layout, R.id.accountName, types)
        binding.accountTypeFrom.setAdapter(standatAdapter)
        binding.accountTypeTo.setAdapter(standatAdapter)
        val choosableTypes = types
        // choosen[0] - from, choosen[1] - to
        val choosen = arrayOf("", "")

        binding.accountTypeFrom.setOnItemClickListener { parent, view, position, id ->
            if (choosen[0] == "") {
                choosen[0] = choosableTypes[position]
                choosableTypes.remove(choosen[0])
            }
            else {
                choosableTypes.add(choosen[0])
                choosen[0] = choosableTypes[position]
                choosableTypes.remove(choosen[0])
            }

            val newAdapter = ArrayAdapter(requireContext(), R.layout.account_spinner_layout, R.id.accountName, choosableTypes)
            binding.accountTypeFrom.setAdapter(newAdapter)
            binding.accountTypeTo.setAdapter(newAdapter)
        }

        binding.accountTypeTo.setOnItemClickListener { parent, view, position, id ->
            if (choosen[1] == "") {
                choosen[1] = choosableTypes[position]
                choosableTypes.remove(choosen[1])
            }
            else {
                choosableTypes.add(choosen[1])
                choosen[1] = choosableTypes[position]
                choosableTypes.remove(choosen[1])
            }

            val newAdapter = ArrayAdapter(requireContext(), R.layout.account_spinner_layout, R.id.accountName, choosableTypes)
            binding.accountTypeFrom.setAdapter(newAdapter)
            binding.accountTypeTo.setAdapter(newAdapter)
        }

        binding.swap.setOnClickListener {
            choosen.reverse()
            binding.accountTypeFrom.setText(choosen[0], false)
            binding.accountTypeTo.setText(choosen[1], false)
        }



        binding.save.setOnClickListener {
            when {
                binding.accountTypeFrom.text.isEmpty() -> binding.accountFrom.error = "Enter account"
                binding.accountTypeTo.text.isEmpty() -> binding.accountTo.error = "Enter account"
                (binding.accountTypeTo.text.isNotEmpty() && binding.accountTypeFrom.text.isNotEmpty()) -> {
                    val operation = OperationsData(0, amountValue, R.drawable.up_right_arrow_icon,
                        choosen[1], "Transfer", pickedDate, choosen[0], choosen[1], false, 0, note)
                    viewModel.addOperation(operation)
                    findNavController().navigate(R.id.action_transferFragment_to_operations)
                }
            }
        }
        binding.back.setOnClickListener {
            findNavController().navigate(R.id.action_transferFragment_to_operations)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}