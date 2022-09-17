package com.wisnu.speechrecognition.view.main.ui.question

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.IBinder
import android.speech.RecognizerIntent
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.databinding.FragmentQuestionBinding
import com.wisnu.speechrecognition.local_db.StudentScore
import com.wisnu.speechrecognition.model.questions.Question
import com.wisnu.speechrecognition.network.ApiConfig.Companion.URL_IMAGE
import com.wisnu.speechrecognition.network.ApiConfig.Companion.URL_SOUNDS
import com.wisnu.speechrecognition.service.BackgroundSoundService
import com.wisnu.speechrecognition.session.UserPreference
import com.wisnu.speechrecognition.utils.*
import com.wisnu.speechrecognition.utils.UtilsCode.REQUEST_CODE_AUDIO_RECORD
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_ERROR
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_SUCESS
import com.wisnu.speechrecognition.view.main.ui.student.MainActivity
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_AZ
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_KONSONAN
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_HURUF_VOKAL
import com.wisnu.speechrecognition.view.main.ui.student.study.StudyFragment.Companion.TIPE_MEMBACA
import www.sanju.motiontoast.MotionToast
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class QuestionFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentQuestionBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: QuestionViewModel

    private var mediaPlayer: MediaPlayer? = null

    private val listSoal: ArrayList<Question> = ArrayList()
    private val listSoalTerjawab: HashMap<Int, StudentScore> = HashMap()

    private var nextQ = 1
    private var totalQ = 0

    private var distance = 0
    private var rightWord = 0
    private var wrongWord = 0
    private var score = 0
    private var index = 0
    private var recognizedWord = ""
    private var isTheLastQ = false
    private var materyType = 0
    private var answerText = ""
    private var siswa = 0

    private lateinit var mainActivity :MainActivity

    private val TAG = QuestionFragment::class.simpleName

    companion object {
        fun newInstance() = QuestionFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mainActivity = activity as MainActivity
        _binding = FragmentQuestionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[
                QuestionViewModel::class.java]

        prepareView()
    }

    private fun prepareView() {
        val args = QuestionFragmentArgs.fromBundle(arguments as Bundle)
        siswa = UserPreference(requireContext()).getUser().id ?: 0 //TODO: rentan error karna api returnnya html
        val materyId = args.idMateriBelajar
        materyType = args.tipeMateriBelajar

        //set title
        var title = ""
        when (materyType) {
            TIPE_HURUF_AZ -> title = "Mengenal\nHuruf"
            TIPE_HURUF_KONSONAN -> title = "Konsonan"
            TIPE_HURUF_VOKAL -> title = "Huruf\nVokal"
            TIPE_MEMBACA -> title = "Mengenal\nKosakata"
        }
        binding.layoutQuestion.tvTitle.text = title

        //siapkan data soal
        when(materyType){
            TIPE_HURUF_AZ -> getQuestionsByType(TIPE_HURUF_AZ)
            TIPE_HURUF_KONSONAN -> getQuestionsByType(TIPE_HURUF_KONSONAN)
            TIPE_HURUF_VOKAL, TIPE_MEMBACA -> getQuestions(materyId)
        }

    }

    private fun getQuestionsByType(materyTypeId: Int) {
        viewModel.questionsByType(materyTypeId).observe(viewLifecycleOwner) { response ->
            binding.progresbar.visibility = View.GONE
            if (response.data != null) {
                if (!response.data.isEmpty()) {
                    if (response.code == 200) {
                        val results = response.data
                        listSoal.addAll(results)
                        showLayout()
                        prepareQuestions()
                    } else {
                        dataNotFound()
                    }
                } else {
                    dataNotFound()
                }
            } else {
                dataNotFound()
            }
        }
    }

    private fun getQuestions(materyId: Int) {
        viewModel.questions(materyId).observe(viewLifecycleOwner) { response ->
            binding.progresbar.visibility = View.GONE
            if (response.data != null) {
                if (!response.data.isEmpty()) {
                    if (response.code == 200) {
                        val results = response.data
                        listSoal.addAll(results)
                        showLayout()
                        prepareQuestions()
                    } else {
                        dataNotFound()
                    }
                } else {
                    dataNotFound()
                }
            } else {
                dataNotFound()
            }
        }
    }

    private fun prepareQuestions() {
        prepareButtonPrevNext()
        with(binding) {
            with(layoutQuestion) {
                btnSpeech.setOnClickListener(this@QuestionFragment)
                btnSpeaker.setOnClickListener(this@QuestionFragment)
                imgbNextq.setOnClickListener(this@QuestionFragment)
                imgbPrevq.setOnClickListener(this@QuestionFragment)
            }
            with(layoutVocabQuestion) {
                btnSpeech.setOnClickListener(this@QuestionFragment)
//                imgbNextq.setOnClickListener(this@QuestionFragment)
//                imgbPrevq.setOnClickListener(this@QuestionFragment)
            }
            val soal = listSoal[index]

            //Prepare Image
            val imageView = layoutQuestion.imgQuestion
            val urlImage = URL_IMAGE + soal.gambar
            Glide.with(requireContext())
                .load(urlImage)
                .error(R.drawable.img_not_found) //TODO: GANTI NANTI
                .into(imageView)

            //Prepare Audio
            mediaPlayer = MediaPlayer()
            val urlAudio = URL_SOUNDS + soal.suara
            prepareMediaPlayer(urlAudio)
            playAudioVoice()

            //prepare text
            with(binding.layoutQuestion) {
                answerText = soal.teksJawaban.lowercase()
                tvKalimatTest.text = "Kalimat: ${answerText}"
            }
            with(binding.layoutVocabQuestion) {

            }

            //prepare view answered Question
            val soalTerjawab = listSoalTerjawab[index]
            if(soalTerjawab != null){
                with(binding.layoutQuestion) {
                    tvVoiceResult.text = "Yang disebut : ${soalTerjawab.spokenWords}"
                    tvCorrectResult.text = "Benar Huruf: ${soalTerjawab.righWord}"
                    tvWrongResult.text = "Salah Huruf: ${soalTerjawab.wrongWord}"
                    tvDistanceResult.text = "Hasil Rumus: ${soalTerjawab.distance}"
                    tvResultScore.text = soalTerjawab.score.toString()
                    tvMessageResultScore.text = kesimpulanHasil(soalTerjawab.score)
                }
                with(binding.layoutVocabQuestion){
                    tvVoiceResult.text = "Yang disebut : ${soalTerjawab.spokenWords}"
                    tvCorrectResult.text = "Benar Huruf: ${soalTerjawab.righWord}"
                    tvWrongResult.text = "Salah Huruf: ${soalTerjawab.wrongWord}"
                    tvDistanceResult.text = "Hasil Rumus: ${soalTerjawab.distance}"
                    tvResultScore.text = soalTerjawab.score.toString()
                    tvMessageResultScore.text = kesimpulanHasil(soalTerjawab.score)
                }
            }else{
                with(binding.layoutQuestion) {
                    tvVoiceResult.text = "Yang disebut : ..."
                    tvCorrectResult.text = "Benar Huruf: ..."
                    tvWrongResult.text = "Salah Huruf: ..."
                    tvDistanceResult.text = "Hasil Rumus: ..."
                    tvResultScore.text = ""
                    tvMessageResultScore.text = "-"
                }
                with(binding.layoutVocabQuestion){
                    tvVoiceResult.text = "Yang disebut : ..."
                    tvCorrectResult.text = "Benar Huruf: ..."
                    tvWrongResult.text = "Salah Huruf: ..."
                    tvDistanceResult.text = "Hasil Rumus: ..}"
                    tvResultScore.text = ""
                    tvMessageResultScore.text = "-"
                }
            }
        }
    }

    private fun prepareButtonPrevNext() {
        val sizeQuestion = listSoal.size
        if (sizeQuestion != 1) {
            with(binding.layoutQuestion) {
                when (index) {
                    0 -> {
                        imgbPrevq.visibility = View.GONE
                        imgbNextq.visibility = View.VISIBLE
                    }
                    sizeQuestion - 1 -> {
                        imgbPrevq.visibility = View.VISIBLE
                        imgbNextq.visibility = View.VISIBLE
                    }
                    else -> {
                        imgbPrevq.visibility = View.VISIBLE
                        imgbNextq.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onClick(view: View?) {
        with(binding.layoutQuestion) {
            when (view?.id) {
                R.id.imgb_nextq -> {
                    ++index
                        nextQuestion()
                }
                R.id.imgb_prevq -> {
                    --index
                    prevQuestion()
                }
                R.id.btn_speech -> {
                    openSpeechRecord()
                }
                R.id.btn_speaker -> {
                    playAudioVoice()
                }
            }
        }
    }

    private fun prevQuestion() {
        prepareQuestions()
    }

    private fun nextQuestion(){
        if(index != listSoal.size || !(index >= listSoal.size)){
            prepareQuestions()
        }else{
            //tutup halaman Question (SELESAI)
            findNavController().popBackStack()
            Log.i("tag","indeks lebih besar dr size soal")
        }
    }

    private fun openSpeechRecord() {
        checkPermissionAudioRecord()
        // Get the Intent action
        val sttIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        // Language model defines the purpose, there are special models for other use cases, like search.
        sttIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        // Adding an extra language, you can use any language from the Locale class.
        sttIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )//TODO: pastikan bahasa hp indonesia
        // Text that shows up on the Speech input prompt.
        sttIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Silahkan berbicara!")
        try {
            // Start the intent for a result, and pass in our request code.
            startActivityForResult(sttIntent, REQUEST_CODE_AUDIO_RECORD)
        } catch (e: ActivityNotFoundException) {
            // Handling error when the service is not available.
            e.printStackTrace()
            val show =
                Toast.makeText(
                    requireContext(),
                    "Your device does not support STT.",
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            // Handle the result for our request code.
            REQUEST_CODE_AUDIO_RECORD -> {
                // Safety checks to ensure data is available.
                if (resultCode == Activity.RESULT_OK && data != null) {
                    // Retrieve the result array.
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    // Ensure result array is not null or empty to avoid errors.
                    if (!result.isNullOrEmpty()) {
                        // Recognized text is in the first position.
                        val recognizedText = result[0]
                        calculateScore(recognizedText.lowercase(Locale.getDefault()))
                    }
                }
            }
        }
    }

    private fun checkPermissionAudioRecord() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    REQUEST_CODE_AUDIO_RECORD
                )
            }
        }
    }

    private fun playAudioVoice() {
        mediaPlayer?.start()
    }

    private fun releaseAudio(emptyMediaPlayer: Boolean = true) {
        mediaPlayer?.release()
        if(emptyMediaPlayer) mediaPlayer = null
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
        } catch (e: Exception) {
            Log.e(TAG, "prepareMediaPlayer: ${e.message}")
        }
    }

    private fun loader(state: Boolean) {
        with(binding.layoutQuestion) {
            if (state) {
                pbLoader.visibility = View.VISIBLE
            } else {
                pbLoader.visibility = View.GONE
            }
        }
    }

    private fun calculateScore(recognizedText: String) {
        //dg metode levenstein;ad di helper
        loader(true)
        distance = getLevenshteinDistance(answerText, recognizedText)
        val rightWrongWord = countRightWrongWord(answerText, recognizedText)
        val similarity = findSimilarity(answerText, recognizedText)
        score = (similarity * 100).toInt()
        rightWord = rightWrongWord["righWord"] ?: 0
        wrongWord = rightWrongWord["wrongWord"] ?: 0
        recognizedWord = recognizedText
        //set ke view
        with(binding.layoutQuestion) {
            tvVoiceResult.text = "Yang disebut : ${recognizedWord}"
            tvCorrectResult.text = "Benar Huruf: ${rightWord}"
            tvWrongResult.text = "Salah Huruf: ${wrongWord}"
            tvDistanceResult.text = "Hasil Rumus: ${distance}"
            tvResultScore.text = score.toString()
            tvMessageResultScore.text = kesimpulanHasil(score)
        }
        with(binding.layoutVocabQuestion){
            tvVoiceResult.text = "Yang disebut : ${recognizedWord}"
            tvCorrectResult.text = "Benar Huruf: ${rightWord}"
            tvWrongResult.text = "Salah Huruf: ${wrongWord}"
            tvDistanceResult.text = "Hasil Rumus: ${distance}"
            tvResultScore.text = score.toString()
            tvMessageResultScore.text = kesimpulanHasil(score)
        }
        releaseAudio() //lepas audio lama

        //simpan atau update nilai skornya
        storeOrUpdate(score)
    }

    private fun kesimpulanHasil(score: Int): String {
        var mString = "-"
        //TODO: ubah warna teks nilai sesuai tingkatannya
        when (score) {
            in 0..30 -> {
                mString = "Ucapan Sangat Kurang Sempurna, silahkan coba lagi"
            }
            in 31..50 -> {
                mString = "Ucapan Kurang Sempurna, silahkan coba lagi"
            }
            in 56..70 -> {
                mString = "Ucapan Cukup Sempurna"
            }
            in 71..85 -> {
                mString = "Ucapan Sempurna"
            }
            in 86..100 -> {
                mString = "Ucapan Sangat Sempurna, Mantap!!!"
            }
        }
        return mString
    }

    private fun storeOrUpdate(score: Int) {
        // temporary
            listSoalTerjawab[index] = StudentScore(
                0,
                score,
                recognizedWord,
                rightWord,
                wrongWord,
                distance
            )

        //db server
        val studentScore = hashMapOf<String,Any>()
        studentScore["id_siswa"] = siswa
        studentScore["id_tipe_game"] = 0
        studentScore["id_soal"] = listSoal[index].id
        studentScore["nilai"] = score
        viewModel.storeScore(studentScore).observe(viewLifecycleOwner, { response ->
            loader(false)
            if (response.data != null) {
                    if (response.code == 200) {
                        showMessage(
                            requireActivity(),
                            TITLE_SUCESS,
                            "Berhasil menyimpan nilai",
                            style = MotionToast.TOAST_SUCCESS
                        )
                    } else {
                        showMessage(
                            requireActivity(),
                            TITLE_ERROR,
                            response.message ?: "",
                            style = MotionToast.TOAST_ERROR
                        )
                    }
            } else {
                showMessage(
                    requireActivity(),
                    TITLE_ERROR,
                    "nilai gagal disimpan",
                    style = MotionToast.TOAST_ERROR
                )
            }
        })
    }

    private fun showLayout() {
        with(binding) {
            when (materyType) {
                TIPE_HURUF_AZ, TIPE_HURUF_KONSONAN, TIPE_HURUF_VOKAL -> {
                    val layoutQuestion = layoutQuestion.root
                    layoutQuestion.visibility = View.VISIBLE
                }
                TIPE_MEMBACA -> {
                    val layoutVocab = layoutVocabQuestion.root
                    layoutVocab.visibility = View.VISIBLE
                }
            }

        }
    }

    override fun onStart() {
        super.onStart()
        if(mainActivity != null) mainActivity.mediaPlayer.pause()
    }

    override fun onStop() {
        super.onStop()
        releaseAudio(emptyMediaPlayer = false)
        if(mainActivity != null) mainActivity.mediaPlayer.start()
    }

    private fun dataNotFound() {
        with(binding) {
            val layoutEmpty = layoutEmpty.root
            val layoutQuestion = layoutQuestion.root
            val layoutVocab = layoutVocabQuestion.root
            layoutEmpty.visibility = View.VISIBLE
            layoutQuestion.visibility = View.GONE
            layoutVocab.visibility = View.GONE
        }
    }
}
