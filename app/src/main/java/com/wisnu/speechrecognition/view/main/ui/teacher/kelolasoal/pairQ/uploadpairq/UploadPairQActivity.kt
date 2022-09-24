package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ.uploadpairq

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
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
import com.wisnu.speechrecognition.databinding.ActivityUploadPairQBinding
import com.wisnu.speechrecognition.local_db.PairWordQClass
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.network.UploadRequestBody
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_ERROR
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_SUCESS
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_WARNING
import com.wisnu.speechrecognition.utils.createPartFromString
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ.PairQViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import java.io.File

class UploadPairQActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUploadPairQBinding

    private var mediaPlayer: MediaPlayer? = null
    private var audioPath: String? = null
    private var isAudioExist = false
    private var id_soal: Int = 0

    private var pairWQ: PairWordQClass? = null
    private val viewModel by viewModels<PairQViewModel>()

    private val TAG = UploadPairQActivity::class.simpleName
    
    companion object {
        const val TYPE = "type"
        const val REQUEST_EDIT = 40
        const val EXTRA_DATA_PAIRQ= "extra_data_pairq"
        private const val REQUEST_CODE_PERMISSIONS = 111
        private const val REQUEST_CODE_SELECT_AUDIO = 444
        private const val AUDIO = 200
        private const val MUST_PICK_AUDIO = "audio soal harus ditambahkan, tidak boleh kosong!"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPairQBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareView()    
    }

    private fun prepareView() {
        with(binding){
            btnSimpan.setOnClickListener(this@UploadPairQActivity)
            btnBack.setOnClickListener(this@UploadPairQActivity)
            pickAudio.setOnClickListener(this@UploadPairQActivity)
            btnReselectAudio.setOnClickListener(this@UploadPairQActivity)
            audioPreviewUpload.setOnClickListener(this@UploadPairQActivity)

            //cek store atau update layout
            if (intent.extras != null) {
                val question = intent.getParcelableExtra<PairWordQClass>(EXTRA_DATA_PAIRQ)
                id_soal = question?.id ?: 0
                if (question != null) {
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
                btnSimpan -> savePairQ()
                btnBack -> finish()
                else -> {}
            }
        }

    }

    private fun savePairQ(){
        with(binding){
            when{
                !isAudioExist -> { //todo: rentan error, notif mana request yg store n update
                    showMessage(
                        this@UploadPairQActivity,
                        title = TITLE_WARNING,
                        message = MUST_PICK_AUDIO,
                        style = MotionToast.TOAST_WARNING
                    )
                    return@with
                }
                else -> {
                    loader(true)
                    val id = createPartFromString(id_soal.toString())
                    if(isAudioExist && audioPath == null){
                        storePairQ(id,reqFileAudioEmpty())
                    }else{
                        storePairQ(id,reqFileAudio())
                    }
                }
            }
        }

    }

    private fun storePairQ(id: RequestBody, bodyAudio: MultipartBody.Part) {
        viewModel.store(id,bodyAudio).observe(this, { response ->
            loader(false)
            if (response.data != null) {
                if (response.code == 200) {
                    showMessage(
                        this@UploadPairQActivity,
                        TITLE_SUCESS,
                        "Berhasil menyimpan soal",
                        style = MotionToast.TOAST_SUCCESS
                    )
                    finish()
                } else {
                    showMessage(
                        this@UploadPairQActivity,
                        TITLE_ERROR,
                        response.message ?: "",
                        style = MotionToast.TOAST_ERROR
                    )
                }
            }else {
                showMessage(
                    this@UploadPairQActivity,
                    TITLE_ERROR,
                    response.message ?: "",
                    style = MotionToast.TOAST_ERROR
                )
            }
        })
    }

    private fun selectAudio() {
        permission()
        mediaPlayer = MediaPlayer()
        val mimeTypes = arrayOf("audio/wav", "audio/m4a", "audio/mp3", "audio/amr")
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).apply {
//            type = "audio/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        resultLauncherVoice.launch(Intent.createChooser(intent, "Pilih 1 Audio"))
    }

    private fun prepareMediaPlayer(urlAudio: String) {
        val attribute = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        try {
            mediaPlayer?.setAudioAttributes(attribute)
            mediaPlayer?.setDataSource(urlAudio) // URL music file
            mediaPlayer?.prepare()
            isAudioExist = true
        } catch (e: Exception) {
            Log.e(TAG, "prepareMediaPlayer: ${e.message}")
            isAudioExist = false
        }
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