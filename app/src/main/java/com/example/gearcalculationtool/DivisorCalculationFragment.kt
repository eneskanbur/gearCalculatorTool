package com.example.gearcalculationtool

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.Spinner
import android.widget.TextView

class DivisorCalculationFragment : Fragment() {
    private lateinit var editTextGearTeeth: EditText
    private lateinit var spinnerRatioOfMovement: Spinner
    private lateinit var gridLayout: GridLayout
    private lateinit var buttonCalculation: Button
    private var movementRatio: Double = 0.0
    private var gearTeethCount: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_divisor_calculation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextGearTeeth = view.findViewById(R.id.editTextDivisorGearTeethNumber)
        buttonCalculation = view.findViewById(R.id.buttonDivisorCalculation)
        gridLayout = view.findViewById(R.id.gridLayoutDivisorCalculation)
        spinnerRatioOfMovement = view.findViewById(R.id.spinnerDivisorRatioOfMovement)


        createSpinnerOptions(spinnerRatioOfMovement)



        buttonCalculation.setOnClickListener {
            val gearTeethInput = editTextGearTeeth.text.toString()
            if (gearTeethInput.isNotEmpty() && movementRatio != 0.0) {
                gearTeethCount = gearTeethInput.toDouble()

                val fractions = generateScaledFractions(movementRatio.toInt(), gearTeethCount.toInt(), 10)
                displayFractions(fractions)
            }
        }

    }

    private fun createSpinnerOptions(spinner: Spinner) {
        val spinnerItems = arrayOf("Seçiniz", "1/40", "1/80", "1/90")

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            spinnerItems
        )
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = spinnerItems[position]

                when (selectedItem) {
                    getString(R.string.spinnerFirstItem) -> {
                        movementRatio = 40.0
                    }

                    getString(R.string.spinnerSecondItem) -> {
                        movementRatio = 80.0
                    }

                    getString(R.string.spinnerThirdItem) -> {
                        movementRatio = 90.0
                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun generateScaledFractions(
        numerator: Int,
        denominator: Int,
        count: Int = 10
    ): List<Pair<Int, Int>> {
        val scaledFractions = mutableListOf<Pair<Int, Int>>()

        var simplifiedNumerator = numerator
        var simplifiedDenominator = denominator

        val pair = simplifyFraction(numerator, denominator)
        simplifiedNumerator = pair.first
        simplifiedDenominator = pair.second

        for (i in 1..count) {
            scaledFractions.add(Pair(simplifiedNumerator * i, simplifiedDenominator * i))
        }
        return scaledFractions
    }



    private fun displayFractions(fractions: List<Pair<Int, Int>>) {
        gridLayout.removeAllViews()
        gridLayout.rowCount = 5
        gridLayout.columnCount = 2

        fractions.forEachIndexed { index, fraction ->
            val numerator = fraction.first
            val denominator = fraction.second
            val wholePart = numerator / denominator
            val remainder = numerator % denominator

            val fractionText = if (wholePart > 0) {
                if (remainder > 0) "$wholePart tam $remainder/$denominator" else "$wholePart"
            } else {
                "$numerator/$denominator"
            }

            val textView = TextView(requireContext())
            textView.text = fractionText
            textView.textSize = 20f
            textView.setPadding(10, 10, 10, 10)
            textView.gravity = Gravity.CENTER

            val row = index / 2
            val col = index % 2
            val params = GridLayout.LayoutParams()
            params.rowSpec = GridLayout.spec(row, 1f)
            params.columnSpec = GridLayout.spec(col, 1f)
            params.width = 0
            params.height = 0

            textView.layoutParams = params
            gridLayout.addView(textView)
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
        require(numerator >= 0) { "Pay pozitif olmalı." }
        require(denominator > 0) { "Payda pozitif olmalı." }

        val gcd = calculateGCD(numerator, denominator)
        val simplifiedNumerator = numerator / gcd
        val simplifiedDenominator = denominator / gcd

        return Pair(simplifiedNumerator, simplifiedDenominator)
    }
}