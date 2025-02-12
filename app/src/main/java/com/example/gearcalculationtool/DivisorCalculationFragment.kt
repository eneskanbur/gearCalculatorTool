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
    lateinit var editTextGearTeeth: EditText
    lateinit var spinnerRatioOfMovement: Spinner
    lateinit var gridLayout: GridLayout
    lateinit var buttonCalculation: Button
    var K : Double = 0.0
    var Z : Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
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
            if (gearTeethInput.isNotEmpty() && K != 0.0) {
                Z = gearTeethInput.toDouble()
                // Temel oran: K/Z (örneğin 4/9). Buradan 10 adet oransal örnek oluşturacağız:
                // 1. örnek: K*1 / Z*1, 2. örnek: K*2 / Z*2, …, 10. örnek: K*10 / Z*10
                val fractions = generateScaledFractions(K.toInt(), Z.toInt(), 10)
                displayFractions(fractions)
            }
        }

    }

    fun createSpinnerOptions(spinner: Spinner) {
        val spinnerItems = arrayOf("Seçiniz", "1/40", "1/80", "1/90")

        // ArrayAdapter ile Spinner'ı doldur
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            spinnerItems
        )
        spinner.adapter = adapter

        // Spinner'da bir öğe seçildiğinde yapılacak işlemleri tanımla
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Seçilen öğeyi al
                val selectedItem = spinnerItems[position]

                // Seçilen öğeye göre işlem yap
                when (selectedItem) {
                    "1/40" -> {
                        K = 40.0
                    }

                    "1/80" -> {
                        K = 80.0
                    }

                    "1/90" -> {
                        K = 90.0
                    }

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }
    private fun generateScaledFractions(numerator: Int, denominator: Int, count: Int = 10): List<Pair<Int, Int>> {
        val scaledFractions = mutableListOf<Pair<Int, Int>>()
        for (i in 1..count) {
            scaledFractions.add(Pair(numerator * i, denominator * i))
        }
        return scaledFractions
    }

    private fun displayFractions(fractions: List<Pair<Int, Int>>) {
        gridLayout.removeAllViews()
        gridLayout.rowCount = 5
        gridLayout.columnCount = 2

        fractions.forEachIndexed { index, fraction ->
            val num = fraction.first
            val den = fraction.second
            val wholePart = num / den
            val remainder = num % den
            val fractionText = if (wholePart > 0) {
                if (remainder > 0) "$wholePart tam $remainder/$den" else "$wholePart"
            } else {
                "$num/$den"
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
}