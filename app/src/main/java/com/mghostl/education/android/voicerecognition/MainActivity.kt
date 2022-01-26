package com.mghostl.education.android.voicerecognition
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SimpleAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val TITLE = "Title"
        const val CONTENT = "Content"
    }
    
    lateinit var requestInput: TextInputEditText
    
    lateinit var podsAdapter: SimpleAdapter

    lateinit var progressBar: ProgressBar
    
    private val pods = pods()

    private fun pods() = (1..4)
        .map { podItem("Title $it", "Content $it") }
        .toMutableList()

    private fun podItem(title: String, content: String) = hashMapOf(TITLE to title, CONTENT to content)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_stop -> {
                Log.d(TAG, "actions_stop")
                return true
            }
            R.id.action_clear -> {
                Log.d(TAG, "actions_clear")
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun initViews() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        
        requestInput = findViewById(R.id.text_input_edit)
        
        val podsList: ListView = findViewById(R.id.pods_list)
        
        podsAdapter = SimpleAdapter(
            applicationContext,
            pods,
            R.layout.item_pod,
            arrayOf(TITLE, CONTENT),
            intArrayOf(R.id.title, R.id.content)
        )

        val voiceInputButton: FloatingActionButton = findViewById(R.id.voice_input_button)
        voiceInputButton.setOnClickListener {
            Log.d(TAG, "FAB")
        }

        progressBar  = findViewById(R.id.progress_bar)


        podsList.adapter = podsAdapter
    }
}