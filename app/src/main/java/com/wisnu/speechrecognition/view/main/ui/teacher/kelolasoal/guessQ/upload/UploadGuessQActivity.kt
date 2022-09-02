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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.ActivityUploadGuessQBinding
import com.wisnu.speechrecognition.databinding.ActivityUploadLessonQBinding
import com.wisnu.speechrecognition.local_db.QuestionClass
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.network.UploadRequestBody
import com.wisnu.speechrecognition.utils.UtilsCode
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.upload.UploadLessonQActivity
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.upload.UploadLessonQViewModel
import okhttp3.MultipartBody
import www.sanju.motiontoast.MotionToast
import java.io.File

class UploadGuessQActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUploadGuessQBinding

    private var mediaPlayer: MediaPlayer? = null
    private val TAG = UploadGuessQActivity::class.simpleName

    private lateinit var bodyAudio: MultipartBody.Part
    private var params = HashMap<String,Any>()
    private lateinit var viewModel: UploadGuessQViewModel
    private var audioPath: String? = null


    companion object {
        const val TYPE = "type"
        const val REQUEST_ADD_VIDEO = 10
        const val REQUEST_ADD_IMAGE = 11
        const val REQUEST_EDIT_VIDEO = 20
        const val REQUEST_EDIT_IMAGE = 21
        const val EXTRA_DATA = "extra_data"
        private const val REQUEST_CODE_PERMISSIONS = 111
        private const val REQUEST_CODE_SELECT_AUDIO = 444
        private const val AUDIO = 200
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityUploadGuessQBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareView()
    }

    private fun prepareView() {
        with(binding){
            btnSimpan.setOnClickListener(this@UploadGuessQActivity)
            pickAudio.setOnClickListener(this@UploadGuessQActivity)
            btnReselectAudio.setOnClickListener(this@UploadGuessQActivity)
            audioPreviewUpload.setOnClickListener(this@UploadGuessQActivity)

            //cek store atau update layout
            if(intent.extras != null){
                val question = intent.getParcelableExtra<QuestionClass>("question")
                if(question != null){
                    rbOpsi1.setText(question.teksJawaban)
                    rbOpsi2.setText(question.teksJawaban)
                    rbOpsi3.setText(question.teksJawaban)

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
        with(binding){
            when(view){
                btnSimpan -> saveGuessQ()
                pickAudio -> selectAudio()
                btnReselectAudio -> {
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
                else -> {}
            }
        }
    }

    private fun saveGuessQ() {

    }

    private fun selectAudio(){
        permission()
        mediaPlayer = MediaPlayer()
        val mimeTypes = arrayOf("audio/wav", "audio/m4a", "audio/mp3","audio/amr")
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
        } catch (e: Exception) {
            Log.e(TAG, "prepareMediaPlayer: ${e.message}")
        }
    }

    private fun releaseAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun reqFileAudio(){
        if (audioPath == null) {
            showMessage(
                this@UploadGuessQActivity,
                UtilsCode.TITLE_WARNING,
                "Data gambar harus ada, silahkan pilih gambar untuk soal",
                style = MotionToast.TOAST_WARNING
            )
        } else {
            val fileAudio = File(audioPath)
            val reqFileAudio = UploadRequestBody(fileAudio, "audio/mp3/wav/m4a")
            bodyAudio = MultipartBody.Part.createFormData("soal_suara", fileAudio.name, reqFileAudio)
        }
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
                            tvNamaFileAudio.text = getFilePath(AUDIO, selectedVoice)
                            prepareMediaPlayer(audioPath!!)

                            Log.d(TAG, "Audio Path: $audioPath")
                        }
                    }
                }
            }
        }

    private fun storeGuessQ(
        bodyAudio: MultipartBody.Part,
        params: HashMap<String,Any>,
    ) {

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