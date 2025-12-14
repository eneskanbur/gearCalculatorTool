package com.example.gearcalculationtool

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gearcalculationtool.databinding.FragmentSpurAndHelicalGearCalculationBinding
import com.google.android.material.snackbar.Snackbar

class SpurAndHelicalGearCalculationFragment : Fragment() {

    private var _binding: FragmentSpurAndHelicalGearCalculationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSpurAndHelicalGearCalculationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonCalculate.setOnClickListener {
            calculate()
        }
    }

    private fun calculate() {
        val module = binding.editTextModule.text.toString().toDoubleOrNull()
        val numberOfTeeth = binding.editTextNumberOfTeeth.text.toString().toIntOrNull()
        val pressureAngle = binding.editTextPressureAngle.text.toString().toDoubleOrNull()
        val helixAngle = binding.editTextHelixAngle.text.toString().toDoubleOrNull()

        if (module == null || numberOfTeeth == null || pressureAngle == null) {
            binding.cardViewResult.visibility = View.GONE
            Snackbar.make(binding.root, getString(R.string.fill_required_fields), Snackbar.LENGTH_SHORT).show()
            return
        }

        // Placeholder calculation
        val result = "${getString(R.string.result_module)}: $module\n" +
                "${getString(R.string.result_number_of_teeth)}: $numberOfTeeth\n" +
                "${getString(R.string.result_pressure_angle)}: $pressureAngle\n" +
                "${getString(R.string.result_helix_angle)}: ${helixAngle ?: getString(R.string.result_not_applicable)}"

        binding.textViewResult.text = result
        binding.cardViewResult.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}