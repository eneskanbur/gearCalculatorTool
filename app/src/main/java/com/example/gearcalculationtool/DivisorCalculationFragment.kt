package com.example.gearcalculationtool

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.GridLayout
import android.widget.TextView
import com.example.gearcalculationtool.databinding.FragmentDivisorCalculationBinding

class DivisorCalculationFragment : Fragment() {

    private var _binding: FragmentDivisorCalculationBinding? = null
    private val binding get() = _binding!!

    private var movementRatio: Double = 0.0
    private var gearTeethCount: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDivisorCalculationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDropdown()

        binding.buttonDivisorCalculation.setOnClickListener {
            val gearTeethInput = binding.editTextDivisorGearTeethNumber.text.toString()
            if (gearTeethInput.isNotEmpty() && movementRatio != 0.0) {
                gearTeethCount = gearTeethInput.toDouble()

                val fractions = generateScaledFractions(movementRatio.toInt(), gearTeethCount.toInt(), 10)
                displayFractions(fractions)
            }
        }
    }

    private fun setupDropdown() {
        val spinnerItems = arrayOf("1/40", "1/80", "1/90")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, spinnerItems)
        binding.autoCompleteTextViewDivisorRatioOfMovement.setAdapter(adapter)

        binding.autoCompleteTextViewDivisorRatioOfMovement.setOnItemClickListener { parent, _, position, _ ->
            val selectedItem = parent.getItemAtPosition(position) as String
            movementRatio = when (selectedItem) {
                "1/40" -> 40.0
                "1/80" -> 80.0
                "1/90" -> 90.0
                else -> 0.0
            }
        }
    }

    private fun generateScaledFractions(
        numerator: Int,
        denominator: Int,
        count: Int = 10
    ): List<Pair<Int, Int>> {
        val scaledFractions = mutableListOf<Pair<Int, Int>>()

        val (simplifiedNumerator, simplifiedDenominator) = simplifyFraction(numerator, denominator)

        for (i in 1..count) {
            scaledFractions.add(Pair(simplifiedNumerator * i, simplifiedDenominator * i))
        }
        return scaledFractions
    }

    private fun displayFractions(fractions: List<Pair<Int, Int>>) {
        binding.gridLayoutDivisorCalculation.removeAllViews()
        if (fractions.isNotEmpty()) {
            binding.textViewResultsTitle.visibility = View.VISIBLE
            binding.gridLayoutDivisorCalculation.rowCount = 5
            binding.gridLayoutDivisorCalculation.columnCount = 2

            fractions.forEachIndexed { index, fraction ->
                val numerator = fraction.first
                val denominator = fraction.second
                val wholePart = numerator / denominator
                val remainder = numerator % denominator

                val fractionText = if (wholePart > 0) {
                    if (remainder > 0) "$wholePart ${getString(R.string.fraction_whole)} $remainder/$denominator" else "$wholePart"
                } else {
                    "$numerator/$denominator"
                }

                val textView = TextView(requireContext()).apply {
                    text = fractionText
                    textSize = 20f
                    setPadding(10, 10, 10, 10)
                    gravity = Gravity.CENTER
                }

                val row = index / 2
                val col = index % 2
                val params =
                    GridLayout.LayoutParams(GridLayout.spec(row, 1f), GridLayout.spec(col, 1f)).apply {
                        width = 0
                        height = 0
                    }

                textView.layoutParams = params
                binding.gridLayoutDivisorCalculation.addView(textView)
            }
        } else {
            binding.textViewResultsTitle.visibility = View.GONE
        }
    }

    private fun calculateGCD(numerator: Int, denominator: Int): Int {
        var num1 = numerator
        var num2 = denominator

        while (num2 != 0) {
            val temp = num2
            num2 = num1 % num2
            num1 = temp
        }

        return num1
    }

    private fun simplifyFraction(numerator: Int, denominator: Int): Pair<Int, Int> {
        require(numerator >= 0) { "Numerator must be positive." }
        require(denominator > 0) { "Denominator must be positive." }

        val gcd = calculateGCD(numerator, denominator)
        val simplifiedNumerator = numerator / gcd
        val simplifiedDenominator = denominator / gcd

        return Pair(simplifiedNumerator, simplifiedDenominator)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}