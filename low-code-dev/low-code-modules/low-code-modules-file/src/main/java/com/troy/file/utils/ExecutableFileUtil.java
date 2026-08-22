package com.troy.file.utils;

import lombok.Data;

/**
 * @author chenxl
 * @description
 * @date 2024-07-26 16:48
 */
@Data
public class ExecutableFileUtil {

    public static boolean isExecutableFile(String mimeType, String fileName) {
        // 常见的可执行文件 MIME 类型
        String[] executableMimeTypes = {
                "application/x-msdownload", // Windows 可执行文件
                "application/x-sharedlib",  // Linux 共享库
                "application/x-dosexec",    // DOS 可执行文件
                "application/java-archive", // Java JAR 文件
                // 可以根据需要添加更多 MIME 类型
        };

        // 常见的可执行文件扩展名
        String[] executableExtensions = {
                ".exe", ".sh", ".bat", ".bin", ".run", ".jar", ".msi", ".cmd",
                // 可以根据需要添加更多扩展名
        };

        for (String executableMimeType : executableMimeTypes) {
            if (mimeType != null && mimeType.equals(executableMimeType)) {
                return true;
            }
        }

        for (String extension : executableExtensions) {
            if (fileName != null && fileName.toLowerCase().endsWith(extension)) {
                return true;
            }
        }

        return false;
    }
}
