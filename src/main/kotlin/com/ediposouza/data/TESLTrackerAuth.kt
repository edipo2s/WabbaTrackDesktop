package com.ediposouza.data

import com.ediposouza.TESLTracker
import com.ediposouza.model.FirebaseAuth
import com.ediposouza.util.CredentialsProvider
import com.ediposouza.util.Logger
import com.ediposouza.util.Mixpanel
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.oauth2.Oauth2
import com.google.gson.Gson
import com.google.gson.JsonParser
import tornadofx.Rest
import java.io.InputStreamReader

/**
 * Created by ediposouza on 05/04/17.
 */
object TESLTrackerAuth {

    private val keysFileStream by lazy { InputStreamReader(TESLTracker::class.java.getResourceAsStream("/client_secrets.json")) }

    var userAccessToken: String? = null
    var userUuid: String? = null
    var userName: String? = null
    var userEmail: String? = null
    var userPhoto: String? = null

    var firebaseLoginAPI: Rest = Rest().apply {
        baseURI = "https://www.googleapis.com/identitytoolkit/v3/relyingparty/"
    }

    val apiKey by lazy {
        val json = JsonParser().parse(keysFileStream).asJsonObject
        json.get("api_key").asString.apply {
            keysFileStream.close()
        }

    }

    fun login(): Boolean {
        try {
            val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()
            val credential = CredentialsProvider.authorize(httpTransport, jsonFactory)
            CredentialsProvider.oauth2 = Oauth2.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName(TESLTracker.APP_NAME).build()
//            Logger.d("Validating a token: \n Access Token: ${credential?.accessToken}")

            Logger.d("Validating a token:")
            userAccessToken = credential?.accessToken
            CredentialsProvider.oauth2?.run {
                //                val tokeninfo = tokeninfo().setAccessToken(userAccessToken).execute()
//                Logger.d(tokeninfo.toPrettyString())
                Logger.d("Obtaining User Profile Information:")
                val userinfo = userinfo().get().execute()
                userName = "${userinfo.givenName} ${userinfo.familyName}"
                userEmail = userinfo.email
                userPhoto = userinfo.picture
//                Logger.d(userinfo.toPrettyString())
            }

            val body = Gson().toJson(FirebaseAuth(userAccessToken ?: ""))
            Logger.d("Getting firebase ID:")
            firebaseLoginAPI.post("verifyAssertion?key=$apiKey", body.byteInputStream()) { processor ->
                processor.addHeader("Content-Type", "application/json")
            }.one().apply {
                userUuid = getString("localId")
//                Logger.d("FirebaseID: $userUuid")
            }
            Mixpanel.trackUser()
            return true
        } catch (e: Exception) {
            Logger.e(e)
            return false
        }
    }

    fun isUserLogged() = userUuid != null

    fun hasLoginCredentialsSaved(): Boolean = CredentialsProvider.hasCredentialsSaved()

}