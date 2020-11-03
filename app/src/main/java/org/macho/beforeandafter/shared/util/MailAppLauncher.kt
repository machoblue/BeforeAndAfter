package org.macho.beforeandafter.shared.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import org.macho.beforeandafter.BuildConfig
import org.macho.beforeandafter.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MailAppLauncher @Inject constructor() {

    fun launchMailApp(context: Context) {
        val intent = Intent(Intent.ACTION_SENDTO).also {
            it.data = Uri.parse("mailto:")
            it.putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.developer_mail_address)))
            it.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.bug_report_mail_title))
            it.putExtra(Intent.EXTRA_TEXT, String.format(context.getString(R.string.bug_report_mail_template), context.getString(R.string.developer_mail_address), Build.VERSION.SDK_INT, BuildConfig.VERSION_NAME, Build.MODEL))
        }

        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        }
    }
}