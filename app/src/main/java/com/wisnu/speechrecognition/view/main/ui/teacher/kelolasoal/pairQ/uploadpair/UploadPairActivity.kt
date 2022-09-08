package com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ.uploadpair

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import com.bumptech.glide.Glide
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.ActivityUploadPairBinding
import com.wisnu.speechrecognition.local_db.Pair
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.utils.UtilsCode
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_WARNING
import com.wisnu.speechrecognition.utils.createPartFromString
import com.wisnu.speechrecognition.utils.showMessage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import www.sanju.motiontoast.MotionToast
import java.io.File

class UploadPairActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityUploadPairBinding
    private val viewModel by viewModels<PairViewModel>()
    
    private var imagePath: String? = null
    private var isImageExist = false
    private var imageUri: Uri? = null
    private var pair: Pair? = null
    private var idPair = 0
    private var idQuestion = 0

    private val TAG = UploadPairActivity::class.simpleName
    
    companion object {
        const val TYPE = "type"
        const val EXTRA_DATA_PAIR = "extra_data_pair"
        const val EXTRA_DATA_ID_Q = "extra_data_id_q"
        private const val REQUEST_CODE_PERMISSIONS = 111
        private const val MUST_PICK_IMAGE = "Gambar soal harus dipilih, tidak boleh kosong!"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadPairBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prepareView()    
    }

    private fun prepareView() {
        //Kasih notif klu sdg cek apa data ada
        with(binding){
            btnAddView.setOnClickListener(this@UploadPairActivity)
            btnBack.setOnClickListener(this@UploadPairActivity)
            btnSimpan.setOnClickListener(this@UploadPairActivity)
            pickImage.setOnClickListener(this@UploadPairActivity)
            btnReselectImg.setOnClickListener(this@UploadPairActivity)

            //cek store atau update layout
            if(intent.extras != null){
                idQuestion = intent.getIntExtra(EXTRA_DATA_ID_Q,0) //otomatis return error jg karna fk tidak ada di db

                pair = intent.getParcelableExtra<Pair>(EXTRA_DATA_PAIR)
                if(pair != null) {
                    idPair = pair?.id ?: 0
                    idQuestion = pair?.idSoal ?: 0
                    prepareViewWithData(pair!!)
                }
            }
        }
    }

    private fun prepareViewWithData(pair: Pair) {
        with(binding){
            edtTeksJawaban.setText(pair.kata)

            //image
            Glide.with(this@UploadPairActivity)
                .load(ApiConfig.URL_IMAGE + pair.gambar)
                .error(R.drawable.no_profile_images)
                .into(imgPreviewUpload)
            imgPreviewUpload.visibility = View.VISIBLE
            btnReselectImg.visibility = View.VISIBLE
            pickImage.visibility = View.GONE
            isImageExist = true
        }
    }

    override fun onClick(view: View?) {
        with(binding){
            when(view){
                btnSimpan -> {
                    savePair()
                    Log.d(TAG,"btn simpan pair cliced")
                }
                pickImage -> selectImage()
                btnReselectImg -> {
                    isImageExist = false
                    imgPreviewUpload.visibility = View.GONE
                    pickImage.visibility = View.VISIBLE
                    tvGambar.visibility = View.VISIBLE
                    btnReselectImg.visibility = View.GONE
                }
                btnBack -> finish()
                else -> {}
            }
        }
    }

    private fun savePair() {
        loader(true)
        with(binding) {
            val kata = edtTeksJawaban.text.toString().trim()
            when {
                kata.isEmpty() -> {
                    showMessage(
                        this@UploadPairActivity,
                        TITLE_WARNING,
                        "Kata pasangan gambar tidak boleh kosong!",
                        style = MotionToast.TOAST_WARNING
                    )
                    return@with
                }
                !isImageExist -> {
                    showMessage(
                        activity = this@UploadPairActivity,
                        title = TITLE_WARNING,
                        message = MUST_PICK_IMAGE,
                        style = MotionToast.TOAST_WARNING
                    )
                    return@with
                }
                else -> {
                    var params = HashMap<String, RequestBody>()
                    params.put("id", createPartFromString(idPair.toString()))
                    params.put("id_soal", createPartFromString(idQuestion.toString()))
                    params.put("kata", createPartFromString(kata))
                    if(isImageExist && imagePath == null){
                        storePair(reqFileImageEmpty(),params)                    
                    }else{
                        storePair(reqFileImage(),params)
                    }
                }
            }
        }
    }

    private fun storePair(bodyImage: MultipartBody.Part, params: HashMap<String, RequestBody>) {
        viewModel.uploadPair(bodyImage,params).observe(this, { response ->
            loader(false)
            if (response.data != null) {
                if (response.code == 200) {
                    showMessage(
                        this@UploadPairActivity,
                        UtilsCode.TITLE_SUCESS,
                        response.message ?: "",
                        style = MotionToast.TOAST_SUCCESS
                    )
                    finish()
                } else {
                    showMessage(
                        this@UploadPairActivity,
                        UtilsCode.TITLE_ERROR,
                        response.message ?: "",
                        style = MotionToast.TOAST_ERROR
                    )
                }
            } else {
                showMessage(
                    this@UploadPairActivity,
                    UtilsCode.TITLE_ERROR,
                    response.message ?: "",
                    style = MotionToast.TOAST_ERROR
                )
            }
        })
    }

    private fun selectImage() {
        permission()
        val mimeTypes = arrayOf("image/jpeg", "image/png", "image/jpg")
        val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            }
        resultLauncherImage.launch(Intent.createChooser(intent, "Pilih 1 Gambar"))
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

    private var resultLauncherImage =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                if (data != null) {
                    val selectedImage = data.data
                    if (selectedImage != null) {
                        with(binding) {
                            imageUri = data?.data
                            imagePath = getPathImage(imageUri!!)
                            imgPreviewUpload.visibility = View.VISIBLE
                            btnReselectImg.visibility = View.VISIBLE
                            pickImage.visibility = View.GONE
                            imgPreviewUpload.setImageURI(imageUri)
                            isImageExist = true
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

    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }
}