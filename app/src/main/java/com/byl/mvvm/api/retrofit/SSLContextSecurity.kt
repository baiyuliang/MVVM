package com.byl.mvvm.api.retrofit

import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.*
import javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier
import javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory

object SSLContextSecurity {

    fun createIgnoreVerifySSL(sslVersion: String): SSLSocketFactory {
        var sc = SSLContext.getInstance(sslVersion)
        val trustAllCerts: Array<TrustManager> = arrayOf(object : X509TrustManager {
            @Throws(CertificateException::class)
            override fun checkClientTrusted(
                chain: Array<X509Certificate>, authType: String
            ) {
            }

            @Throws(CertificateException::class)
            override fun checkServerTrusted(
                chain: Array<X509Certificate>,
                authType: String
            ) {
            }

            override fun getAcceptedIssuers(): Array<X509Certificate?> {
                return arrayOfNulls(0)
            }
        })

        sc?.init(null, trustAllCerts, java.security.SecureRandom())

        // Create all-trusting host name verifier
        val allHostsValid = HostnameVerifier { _, _ -> true }

        setDefaultSSLSocketFactory(sc.socketFactory)
        setDefaultHostnameVerifier(allHostsValid)
        return sc.socketFactory
    }
}