package `in`.co.dipankar.companion

import `in`.co.dipankar.companion.databinding.ActivityMainBinding
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
// Zoom https://fb.zoom.us/j/97187218878?pwd=dXhGdGNnWXlZeDFLVU44WlhIRHg1dz09#success
class IntentHandlerActivity(contentLayoutId: Int) : AppCompatActivity(contentLayoutId) {
    private lateinit var binding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
