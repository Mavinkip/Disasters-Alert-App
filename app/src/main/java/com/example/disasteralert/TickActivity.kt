package com.example.disasteralert

import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class TickActivity : AppCompatActivity() {

    private lateinit var radioGroup: RadioGroup
    private lateinit var radioLevel1: RadioButton
    private lateinit var radioLevel2: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tick) // Ensure this layout contains the required views

        // Initialize views
        radioGroup = findViewById(R.id.radio_group) // Make sure this ID is correct in your XML
        radioLevel1 = findViewById(R.id.radio_level_1)
        radioLevel2 = findViewById(R.id.radio_level_2)

        // Set listener for RadioGroup
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            // Clear tick from all RadioButtons first
            radioLevel1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            radioLevel2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            when (checkedId) {
                R.id.radio_level_1 -> {
                    // Show tick icon for Level 1 on the right side
                    radioLevel1.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.drawable.tick_icon), null
                    )
                }
                R.id.radio_level_2 -> {
                    // Show tick icon for Level 2 on the right side
                    radioLevel2.setCompoundDrawablesWithIntrinsicBounds(
                        null, null,
                        ContextCompat.getDrawable(this, R.drawable.tick_icon), null
                    )
                }
            }
        }
    }
}
