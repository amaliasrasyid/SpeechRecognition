package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.lessonQ.upload

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.bumptech.glide.Glide
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.ActivityUploadLessonQBinding
import com.wisnu.speechrecognition.local_db.QuestionStudyClass
import com.wisnu.speechrecognition.model.matery.MateryStudy
import com.wisnu.speechrecognition.model.questions.Question
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.network.UploadRequestBody
import com.wisnu.speechrecognition.utils.UtilsCode
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_ERROR
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_SUCESS
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_WARNING
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.guessQ.upload.UploadGuessQActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import java.io.File


class UploadLessonQActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityUploadLessonQBinding
    private val viewModel by viewModels<UploadLessonQViewModel>()
    private var listTeksJawaban =  ArrayList<String>()

    private var tipeMateriSoal = 0

    private var imagePath: String? = null
    private var audioPath: String? = null

    private var idQuestion = 0
    private var idMatery = 0
    private var type = 0
    private var isAudioExist = false
    private var isImageExist = false

    private var imageUri: Uri? = null

    private var mediaPlayer: MediaPlayer? = null
    private val TAG = UploadLessonQActivity::class.simpleName


    companion object {
        const val TYPE = "type"
        const val REQUEST_ADD = 30
        const val REQUEST_EDIT = 40
        const val EXTRA_DATA_QUESTION = "extra_data_question"
        const val EXTRA_DATA_MATERY_ID = "extra_data_matery_id"
        private const val REQUEST_CODE_PERMISSIONS = 111
        private const val REQUEST_CODE_SELECT_IMAGE = 333
        private const val REQUEST_CODE_SELECT_AUDIO = 444
        private const val AUDIO = 200
        private const val MUST_PICK_IMAGE = "Gambar soal harus dipilih, tidak boleh kosong!"
        private const val MUST_PICK_AUDIO = "Audio soal harus dipilih, tidak boleh kosong!"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        binding = ActivityUploadLessonQBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareView()
    }

    private fun prepareView() {
        loader(true) //Kasih notif klu sdg cek apa data ada
        with(binding){
            btnAddView.setOnClickListener(this@UploadLessonQActivity)
            btnBack.setOnClickListener(this@UploadLessonQActivity)
            btnSimpan.setOnClickListener(this@UploadLessonQActivity)
            pickImage.setOnClickListener(this@UploadLessonQActivity)
            pickAudio.setOnClickListener(this@UploadLessonQActivity)
            btnReselectImg.setOnClickListener(this@UploadLessonQActivity)
            btnReselectAudio.setOnClickListener(this@UploadLessonQActivity)
            audioPreviewUpload.setOnClickListener(this@UploadLessonQActivity)

            //cek jika ada datanya
            if(intent.extras != null){
                idMatery = intent.getIntExtra(EXTRA_DATA_MATERY_ID,0)
                val question = intent.getParcelableExtra<QuestionStudyClass>(EXTRA_DATA_QUESTION)
                if(question == null){
                    getQuestions(idMatery)
                }else{
                    prepareViewWithData(question)
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }

    override fun onClick(view: View?) {
        with(binding){
            when(view){
                btnAddView -> addInputFieldVocal()
                btnSimpan -> saveQuestion()
                pickImage -> selectImage()
                pickAudio -> selectAudio()
                btnReselectImg -> {
                    imgPreviewUpload.visibility = View.GONE
                    pickImage.visibility = View.VISIBLE
                    tvGambar.visibility = View.VISIBLE
                    btnReselectImg.visibility = View.GONE
                }
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
                btnBack -> finish()
                else -> {}
            }
        }
    }

    private fun getQuestions(materyId:Int) { //PASTI SATU KECUALI VOKAL
        viewModel.questions(materyId).observe(this) { response ->
            loader(false)
            if (response.data != null) {
                if (!response.data.isEmpty()) {
                    if (response.code == 200) {
                        val results = response.data
                        prepareViewWithData(results.get(0))
                    }
                } else {
                    prepareViewWithData()
                }
            } else {
                showMessage(
                    this,
                    TITLE_ERROR,
                    style = MotionToast.TOAST_ERROR
                )
            }
        }
    }

    private fun prepareViewWithData(item: Any? = null) {
        with(binding){
            if(item != null){
                when(item){
                    is Question -> {
                        edtTeksJawaban.setText(item.teksJawaban)

                        //image
                        Glide.with(this@UploadLessonQActivity)
                            .load(ApiConfig.URL_IMAGE + item.gambar)
                            .error(R.drawable.no_profile_images)
                            .into(imgPreviewUpload)
                        imgPreviewUpload.visibility = View.VISIBLE
                        btnReselectImg.visibility = View.VISIBLE
                        pickImage.visibility = View.GONE
                        isImageExist = true

                        //audio
                        audioPreviewUpload.visibility = View.VISIBLE
                        btnReselectAudio.visibility = View.VISIBLE
                        pickAudio.visibility = View.GONE
                        tvNamaFileAudio.text = item.suara

                        audioPath = item.suara
                        imagePath = item.gambar

                        //Prepare Voice
                        mediaPlayer = MediaPlayer()
                        val urlAudio = ApiConfig.URL_SOUNDS + item.suara
                        prepareMediaPlayer(urlAudio)
                    }
                    is QuestionStudyClass -> {
                        edtTeksJawaban.setText(item.teksJawaban)

                        //image
                        Glide.with(this@UploadLessonQActivity)
                            .load(ApiConfig.URL_IMAGE + item.gambar)
                            .error(R.drawable.no_profile_images)
                            .into(imgPreviewUpload)
                        imgPreviewUpload.visibility = View.VISIBLE
                        btnReselectImg.visibility = View.VISIBLE
                        pickImage.visibility = View.GONE
                        isImageExist = true

                        //audio
                        audioPreviewUpload.visibility = View.VISIBLE
                        btnReselectAudio.visibility = View.VISIBLE
                        pickAudio.visibility = View.GONE
                        tvNamaFileAudio.text = item.suara

                        audioPath = item.suara
                        imagePath = item.gambar

                        //Prepare Voice
                        mediaPlayer = MediaPlayer()
                        val urlAudio = ApiConfig.URL_SOUNDS + item.suara
                        prepareMediaPlayer(urlAudio)
                    }
                }
            }
        }
    }

    private fun addInputFieldVocal(){
        var layoutParent = binding.layoutParentKalimat

        //create and set attribute for new input field
        var newInputField = EditText(this@UploadLessonQActivity)
        newInputField.width = 100
        newInputField.height = 120
        newInputField.setPadding(8,8,8,8)
        newInputField.background = ContextCompat.getDrawable(this@UploadLessonQActivity,R.drawable.card_input)
        layoutParent.addView(newInputField)

        //set margin new input field
        var test =  newInputField.layoutParams as LinearLayout.LayoutParams
        test.topMargin = 30
    }

    private fun getListEdtJawaban() {
        var parentLayout = binding.layoutParentKalimat
        var childViews = parentLayout.childCount
        Log.e("ee",childViews.toString())


        with(binding){
            var i = 0
            while(i < childViews){
                val edtChild = parentLayout.getChildAt(i) as EditText
                listTeksJawaban.add(edtChild.text.toString())
                Log.e("ee",edtChild.toString())
                i++
            }
        }

    }

    private fun releaseAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun selectAudio(){
        permission()
        mediaPlayer = MediaPlayer()
        val mimeTypes = arrayOf("audio/wav", "audio/m4a", "audio/mp3","audio/amr")
//        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
        val intent = Intent(Intent.ACTION_PICK,MediaStore.Audio.Media.EXTERNAL_CONTENT_URI).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        if (intent.resolveActivity(packageManager!!) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_AUDIO)
        }
    }

    private fun selectAudioWithRecorder() {
        val mimeTypes = arrayOf("audio/wav", "audio/m4a", "audio/mp3","audio/amr")
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "audio/*"
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        resultLauncherVoice.launch(Intent.createChooser(intent, "Pilih 1 Audio"))
    }

    private fun selectImage() {
        permission()
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        if (intent.resolveActivity(packageManager!!) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE)
        }
    }

    private fun saveQuestion() {
        with(binding) {
            val teksJawaban = edtTeksJawaban.text ?: ""

            when (type) {
//                REQUEST_ADD -> {
//                    showMessage(
//                        this@UploadLessonQActivity,
//                        title = TITLE_WARNING,
//                        message = MUST_PICK_AUDIO,
//                        style = MotionToast.TOAST_WARNING
//                    )
//                }
                REQUEST_ADD -> {
                    if (imagePath.isNullOrEmpty()) {
                        showMessage(
                            activity = this@UploadLessonQActivity,
                            title = TITLE_WARNING,
                            message = MUST_PICK_IMAGE,
                            style = MotionToast.TOAST_WARNING
                        )
                        return@with
                    }
                    if (audioPath.isNullOrEmpty()) {
                        showMessage(
                            activity = this@UploadLessonQActivity,
                            title = TITLE_WARNING,
                            message = MUST_PICK_AUDIO,
                            style = MotionToast.TOAST_WARNING
                        )
                        return@with
                    }
                }
            }
                when {
                    teksJawaban.isEmpty() -> {
                        showMessage(
                            this@UploadLessonQActivity,
                            TITLE_WARNING,
                            "Teks jawaban soal tidak boleh kosong!",
                            style = MotionToast.TOAST_WARNING
                        )
                        return@with
                    }
                    else -> {
                        val bodyImage = reqFileImage()
                        val bodyAudio = reqFileAudio()
                        val materyQType = tipeMateriSoal
                        var params = HashMap<String, Any>()
                        params.put("id", idQuestion)
                        params.put("id_materi", idMatery)
                        params.put("teks_jawaban", teksJawaban)
                        storeLessonQuestion(bodyImage, bodyAudio, params)
                    }
                }
            }
    }

    private fun storeLessonQuestion(
        bodyImage: MultipartBody.Part,
        bodyAudio: MultipartBody.Part,
        params: HashMap<String,Any>
    ) {
        viewModel.uploadLessonQ(bodyImage,bodyAudio,params).observe(this) { response ->
            loader(false)
            if (response.data != null) {
                if (response.code == 200) {
                    showMessage(
                        this@UploadLessonQActivity,
                        TITLE_SUCESS,
                        "Berhasil menyimpan soal",
                        style = MotionToast.TOAST_SUCCESS
                    )
                } else {
                    showMessage(
                        this@UploadLessonQActivity,
                        TITLE_ERROR,
                        response.message ?: "",
                        style = MotionToast.TOAST_ERROR
                    )
                }
            } else {
                showMessage(
                    this@UploadLessonQActivity,
                    UtilsCode.TITLE_ERROR,
                    "nilai gagal disimpan",
                    style = MotionToast.TOAST_ERROR
                )
            }
        }
    }

    private fun reqFileImage(): MultipartBody.Part {
        val fileImage = File(imagePath!!)
        val reqFileImage =
            fileImage.asRequestBody("image/jpeg/jpg/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(
            "gambar", fileImage.name, reqFileImage
        )
    }

    private fun reqFileImageEmpty(): MultipartBody.Part {
        val reqFileImage = ""
            .toRequestBody("image/jpeg/jpg/png".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("gambar", "", reqFileImage)
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

    private fun getPathImage(contentUri: Uri): String? {
        val filePath: String?
        val cursor = contentResolver?.query(contentUri, null, null, null, null)
        if (cursor == null) {
            filePath = contentUri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        with(binding){
            when (requestCode) {
                REQUEST_CODE_SELECT_IMAGE -> {
                    if (resultCode == RESULT_OK) {
                        imageUri = data?.data
                        imagePath = getPathImage(imageUri!!)
                        imgPreviewUpload.visibility = View.VISIBLE
                        btnReselectImg.visibility = View.VISIBLE
                        pickImage.visibility = View.GONE
                        imgPreviewUpload.setImageURI(imageUri)
                        isImageExist = true
                    }
                }
                REQUEST_CODE_SELECT_AUDIO -> {
                    if (resultCode == RESULT_OK) {
                        // There are no request codes
                        val data: Intent? = data
                        if (data != null) {
                            val selectedVoice = data.data

                            audioPath = getFilePath(AUDIO, selectedVoice!!)

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

    private fun loader(state: Boolean) {
        with(binding) {
            if (state) {
                pbLoader.visibility = android.view.View.VISIBLE
            } else {
                pbLoader.visibility = android.view.View.GONE
            }
        }
    }

}