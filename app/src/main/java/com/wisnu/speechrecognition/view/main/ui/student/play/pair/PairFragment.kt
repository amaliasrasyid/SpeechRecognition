package com.wisnu.speechrecognition.view.main.ui.student.play.pair

import android.graphics.Color
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wisnu.speechrecognition.R
import com.wisnu.speechrecognition.adapter.ImagePairQAdapter
import com.wisnu.speechrecognition.adapter.TextPairQAdapter
import com.wisnu.speechrecognition.databinding.FragmentPairBinding
import com.wisnu.speechrecognition.model.questions.PairWordQ
import com.wisnu.speechrecognition.model.questions.PairsItem
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.session.UserPreference
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_WARNING
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ.PairQViewModel
import com.wisnu.speechrecognition.utils.miliSecondToTimer
import com.wisnu.speechrecognition.view.main.ui.student.MainActivity
import com.wisnu.speechrecognition.view.main.ui.student.ResultFragment.Companion.PAIR_TYPE
import www.sanju.motiontoast.MotionToast

class PairFragment : Fragment() {
    private lateinit var binding: FragmentPairBinding

    private var selectedImage: ImageView? = null
    private var listSelectedImg = ArrayList<ImageView>()
    private var listSelectedText = ArrayList<TextView>()
    private var listSelectedPair = ArrayList<Pair<ImageView,String>>()
    private var listPairQ = ArrayList<PairWordQ>()
    private var listText = ArrayList<String>()
    private var selectedPair :Pair<ImageView,String>? = null

    private var isTextBeingSelected = false
    private var countDeletedLines = 0

    private var x1 = 0f
    private var y1 = 0f

    private var score :Int = 0
    private var index = 0
    private var nextQ = 1
    private var totalQ = 0
    private var indexProgress = 1

    private var indexPair = 1
    private var totalPairs = 0

    private var countTotalPairs = 0

    private lateinit var countDownTimer: CountDownTimer
    private var timeLeft: Long = 120000 //180000=3minute per question

    private var mediaPlayer: MediaPlayer? = null

    private lateinit var imgAdapter: ImagePairQAdapter
    private lateinit var textAdapter: TextPairQAdapter

    private val viewModel by viewModels<PairQViewModel>()
    private lateinit var mainActivity :MainActivity

    private val TAG = PairFragment::class.java.simpleName

    companion object{
        private const val CLICKED_TEXT = 100
        private const val CLICKED_IMAGE = 200
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity

        binding = FragmentPairBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareView()
    }

    private fun prepareView() {
        with(binding){
            loader(true)
            //hapus baris jika ada
            drawView.clearLines()
            observePairQ()
        }
    }

