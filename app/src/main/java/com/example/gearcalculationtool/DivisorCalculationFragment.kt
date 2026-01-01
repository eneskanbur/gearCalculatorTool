package com.example.gearcalculationtool

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class DivisorCalculationFragment : Fragment() {
    private lateinit var textInputLayoutGearTeeth: TextInputLayout
    private lateinit var editTextGearTeeth: TextInputEditText
    private lateinit var spinnerRatioOfMovement: Spinner
    private lateinit var gridLayout: GridLayout
    private lateinit var buttonCalculation: MaterialButton
    private lateinit var cardResultsSection: MaterialCardView
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

        try {
            textInputLayoutGearTeeth = view.findViewById(R.id.textInputLayoutGearTeeth)
            editTextGearTeeth = view.findViewById(R.id.editTextDivisorGearTeethNumber)
            buttonCalculation = view.findViewById(R.id.buttonDivisorCalculation)
            gridLayout = view.findViewById(R.id.gridLayoutDivisorCalculation)
            spinnerRatioOfMovement = view.findViewById(R.id.spinnerDivisorRatioOfMovement)
            cardResultsSection = view.findViewById(R.id.cardResultsSection)

            cardResultsSection.visibility = View.GONE

            createSpinnerOptions(spinnerRatioOfMovement)

            buttonCalculation.setOnClickListener {
                performCalculation()
            }
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Başlatma hatası: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun performCalculation() {
        val gearTeethInput = editTextGearTeeth.text.toString()

        textInputLayoutGearTeeth.error = null

        if (gearTeethInput.isEmpty()) {
            textInputLayoutGearTeeth.error = "Lütfen diş sayısını girin"
            Toast.makeText(requireContext(), "Lütfen diş sayısını girin", Toast.LENGTH_SHORT).show()
            return
        }

        if (movementRatio == 0.0) {
            Toast.makeText(requireContext(), "Lütfen hareket iletim oranını seçin", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            gearTeethCount = gearTeethInput.toDouble()

            if (gearTeethCount <= 0) {
                textInputLayoutGearTeeth.error = "Diş sayısı pozitif olmalıdır"
                Toast.makeText(requireContext(), "Diş sayısı pozitif olmalıdır", Toast.LENGTH_SHORT).show()
                return
            }

            val fractions = generateScaledFractions(movementRatio.toInt(), gearTeethCount.toInt(), 10)
            displayFractions(fractions)

            cardResultsSection.visibility = View.VISIBLE
            cardResultsSection.alpha = 0f
            cardResultsSection.animate()
                .alpha(1f)
                .setDuration(300)
                .start()

        } catch (e: NumberFormatException) {
            textInputLayoutGearTeeth.error = "Geçerli bir sayı girin"
            Toast.makeText(requireContext(), "Geçerli bir sayı girin", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Hesaplama hatası: ${e.message}", Toast.LENGTH_LONG).show()
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
                movementRatio = when (position) {
                    1 -> 40.0
                    2 -> 80.0
                    3 -> 90.0
                    else -> 0.0
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                movementRatio = 0.0
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
        try {
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
                textView.textSize = 18f

                val paddingPx = (16 * resources.displayMetrics.density).toInt()
                textView.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
                textView.gravity = Gravity.CENTER

                textView.setTextColor(
                    MaterialColors.getColor(
                        requireContext(),
                        com.google.android.material.R.attr.colorOnSurface,
                        Color.BLACK
                    )
                )

                val row = index / 2
                val col = index % 2
                val params = GridLayout.LayoutParams()
                params.rowSpec = GridLayout.spec(row, 1f)
                params.columnSpec = GridLayout.spec(col, 1f)
                params.width = 0
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT

                val marginPx = (8 * resources.displayMetrics.density).toInt()
                params.setMargins(marginPx, marginPx, marginPx, marginPx)

                textView.layoutParams = params
                gridLayout.addView(textView)
            }

            gridLayout.requestLayout()
            gridLayout.invalidate()

        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Sonuç gösterme hatası: ${e.message}", Toast.LENGTH_LONG).show()
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