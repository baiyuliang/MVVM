package com.byl.mvvm.util

import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

object Sign {

    private const val AUTH_SECRET = "123456"//前后台协议密钥

    /**
     * 无参
     */
    fun signNoParams(): LinkedHashMap<String, String?> {
        val params = LinkedHashMap<String, String?>()
        params["sign"] = getSign(params)
        return params
    }

    /**
     * 有参
     */
    fun signParams(params: LinkedHashMap<String, String?>): LinkedHashMap<String, String?> {
        params["sign"] = getSign(params)
        return params
    }


    /**
     * 签名
     */
    private fun getSign(params: LinkedHashMap<String, String?>): String {
        val sb = StringBuilder()
        params.forEach {
            val key = it.key
            var value = ""
            if (!it.value.isNullOrEmpty()) {
                value = URLEncoder.encode(it.value).replace("\\+", "%20")
            }
            sb.append("$key=$value&")
        }
        val s = sb.toString().substring(0, sb.toString().length - 1).toLowerCase() + AUTH_SECRET
        return encryption(s)
    }


    /**
     * MD5加密
     *
     * @param plainText 明文
     * @return 32位密文
     */
    private fun encryption(plainText: String): String {
        var re_md5 = ""
        try {
            val md: MessageDigest = MessageDigest.getInstance("MD5")
            md.update(plainText.toByteArray())
            val b: ByteArray = md.digest()
            var i: Int
            val buf = StringBuffer("")
            for (offset in b.indices) {
                i = b[offset].toInt()
                if (i < 0) i += 256
                if (i < 16) buf.append("0")
                buf.append(Integer.toHexString(i))
            }
            re_md5 = buf.toString()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return re_md5
    }
}