    private fun prepareQuestion() {
        with(binding){
            val pairQ = listPairQ.get(index)
            totalPairs = pairQ.pairs!!.size
            if(totalPairs == 0){
                if(index != listPairQ.size-1 && index < listPairQ.size){
                    nextPairQ()
                }else{
                    val studentId = UserPreference(requireActivity()).getUser().id
                    if(listPairQ.size == 1){
                        dataNotFound(isPairsEmpty = true)
                    }else{
                        moveToResult()
                    }
                }
            }else{
                countTotalPairs += totalPairs

                //prepare audio
                mediaPlayer = MediaPlayer()
                val urlAudio = ApiConfig.URL_SOUNDS + pairQ.suara
                prepareMediaPlayer(urlAudio)

                var(listImg,listText) = prepareDataQ(pairQ)

                //init adapter
                imgAdapter = ImagePairQAdapter(requireActivity(),0,listImg)
                textAdapter = TextPairQAdapter(requireActivity(),0,listText)

                lvImg.adapter = imgAdapter
                lvText.adapter = textAdapter

                lvImg.isScrollContainer = false
                lvText.isScrollContainer = false

                imgAdapter.setOnImageClickCallback(object: ImagePairQAdapter.OnImageClickCallback{
                    override fun onImageClicked(img: ImageView, imgPoint: ImageView) {
                        if(selectedImage != img){
                            val isImageHadSelected = findImage(listSelectedPair,img)
                            if(!isImageHadSelected){
                                selectedImage = img
                                drawStartLine(imgPoint)
                            }else{
                                showMessage(
                                    requireActivity(),
                                    TITLE_WARNING,
                                    "pilihlah gambar yang belum dipasangkan",
                                    MotionToast.TOAST_WARNING
                                )
                            }
                        }else{
                            showMessage(
                                requireActivity(),
                                TITLE_WARNING,
                                "gambar sudah dipilih, pilihlah pasangan katanya",
                                MotionToast.TOAST_WARNING
                            )
                        }
                    }
                })
                textAdapter.setOnTextClickCallback(object: TextPairQAdapter.OnTextClickCallback{
                    override fun onTextClicked(
                        textView: TextView,
                        pointView: ImageView,
                        position: Int
                    ) {
                        Log.d(TAG,"posisi text ${position}")
//                        Log.d(TAG,"isTextBeingSelected: ${isTextBeingSelected.toString()}")
                        val isTextViewHadSelected = findText(listSelectedPair,textView)

                        if(!isTextViewHadSelected && !isTextBeingSelected){
                            val stateDraw = drawDestLine(pointView)
                            Log.d(TAG,"state draw dest line: ${stateDraw}")
                            if(stateDraw) {
                                checkPair(textView.text.toString())

                                //change the text point to remover line
                                pointView.setImageResource(R.drawable.ic_red_close)
                                pointView.setBackgroundColor(Color.BLACK)
                            }
                        }else{
                            if(selectedImage == null){
                                //change the remover line to text point
                                pointView.setImageResource(R.drawable.bg_shape_button_blue)
                                pointView.setBackgroundColor(Color.TRANSPARENT)

                                //remove the line
                                removeLine(pointView)

                                //remove text from selected text list
                                val text = textView.text.toString().trim()
                                val filteredPairs = listSelectedPair.filterNot {
                                    it.second == text
                                }
                                listSelectedPair.clear()
                                listSelectedPair.addAll(filteredPairs)
                            }else{
                                showMessage(
                                    requireActivity(),
                                    TITLE_WARNING,
                                    "pilih kata yg belum dipasangkan",
                                    MotionToast.TOAST_WARNING
                                )
                            }

                        }
                    }

                })
                //seekbar
                seekBar.setOnTouchListener { view, motionEvent -> true }
                seekBar.max = 0
                seekBar.max = listPairQ.size
                seekBar.progress = indexProgress
                tvCorrectValue.text = "Benar\n${score}"

                //automatically play sounds and timer
                playAudioVoice()
                startTimer()

                //btn sound
                btnSound.setOnClickListener{ playAudioVoice() }

                val displayMetrics = DisplayMetrics()
                requireActivity().windowManager.defaultDisplay.getMetrics(displayMetrics)
                drawView.setDensitiyDpi(displayMetrics.densityDpi)
            }
        }
    }

    private fun nextPairQ() {
        reset()
        index++
        indexProgress++
        prepareQuestion()
    }

    //prepare list gambar n kata n shuffle it
    private fun prepareDataQ(result: PairWordQ): Pair<List<PairsItem>,List<String>> {
        val pairQ = result
        //for data image
        var pairsItem = pairQ.pairs
        var shuffledPairs = pairsItem!!.shuffled()

        //for data text
        var listText = ArrayList<String>()
        for((idx,value) in pairsItem.withIndex()){
            listText.add(value.kata!!)
        }
        var shuffledText = listText.shuffled()

        return Pair(shuffledPairs,shuffledText)
    }

    private fun drawStartLine(view: ImageView) {
        val coordinates = IntArray(2)
        view.getLocationOnScreen(coordinates)
        val x1 = coordinates[0].toFloat()
        val y1 = coordinates[1].toFloat()

        binding.drawView.addSourcePoint(x1,y1)
        isTextBeingSelected = false

        Log.d(TAG,"img point position x1:${x1},y1:${y1}")
    }

    private fun drawDestLine(view: ImageView) :Boolean {
        if(selectedImage != null){
            val coordinates = IntArray(2)
            view.getLocationInWindow(coordinates)
            val x2 = coordinates[0].toFloat()
            val y2 = coordinates[1].toFloat()
            binding.drawView.addDestinationPoint(x2,y2)

            isTextBeingSelected = true

            Log.d(TAG,"text point position x2:${x2},y2:${y2}")

            return true
        }else{
            showMessage(
                requireActivity(),
                TITLE_WARNING,
                "pilih dulu gambar yang ingin dipasangkan",
                MotionToast.TOAST_WARNING
            )
            return false
        }
    }

    private fun removeLine(view: ImageView) {
        isTextBeingSelected = false
        indexPair--

        val coordinates = IntArray(2)
        view.getLocationInWindow(coordinates)
        val x2 = coordinates[0].toFloat()
        val y2 = coordinates[1].toFloat()
        binding.drawView.removeLine(x2,y2)

        Log.d(TAG,"removeline() called")

    }

