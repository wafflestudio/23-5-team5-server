package com.team5.studygroup.common

import io.awspring.cloud.s3.S3Template
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.net.URI
import java.util.UUID

@Service
class S3Service(
    private val s3Template: S3Template,
    @Value("\${spring.cloud.aws.s3.bucket}")
    private val bucketName: String,
) {
    fun upload(
        file: MultipartFile,
        dirName: String = "images",
    ): String {
        val fileName = "$dirName/${UUID.randomUUID()}-${file.originalFilename}"

        return s3Template.upload(bucketName, fileName, file.inputStream)
            .url
            .toString()
    }

    fun delete(fileUrl: String) {
        try {
            val path = URI(fileUrl).path
            val key = if (path.startsWith("/")) path.substring(1) else path

            s3Template.deleteObject(bucketName, key)
        } catch (e: Exception) {
            println("S3 파일 삭제 실패: ${e.message}")
        }
    }
}
