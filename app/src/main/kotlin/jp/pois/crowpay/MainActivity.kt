package jp.pois.crowpay

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.pois.crowpay.databinding.ActivityMainBinding
import jp.pois.crowpay.databinding.DialogNameConfigurationBinding
import jp.pois.crowpay.utils.endpointIdentifier
import jp.pois.crowpay.utils.initializeEndpointIdentifier
import jp.pois.crowpay.utils.isEndpointIdentifierInitialized

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = setContentView(this, R.layout.activity_main)

        val navHost = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment

        binding.bottomNavigation.setupWithNavController(navHost.navController)

        if (!isEndpointIdentifierInitialized) {
            val dialogBinding = DialogNameConfigurationBinding.inflate(layoutInflater)

            AlertDialog.Builder(this)
                .setView(dialogBinding.root)
                .setCancelable(false)
                .setPositiveButton(R.string.ok, null)
                .create()
                .apply {
                    setOnShowListener {
                        getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                            Log.d("DialogMainActivity", "positiveButton")
                            val name = dialogBinding.nameInput.text?.toString()
                            Log.d("DialogMainActivity", name.isNullOrBlank().toString())
                            if (name.isNullOrBlank()) {
                                dialogBinding.nameInputContainer.error = getString(R.string.name_error)
                            } else {
                                initializeEndpointIdentifier(name)
                                dismiss()
                            }
                        }
                    }
                }
                .show()
        } else {
            Log.d("mainActivity", endpointIdentifier.toString())
        }
    }
}
