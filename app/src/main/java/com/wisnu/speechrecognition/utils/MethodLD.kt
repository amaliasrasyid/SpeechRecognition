package com.wisnu.speechrecognition.utils

import kotlin.math.max
import kotlin.math.min

/**
 *  X =
 *  Y =
 */
    fun getLevenshteinDistance(X: String, Y: String): Int {
        val m = X.length
        val n = Y.length
        val T = Array(m + 1) { IntArray(n + 1) }
        for (i in 1..m) {
            T[i][0] = i
        }
        for (j in 1..n) {
            T[0][j] = j
        }
        var cost: Int
        for (i in 1..m) {
            for (j in 1..n) {
                cost = if (X[i - 1] == Y[j - 1]) 0 else 1
                T[i][j] = min(min(T[i - 1][j] + 1, T[i][j - 1] + 1),
                    T[i - 1][j - 1] + cost)
            }
        }
        return T[m][n]
    }

    fun findSimilarity(x: String?, y: String?): Double {
        require(!(x == null || y == null)) { "Strings should not be null" }

        val maxLength = max(x.length, y.length)
        return if (maxLength > 0) {
            (maxLength * 1.0 - getLevenshteinDistance(x, y)) / maxLength * 1.0
        } else 1.0
    }

/**
 * NOTE: huruf yg salah dihitung dr huruf (hasil ucapan siswa) yg tidak sesuai dg jawaban
 * TIDAK TERMASUK jumlah huruf yg tidak disebutkannya
 * misal: "ayah" & "at", Huruf yg salah hanya 1
 */
fun countRightWrongWord(x: String,y: String): HashMap<String,Int>{
    var hashMap = HashMap<String,Int>()
    var rResult = 0
    var wResult = 0
    val arrCharsX = x.toCharArray() //teks jawaban Soal
    val arrCharsY = y.toCharArray() //teks hasil speech recognition
    var markX = -1

//    print(arrCharsX.size)

    for((i,valY) in arrCharsY.withIndex()){
        for ((j,valX) in arrCharsX.withIndex()){
            if(valY == valX && j != markX){
                rResult++
                markX = j
                break
            }else if(j == (arrCharsX.size-1)){
                wResult++
            }
        }
    }
    hashMap["righWord"] = rResult
    hashMap["wrongWord"] = wResult
    return hashMap
}
