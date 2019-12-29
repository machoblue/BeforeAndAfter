package org.macho.beforeandafter.preference.backup

import android.accounts.Account
import android.content.Context
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveScopes
import java.util.*

object DriveUtil {
    fun buildDriveService(context: Context, account: Account): Drive? {
        return GoogleAccountCredential.usingOAuth2(context, Collections.singleton(DriveScopes.DRIVE_APPDATA)).let { credential ->
            credential.selectedAccount = account
            Drive.Builder(AndroidHttp.newCompatibleTransport(), GsonFactory(), credential)
                    .setApplicationName("BeforeAndAfter")
                    .build()
        }
    }
}