/*
 This file is part of Privacy Friendly Sudoku.

 Privacy Friendly Sudoku is free software:
 you can redistribute it and/or modify it under the terms of the
 GNU General Public License as published by the Free Software Foundation,
 either version 3 of the License, or any later version.

 Privacy Friendly Sudoku is distributed in the hope
 that it will be useful, but WITHOUT ANY WARRANTY; without even
 the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Privacy Friendly Sudoku. If not, see <http://www.gnu.org/licenses/>.
 */
package org.secuso.privacyfriendlysudoku

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDex
import androidx.work.Configuration
import org.secuso.privacyfriendlysudoku.backup.BackupCreator
import org.secuso.privacyfriendlysudoku.backup.BackupRestorer
import org.secuso.privacyfriendlybackup.api.pfa.BackupManager

class PFSudoku : Application(), Configuration.Provider {
    override fun onCreate() {
        BackupManager.backupCreator = BackupCreator()
        BackupManager.backupRestorer = BackupRestorer()
        super.onCreate()
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // channels
            val channel = NotificationChannel(CHANNEL_ID, "Default", NotificationManager.IMPORTANCE_LOW)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager?.createNotificationChannel(channel)
        }
    }

    override val workManagerConfiguration by lazy {
        Configuration.Builder().setMinimumLoggingLevel(Log.INFO).build()
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {
        const val CHANNEL_ID = "sudoku.0"
    }
}
