package com.wisnu.speechrecognition.view.main.ui.student.play.pair

import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.postDelayed
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.wisnu.speechrecognition.adapter.ImagePairQAdapter
import com.wisnu.speechrecognition.adapter.TextPairQAdapter
import com.wisnu.speechrecognition.databinding.FragmentPairBinding
import com.wisnu.speechrecognition.model.questions.PairWordQ
import com.wisnu.speechrecognition.model.questions.PairsItem
import com.wisnu.speechrecognition.network.ApiConfig
import com.wisnu.speechrecognition.utils.UtilsCode.TITLE_WARNING
import com.wisnu.speechrecognition.utils.showMessage
import com.wisnu.speechrecognition.view.main.ui.teacher.kelolasoal.pairQ.PairQViewModel
import com.wisnu.speechrecognition.utils.miliSecondToTimer
import com.wisnu.speechrecognition.view.main.ui.student.ResultFragment.Companion.PAIR_TYPE
import com.wisnu.speechrecognition.view.main.ui.student.play.guess.GuessFragmentDirections
import www.sanju.motiontoast.MotionToast

class PairFragment : Fragment() {
    private lateinit var binding: FragmentPairBinding

    private var selectedImage: ImageView? = null
    private var selectedText: TextView? = null
    private var listSelectedImg = ArrayList<ImageView>()
    private var listSelectedText = ArrayList<TextView>()
    private var listPairQ = ArrayList<PairWordQ>()
    private var listText = ArrayList<String>()

    private var isTextBeingSelected = false

    private var score :Int = 0
    private var index = 0
    private var nextQ = 1
    private var totalQ = 0
    private var indexProgress = 1

    private var indexPair = 1
    private var totalPairs = 0

    private var countTotalPairs = 0

    private lateinit var countDownTimer: CountDownTimer
    private var timeLeft: Long = 180000 //3minute per question

    private var mediaPlayer: MediaPlayer? = null

    private lateinit var imgAdapter: ImagePairQAdapter
    private lateinit var textAdapter: TextPairQAdapter

    private val viewModel by viewModels<PairQViewModel>()

    private val TAG = PairFragment::class.java.simpleName

    companion object{
        private const val CLICKED_TEXT = 100
        private const val CLICKED_IMAGE = 200
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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

    private fun initAdapter(result: List<PairWordQ>) {
//        imgAdapter = ImagePairQAdapter(requireActivity(),0,result)
    }

    private fun prepareQuestion() {
        with(binding){
            val pairQ = listPairQ.get(index) //TODO: Rentan error, karna list data pairQ meski dia tak ada pairs
            totalPairs = pairQ.pairs!!.size
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

            imgAdapter.setOnImageClickCallback(object: ImagePairQAdapter.OnImageClickCallback{
                override fun onImageClicked(img: ImageView, imgPoint: ImageView) {
                    if(selectedImage != img){
                        val isImageHadSelected = find(listSelectedImg,img)
                        if(!isImageHadSelected){
                            selectedImage = img
                            drawStartLine(imgPoint)
                            listSelectedImg.add(img)
                        }else{
                            showMessage(
                                requireActivity(),
                                TITLE_WARNING,
                                "gambar sudah dipasangkan dengan katanya, silahkan pasangkan gambar yang lain",
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
                override fun onTextClicked(textView: TextView, pointView: ImageView) {
                    if(selectedText != textView){
                        val isTextViewHadSelected = find(listSelectedText,textView)
                        if(!isTextViewHadSelected){
                            if(!isTextBeingSelected){
                                drawDestLine(pointView)
                                selectedText = textView
                                checkPair(textView.text.toString())
                                listSelectedText.add(textView)
                            }else{
                                showMessage(
                                    requireActivity(),
                                    TITLE_WARNING,
                                    "kata hanya bisa dipasangkan satu kali saja",
                                    MotionToast.TOAST_WARNING
                                )
                            }
                        }else{
                            showMessage(
                                requireActivity(),
                                TITLE_WARNING,
                                "kata sudah dipasangkan",
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
        }
    }

    //prepare list gambar n kata n shuffle it
    //TODO: shuffle element random blm berhasil
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
//        val width = view.width/2
        val coordinates = IntArray(2)
        view.getLocationOnScreen(coordinates)
        val x1 = coordinates[0].toFloat()
        val y1 = coordinates[1].toFloat()

        binding.drawView.addSourcePoint(x1,y1)
        isTextBeingSelected = false

        Log.d(TAG,"img point position x1:${x1},y1:${y1}")
        Log.d(TAG,"coordinates ${coordinates[0]},${coordinates[1]}")

    }

    private fun drawDestLine(view: ImageView) {
        if(selectedImage != null){
            val coordinates = IntArray(2)
            view.getLocationOnScreen(coordinates)
            val x2 = coordinates[0].toFloat()
            val y2 = coordinates[1].toFloat()
            binding.drawView.addDestinationPoint(x2,y2)

            isTextBeingSelected = true

            Log.d(TAG,"text point position x2:${x2},y2:${y2}")
            Log.d(TAG,"coordinates ${coordinates[0]},${coordinates[1]}")
        }else{
            showMessage(
                requireActivity(),
                TITLE_WARNING,
                "pilih dulu gambar yang ingin dipasangkan",
                MotionToast.TOAST_WARNING
            )
        }
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

    private fun checkPair(text: String) {
        val imgPair = selectedImage!!.contentDescription.trim()
        val textPair = text.trim()
        if(textPair == imgPair){
            score++
        }
        //reset after all pairing finished and move index PairQ
        if(indexPair ==  totalPairs){
            //cek dulu ada ndak soal selanjutnya
            if(index != listPairQ.size-1 && index < listPairQ.size){
                loader(true)
                //delay 1 second
                Handler(Looper.getMainLooper()).postDelayed({
                    loader(false)
                    reset()
                    index++
                    indexProgress++
                    prepareQuestion()
                },500)
            }else{
                loader(true)
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
        }else{
            indexPair++
            Log.d(TAG,"content description img : ${imgPair}")
            Log.d(TAG,"skor saat ini : ${score}")
        }
    }

    private fun find(list: List<Any>, view :View): Boolean {
        return list.filter{it == view}.isNotEmpty()
    }

    private fun cancelTimer() {
        countDownTimer.cancel()
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

    private fun releaseAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
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

    private fun dataNotFound() {
        with(binding) {
            val layoutEmpty = layoutEmpty.root
            layoutEmpty.visibility = android.view.View.VISIBLE
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