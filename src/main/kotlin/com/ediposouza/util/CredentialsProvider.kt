/*
 * Copyright (c) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.ediposouza.util

import com.ediposouza.TESLTracker
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.oauth2.Oauth2
import java.io.File
import java.io.IOException
import java.io.InputStreamReader

/**
 * Utility class to provide credentials and cache them in a local file.
 */
object CredentialsProvider {

    /** Directory to store user credentials.  */
    private val DATA_STORE_DIR = File(System.getProperty("user.home"), ".store/tesl_tracker")

    /** Scopes */
    val SCOPE_PROFILE_EMAIL = "https://www.googleapis.com/auth/userinfo.email"
    val SCOPE_PROFILE_PROFILE = "https://www.googleapis.com/auth/userinfo.profile"

    /**
     * Global instance of the [DataStoreFactory]. The best practice is to make it a single
     * globally shared instance across your application, but for this sample, it's only useful
     * for saving credentials across runs.
     */
    private val dataStoreFactory: FileDataStoreFactory by lazy { FileDataStoreFactory(DATA_STORE_DIR) }

    var oauth2: Oauth2? = null

    /**
     * Authorizes the installed application to access user's protected data.

     *
     * If you plan to run on AppEngine or Compute Engine, consider instead
     * [GoogleCredential.getApplicationDefault], which will use the ambient credentials
     * for the project's service-account.
     */
    @Throws(IOException::class)
    fun authorize(httpTransport: HttpTransport, jsonFactory: JsonFactory): Credential? {
        // load client secrets
        val keysFileStream = InputStreamReader(TESLTracker::class.java.getResourceAsStream("/client_secrets.json"))
        val clientSecrets = GoogleClientSecrets.load(jsonFactory, keysFileStream)
        keysFileStream.close()
        if (clientSecrets.details.clientId.startsWith("Enter") || clientSecrets.details.clientSecret.startsWith("Enter")) {
            Logger.e("API Keys not set")
            return null
        }
        // set up authorization code flow
        val scopes = setOf(SCOPE_PROFILE_EMAIL, SCOPE_PROFILE_PROFILE)
        val flow = GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, clientSecrets, scopes)
                .setDataStoreFactory(dataStoreFactory).build()
        // authorize
        return AuthorizationCodeInstalledApp(flow, LocalServerReceiver()).authorize("user")
    }

    fun hasCredentialsSaved(): Boolean = File(DATA_STORE_DIR, "StoredCredential").exists()

}