    private fun checkPair(text: String) {
        val imgPair = selectedImage!!.contentDescription.trim()
        val textPair = text.trim()
        if(textPair == imgPair){
            score++
        }
        //save pair
        selectedPair = Pair(selectedImage!!,text)
        listSelectedPair.add(selectedPair!!)

        selectedImage = null //update state image to null after finished using

        //reset after all pairing finished and move index PairQ
        if(indexPair ==  totalPairs){
            //cek dulu ada ndak soal selanjutnya
            if(index != listPairQ.size-1 && index < listPairQ.size){
                loader(true)
                //delay 1 second
                Handler(Looper.getMainLooper()).postDelayed({
                    loader(false)
                    nextPairQ()
                },1000)
            }else{
                loader(true)
                reset()
                moveToResult()
            }
        }else{
            indexPair++
            Log.d(TAG,"content description img : ${imgPair}")
            Log.d(TAG,"skor saat ini : ${score}")
        }
        Log.d(TAG,"jumlah gambar yg terpilih ${listSelectedPair.size}")
    }

    private fun observePairQ() {
        with(binding){
            viewModel.questions().observe(viewLifecycleOwner, { response ->
                loader(false)
                if (response.data != null) {
                    if(!response.data.isEmpty()){
                        if (response.code == 200) {
                            val result = response.data
                            listPairQ.addAll(result)
                            prepareQuestion()
                        } else {
                            dataNotFound()
                        }
                    }else{
                        dataNotFound()
                    }
                } else {
                    Log.e(TAG,"data is null")
                }
            })
        }
    }

    private fun moveToResult() {
        val toResult = PairFragmentDirections.actionPairFragmentToResultFragment().apply {
            totalQuestion = countTotalPairs
            correctNumber = score
            type = PAIR_TYPE
        }
        Handler(Looper.getMainLooper()).postDelayed({
            loader(false)
            findNavController().navigate(toResult)
        },200)//0.2s
    }

    private fun findImage(list: List<Pair<ImageView,String>>, view :View): Boolean {
        return list.filter{it.first == view}.isNotEmpty()
    }

    private fun findText(list: List<Pair<ImageView,String>>, view: TextView): Boolean {
        return list.filter{it.second == view.text.toString().trim()}.isNotEmpty()
    }

    private fun cancelTimer() {
        if(countDownTimer != null) countDownTimer.cancel()
    }

    private fun startTimer() {
        with(binding){
            countDownTimer = object : CountDownTimer(timeLeft, 1000) {

                override fun onTick(millisUntilFinished: Long) {
                    tvTimer.text = miliSecondToTimer(millisUntilFinished)
                }

                override fun onFinish() {
                    showMessage(
                        requireActivity(),
                        "Waktu Habis",
                        "Sayang sekali kesempatanmu untuk menjawab habis",
                        MotionToast.TOAST_WARNING
                    )
                    reset()
                    index++
                    indexProgress++
                    prepareQuestion()
                }
            }.start()
        }
    }

    private fun playAudioVoice() {
        mediaPlayer?.start()
    }

    private fun reset(){
        //clear all selected condition
        listSelectedImg.clear()
        listSelectedText.clear()
        binding.drawView.clearLines()

        //reset index
        indexPair =1

        //clear audio & timer
        releaseAudio()
        cancelTimer()
    }

    private fun releaseAudio(emptyMediaPlayer: Boolean = true) {
        mediaPlayer?.release()
        if(emptyMediaPlayer)mediaPlayer = null
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

    private fun dataNotFound(isPairsEmpty :Boolean = false) {
        with(binding) {
            if(isPairsEmpty){
                layoutEmpty.tvEmptyMessage.text = "Data pasangan tidak ditemukan"
            }
            val layoutEmpty = layoutEmpty.root
            layoutEmpty.visibility = View.VISIBLE
            imgAlarm.visibility = View.GONE
            tvTimer.visibility = View.GONE
            btnSound.visibility = View.GONE
            imgRight.visibility = View.GONE
            imgRight.visibility = View.GONE
            tvCorrectValue.visibility = View.GONE
        }
    }

    private fun loader(state: Boolean) {
        with(binding) {
            if (state) {
                pbLoader.visibility = View.VISIBLE
            } else {
                pbLoader.visibility = android.view.View.GONE
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
        if(countDownTimer != null) cancelTimer()
        if(mainActivity != null) mainActivity.mediaPlayer.start()
    }
}