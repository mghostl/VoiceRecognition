package com.mghostl.education.android.voicerecognition

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.SimpleAdapter
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.wolfram.alpha.WAEngine
import com.wolfram.alpha.WAPlainText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.StringBuilder
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
        const val TITLE = "Title"
        const val CONTENT = "Content"
        const val VOICE_RECOGNITION_REQUEST_CODE = 777

    }

    private lateinit var waEngine: WAEngine

    private lateinit var requestInput: TextInputEditText

    private lateinit var podsAdapter: SimpleAdapter

    private lateinit var progressBar: ProgressBar

    private lateinit var textToSpeech: TextToSpeech

    private val pods = pods()


    private var isTtsReady = false

    private fun pods() = mutableListOf<HashMap<String, String>>()

    private fun podItem(title: String, content: String) =
        hashMapOf(TITLE to title, CONTENT to content)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initWolframEngine()
        initTts()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_stop -> {
                if (isTtsReady) {
                    textToSpeech.stop()
                }
                return true
            }
            R.id.action_clear -> {
                requestInput.text?.clear()
                pods.clear()
                podsAdapter.notifyDataSetChanged()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initWolframEngine() {
        waEngine = WAEngine().apply {
            appID = "PYUW98-P633PXY82P"
            addFormat("plaintext")
        }
    }

    private fun initTts() {
        textToSpeech = TextToSpeech(this) {
            if (it != TextToSpeech.SUCCESS) {
                Log.e(TAG, "TTS error code: $it")
                showSnackbar(getString(R.string.error_tts_is_not_ready))
            } else {
                isTtsReady = true
            }
            textToSpeech.language = Locale.US
        }
    }

    private fun showVoiceInputDialog() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )
            putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.request_hint))
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US)
        }

        kotlin.runCatching {
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE)
        }
            .onFailure {
                showSnackbar(it.message ?: getString(R.string.error_voice_recognition_unavailable))
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0)
                ?.let { question ->
                    requestInput.setText(question)
                    askWolfram(question)
                    Log.d(TAG, question)
                }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE)
            .apply {
                setAction(android.R.string.ok) {
                    dismiss()
                }
                show()
            }
    }

    private fun askWolfram(request: String) {
        progressBar.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val query = waEngine.createQuery(request)
            kotlin.runCatching {
                waEngine.performQuery(query)
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    if (it.isError) {
                        showSnackbar(it.errorMessage)
                        return@withContext
                    }
                    if (!it.isSuccess) {
                        requestInput.error = getString(R.string.error_do_not_understand)
                        return@withContext
                    }
                    for (pod in it.pods) {
                        if (pod.isError) continue
                        val content = StringBuilder()
                        for (subpod in pod.subpods) {
                            for (element in subpod.contents) {
                                if (element is WAPlainText) {
                                    content.append(element.text)
                                }
                            }
                        }
                        pods.add(0, podItem(pod.title, content.toString()))
                    }
                    podsAdapter.notifyDataSetChanged()
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    progressBar.visibility = View.GONE
                    // обработка ошибки
                    showSnackbar(it.message ?: getString(R.string.error_something_went_wrong))
                }

            }
        }
    }

    private fun initViews() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        requestInput = findViewById(R.id.text_input_edit)
        requestInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                pods.clear()
                podsAdapter.notifyDataSetChanged()
                val question = requestInput.text.toString()
                askWolfram(question)
            }
            return@setOnEditorActionListener false
        }

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
            pods.clear()
            podsAdapter.notifyDataSetChanged()
            if(isTtsReady) {
                textToSpeech.stop()
            }
            showVoiceInputDialog()

        }

        progressBar = findViewById(R.id.progress_bar)


        podsList.adapter = podsAdapter
        podsList.setOnItemClickListener { _, _, position, _ ->
            if (isTtsReady) {
                val pod = pods[position]
                val title = pod[TITLE]
                val content = pod[CONTENT]
                textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null, title)
            }
        }
    }
}