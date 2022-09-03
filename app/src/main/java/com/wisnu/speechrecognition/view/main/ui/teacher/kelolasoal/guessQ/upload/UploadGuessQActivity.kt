package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ.upload

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.wisnu.speechrecognition.databinding.ActivityUploadGuessQBinding
import com.wisnu.speechrecognition.local_db.QuestionPlayGuess
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.network.UploadRequestBody
import com.wisnu.speechrecognition.utils.UtilsCode
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_ERROR
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_WARNING
import com.wisnu.speechrecognition.utils.createPartFromString
import com.wisnu.speechrecognition.utils.showMessage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import java.io.File

class UploadGuessQActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUploadGuessQBinding

    private var mediaPlayer: MediaPlayer? = null
    private val TAG = UploadGuessQActivity::class.simpleName

    private var params = HashMap<String, RequestBody>()
    private var audioPath: String? = null
    private var id_soal: Int = 0
    private var type = 0

    private var isAudioExist = false

    private val viewModel by viewModels<UploadGuessQViewModel>()



    companion object {
        const val TYPE = "type"
        const val EXTRA_DATA_QUESTION = "extra_data_question"
        private const val REQUEST_CODE_PERMISSIONS = 111
        private const val REQUEST_CODE_SELECT_AUDIO = 444
        const val REQUEST_ADD_AUDIO = 10
        const val REQUEST_ADD_IMAGE = 11
        const val REQUEST_EDIT_AUDIO = 20
        const val REQUEST_EDIT_IMAGE = 21
        const val REQUEST_ADD = 30
        const val REQUEST_EDIT = 40
        private const val AUDIO = 200
        private const val MUST_SET_OPSI = "Kolom inputan pernyataan opsi %s tidak boleh kosong!"
        private const val MUST_SET_KEY_OPSI = "Opsi kunci jawaban tidak boleh kosong!"
        private const val MUST_PICK_AUDIO = "Audio soal harus dipilih, tidak boleh kosong!"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityUploadGuessQBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareView()
    }

    private fun prepareView() {
        with(binding) {
            btnSimpan.setOnClickListener(this@UploadGuessQActivity)
            btnBack.setOnClickListener(this@UploadGuessQActivity)
            pickAudio.setOnClickListener(this@UploadGuessQActivity)
            btnReselectAudio.setOnClickListener(this@UploadGuessQActivity)
            audioPreviewUpload.setOnClickListener(this@UploadGuessQActivity)


            //cek store atau update layout
            if (intent.extras != null) {
                type = intent.getIntExtra(TYPE, 0)
                val question = intent.getParcelableExtra<QuestionPlayGuess>(EXTRA_DATA_QUESTION)
                if (question != null) {
                    id_soal = question.id ?: 0
                    when (question.kunciJawaban) { // kunci jawaban
                        1 -> rbOpsi1.isChecked = true
                        2 -> rbOpsi2.isChecked = true
                        3 -> rbOpsi3.isChecked = true
                    }

                    edtOpsi1.setText(question.opsi1)
                    edtOpsi2.setText(question.opsi2)
                    edtOpsi3.setText(question.opsi3)

                    //audio
                    audioPreviewUpload.visibility = View.VISIBLE
                    btnReselectAudio.visibility = View.VISIBLE
                    pickAudio.visibility = View.GONE
                    tvNamaFileAudio.text = question.suara

                    //Prepare Voice
                    mediaPlayer = MediaPlayer()
                    val urlAudio = ApiConfig.URL_SOUNDS + question.suara
                    prepareMediaPlayer(urlAudio)
                }
            }
        }
    }

    override fun onClick(view: View?) {
        with(binding) {
            when (view) {
                btnSimpan -> saveGuessQ()
                pickAudio -> selectAudio()
                btnReselectAudio -> {
                    isAudioExist = false
                    audioPreviewUpload.visibility = View.GONE
                    pickAudio.visibility = View.VISIBLE
                    tvSuara.visibility = View.VISIBLE
                    btnReselectAudio.visibility = View.GONE

                    //clear audio
                    releaseAudio()
                    audioPath = null
                }
                audioPreviewUpload -> {
                    //play audio
                    mediaPlayer?.start()
                }
                btnBack -> finish()
                else -> {}
            }
        }
    }

    private fun saveGuessQ() {
        with(binding) {

            val opsi1 = edtOpsi1.text.toString().trim()
            val opsi2 = edtOpsi2.text.toString().trim()
            val opsi3 = edtOpsi3.text.toString().trim()

            when {
                !isAudioExist -> { //todo: rentan error, notif mana request yg store n update
                    showMessage(
                        this@UploadGuessQActivity,
                        title = TITLE_WARNING,
                        message = MUST_PICK_AUDIO,
                        style = MotionToast.TOAST_WARNING
                    )
                    return@with
                }
                opsi1.isEmpty() -> {
                    showMessage(
                        this@UploadGuessQActivity,
                        title = TITLE_WARNING,
                        message = String.format(MUST_SET_OPSI, "1"),
                        style = MotionToast.TOAST_WARNING
                    )
                    return@with
                }
                opsi2.isEmpty() -> {
                    showMessage(
                        this@UploadGuessQActivity,
                        title = TITLE_WARNING,
                        message = String.format(MUST_SET_OPSI, "2"),
                        style = MotionToast.TOAST_WARNING
                    )
                    return@with
                }
                opsi3.isEmpty() -> {
                    showMessage(
                        this@UploadGuessQActivity,
                        title = TITLE_WARNING,
                        message = String.format(MUST_SET_OPSI, "3"),
                        style = MotionToast.TOAST_WARNING
                    )
                    return@with
                }
                !rbOpsi1.isChecked && !rbOpsi2.isChecked &&
                        !rbOpsi3.isChecked -> {
                    showMessage(
                        this@UploadGuessQActivity,
                        title = TITLE_WARNING,
                        message = MUST_SET_KEY_OPSI,
                        style = MotionToast.TOAST_WARNING
                    )
                    return@with
                }
                else -> {
                    loader(true)
                    var kunciJawaban = 0
                    when {
                        rbOpsi1.isChecked -> kunciJawaban = 1
                        rbOpsi2.isChecked -> kunciJawaban = 2
                        rbOpsi3.isChecked -> kunciJawaban = 3
                    }
                    params["id"] = createPartFromString(id_soal.toString())
                    params["opsi1"] = createPartFromString(opsi1)
                    params["opsi2"] = createPartFromString(opsi2)
                    params["opsi3"] = createPartFromString(opsi3)
                    params["kunci_jawaban"] = createPartFromString(kunciJawaban.toString())
                    if(isAudioExist && audioPath == null){
                        storeGuessQ(reqFileAudioEmpty(), params)
                    }else{
                        storeGuessQ(reqFileAudio(), params)
                    }

                }
            }
        }
    }

    private fun storeGuessQ(
        bodyAudio: MultipartBody.Part,
        params: HashMap<String, RequestBody>,
    ) {
        viewModel.store(bodyAudio, params).observe(this, { response ->
            loader(false)
            if (response.data != null) {
                if (response.code == 200) {
                    showMessage(
                        this@UploadGuessQActivity,
                        UtilsCode.TITLE_SUCESS,
                        "Berhasil menyimpan soal",
                        style = MotionToast.TOAST_SUCCESS
                    )
                    finish()
                } else {
                    showMessage(
                        this@UploadGuessQActivity,
                        TITLE_ERROR,
                        response.message ?: "",
                        style = MotionToast.TOAST_ERROR
                    )
                }
            } else {
                showMessage(
                    this@UploadGuessQActivity,
                    TITLE_ERROR,
                    "soal gagal disimpan",
                    style = MotionToast.TOAST_ERROR
                )
            }
        })
    }

    private fun selectAudio() {
        permission()
        mediaPlayer = MediaPlayer()
        val mimeTypes = arrayOf("audio/wav", "audio/m4a", "audio/mp3", "audio/amr")
//        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        resultLauncherVoice.launch(Intent.createChooser(intent, "Pilih 1 Audio"))
    }

    private fun getFilePath(type: Int, uri: Uri): String {
        lateinit var filePathColumn: Array<String>
        lateinit var filePath: String
        when (type) {
            AUDIO -> {
                filePathColumn = arrayOf(MediaStore.Audio.Media.DATA)
            }
        }
        val cursor = contentResolver?.query(
            uri,
            filePathColumn, null, null, null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            filePath = cursor.getString(columnIndex)
            cursor.close()
        } else {
            filePath = uri.path.toString()
        }
        return filePath
    }

    private fun prepareMediaPlayer(urlAudio: String) {
        try {
            mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
            mediaPlayer?.setDataSource(urlAudio) // URL music file
            mediaPlayer?.prepare()
            isAudioExist = true
        } catch (e: Exception) {
            Log.e(TAG, "prepareMediaPlayer: ${e.message}")
            isAudioExist = false
        }
    }

    private fun releaseAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun reqFileAudio(): MultipartBody.Part {
        val fileAudio = File(audioPath)
        val reqFileAudio = UploadRequestBody(fileAudio, "audio/mp3/wav/m4a/amr")
        return MultipartBody.Part.createFormData("suara", fileAudio.name, reqFileAudio)
    }

    private fun reqFileAudioEmpty(): MultipartBody.Part {
        val reqFileAudio = ""
            .toRequestBody("audio/mp3/wav/m4a/amr".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("suara", "", reqFileAudio)
    }

    private var resultLauncherVoice =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null) {
                    val selectedVoice = data.data

                    if (selectedVoice != null) {
                        audioPath = getFilePath(AUDIO, selectedVoice)

                        with(binding) {
                            // visible player Audio
                            audioPreviewUpload.visibility = View.VISIBLE
                            btnReselectAudio.visibility = View.VISIBLE
                            pickAudio.visibility = View.GONE
                            tvNamaFileAudio.text = getFilePath(AUDIO, selectedVoice)
                            prepareMediaPlayer(audioPath!!)

                            Log.d(TAG, "Audio Path: $audioPath")
                        }
                    }
                }
            }
        }

    private fun loader(state: Boolean) {
        with(binding) {
            if (state) {
                pbLoader.visibility = android.view.View.VISIBLE
            } else {
                pbLoader.visibility = android.view.View.GONE
            }
        }
    }

    // permission camera, write file, read file , and image
    private fun permission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), REQUEST_CODE_PERMISSIONS
            )
        }
    }
}