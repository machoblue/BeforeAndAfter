package org.macho.beforeandafter.preference.backup

import org.macho.beforeandafter.shared.data.Record

class BackupData(val records: List<Record>, var imageFileNameToDriveFileId: Map<String, String>) {
}