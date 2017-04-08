package com.ediposouza.model

/**
 * Created by ediposouza on 05/04/17.
 */
data class FirebaseAuth(

        val postBody: String,
        val requestUri: String = "http://localhost",
        val returnSecureToken: Boolean = true

) {

    constructor(accessToken: String, providerId: String = "google.com") : this(
            postBody = "access_token=$accessToken&providerId=$providerId"
    )

}