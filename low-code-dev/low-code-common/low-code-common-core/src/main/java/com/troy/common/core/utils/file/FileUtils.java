package com.troy.common.core.utils.file;

import com.troy.common.core.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.resizers.configurations.Rendering;
import org.apache.commons.lang3.ArrayUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @Author ZhuQing
 * @Date: 2022/7/6  13:29
 * 文件处理工具类
 */
@Slf4j
public class FileUtils extends org.apache.commons.io.FileUtils {


    // 图片压缩默认配置
    private static final double DEFAULT_INITIAL_QUALITY = 0.85;
    private static final double DEFAULT_MIN_QUALITY = 0.3;
    private static final double DEFAULT_QUALITY_STEP = 0.08;
    private static final int DEFAULT_MAX_ATTEMPTS = 12;


    /**
     * 字符常量：斜杠 {@code '/'}
     */
    public static final char SLASH = '/';

    /**
     * 字符常量：反斜杠 {@code '\\'}
     */
    public static final char BACKSLASH = '\\';

    public static String FILENAME_PATTERN = "[a-zA-Z0-9_\\-\\|\\.\\u4e00-\\u9fa5]+";

    /**
     * 输出指定文件的byte数组
     *
     * @param filePath 文件路径
     * @param os       输出流
     * @return
     */
    public static void writeBytes(String filePath, OutputStream os) throws IOException {
        FileInputStream fis = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                throw new FileNotFoundException(filePath);
            }
            fis = new FileInputStream(file);
            byte[] b = new byte[1024];
            int length;
            while ((length = fis.read(b)) > 0) {
                os.write(b, 0, length);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除文件
     *
     * @param filePath 文件
     * @return
     */
    public static boolean deleteFile(String filePath) {
        boolean flag = false;
        File file = new File(filePath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
            flag = true;
        }
        return flag;
    }

    /**
     * 文件名称验证
     *
     * @param filename 文件名称
     * @return true 正常 false 非法
     */
    public static boolean isValidFilename(String filename) {
        return filename.matches(FILENAME_PATTERN);
    }

    /**
     * 检查文件是否可下载
     *
     * @param resource 需要下载的文件
     * @return true 正常 false 非法
     */
    public static boolean checkAllowDownload(String resource) {
        // 禁止目录上跳级别
        if (StringUtils.contains(resource, "..")) {
            return false;
        }

        // 检查允许下载的文件规则
        if (ArrayUtils.contains(MimeTypeUtils.DEFAULT_ALLOWED_EXTENSION, FileTypeUtils.getFileType(resource))) {
            return true;
        }

        // 不在允许下载的文件规则
        return false;
    }

    /**
     * 下载文件名重新编码
     *
     * @param request  请求对象
     * @param fileName 文件名
     * @return 编码后的文件名
     */
    public static String setFileDownloadHeader(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        final String agent = request.getHeader("USER-AGENT");
        String filename = fileName;
        if (agent.contains("MSIE")) {
            // IE浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+", " ");
        } else if (agent.contains("Firefox")) {
            // 火狐浏览器
            filename = new String(fileName.getBytes(), "ISO8859-1");
        } else if (agent.contains("Chrome")) {
            // google浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        } else {
            // 其它浏览器
            filename = URLEncoder.encode(filename, "utf-8");
        }
        return filename;
    }

    /**
     * 返回文件名
     *
     * @param filePath 文件
     * @return 文件名
     */
    public static String getName(String filePath) {
        if (null == filePath) {
            return null;
        }
        int len = filePath.length();
        if (0 == len) {
            return filePath;
        }
        if (isFileSeparator(filePath.charAt(len - 1))) {
            // 以分隔符结尾的去掉结尾分隔符
            len--;
        }

        int begin = 0;
        char c;
        for (int i = len - 1; i > -1; i--) {
            c = filePath.charAt(i);
            if (isFileSeparator(c)) {
                // 查找最后一个路径分隔符（/或者\）
                begin = i + 1;
                break;
            }
        }

        return filePath.substring(begin, len);
    }

    /**
     * 是否为Windows或者Linux（Unix）文件分隔符<br>
     * Windows平台下分隔符为\，Linux（Unix）为/
     *
     * @param c 字符
     * @return 是否为Windows或者Linux（Unix）文件分隔符
     */
    public static boolean isFileSeparator(char c) {
        return SLASH == c || BACKSLASH == c;
    }

    /**
     * 下载文件名重新编码
     *
     * @param response     响应对象
     * @param realFileName 真实文件名
     * @return
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) throws UnsupportedEncodingException {
        String percentEncodedFileName = percentEncode(realFileName);

        StringBuilder contentDispositionValue = new StringBuilder();
        contentDispositionValue.append("attachment; filename=")
                .append(percentEncodedFileName)
                .append(";")
                .append("filename*=")
                .append("utf-8''")
                .append(percentEncodedFileName);

        response.setHeader("Content-disposition", contentDispositionValue.toString());
        response.setHeader("download-filename", percentEncodedFileName);
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) throws UnsupportedEncodingException {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
        return encode.replaceAll("\\+", "%20");
    }

    /**
     * 获取图片尺寸
     *
     * @param file
     * @return
     */
    public static ImageSize getImageSize(File file) {
        ImageSize imageSize = null;
        try {
            BufferedImage image = ImageIO.read(file);
            imageSize = new ImageSize();
            imageSize.setWidth(image.getWidth());
            imageSize.setHeight(image.getHeight());
        } catch (IOException e) {
            log.error("获取文件尺寸失败", e);
        }
        return imageSize;

    }

    /**
     * 通过url得到文件
     *
     * @param urlString
     * @return
     */
    public static InputStream urlToInput(String urlString) throws Exception {
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }

    /**
     * 得到文件后缀
     *
     * @param fileName
     * @return
     */
    public static String getSuffix(String fileName) {
        return StringUtils.substring(fileName, StringUtils.lastIndexOf(fileName, "."));
    }

    public static String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();
        try {
            List<String> list = Files.readAllLines(Paths.get(filePath));
            if (StringUtils.isNotEmpty(list)) {
                for (String s : list) {
                    sb.append(s.trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static void writeFile(String filePath, List<String> lines) {
        try {
            Files.write(Paths.get(filePath), lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 将File转换为InputStream
     * @param file 要转换的文件
     * @return InputStream对象
     * @throws IOException 如果文件不存在或无法读取
     */
    public static InputStream fileToInputStream(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        if (!file.exists()) {
            throw new IOException("File does not exist: " + file.getAbsolutePath());
        }
        if (!file.canRead()) {
            throw new IOException("Cannot read file: " + file.getAbsolutePath());
        }

        return new FileInputStream(file);
    }

    /**
     * 压缩图片到指定大小
     * @param imageBytes 原始图片字节数组
     * @param maxSizeKB 目标最大大小(KB)
     * @param format 图片格式 ("jpg", "png", "gif", "bmp")
     * @return 压缩后的字节数组
     */
    public static byte[] compressImage(byte[] imageBytes, long maxSizeKB, String format) {
        try {
            return compressImage(imageBytes, maxSizeKB, format, DEFAULT_INITIAL_QUALITY);
        } catch (Exception e) {
            log.error("压缩图片失败", e);
        }
        return imageBytes;
    }

    /**
     * 压缩图片到指定大小（可指定初始质量）
     * @param imageBytes 原始图片字节数组
     * @param maxSizeKB 目标最大大小(KB)
     * @param format 图片格式
     * @param initialQuality 初始质量 (0.0-1.0)
     * @return 压缩后的字节数组
     */
    public static byte[] compressImage(byte[] imageBytes, long maxSizeKB, String format,
                                       double initialQuality) throws IOException {
        long maxSizeBytes = maxSizeKB * 1024;

        // 检查是否需要压缩
        if (imageBytes.length <= maxSizeBytes) {
           log.info("图片无需压缩，当前大小: {}KB", imageBytes.length / 1024);
            return imageBytes;
        }

        log.info("开始压缩: {}KB → {}KB, 格式: {}",
                imageBytes.length / 1024, maxSizeKB, format);


        double currentQuality = Math.min(1.0, Math.max(0.1, initialQuality));
        byte[] compressedBytes = imageBytes;
        int originalWidth = 0;
        int originalHeight = 0;

        // 获取原始图片尺寸信息
        try (ByteArrayInputStream sizeStream = new ByteArrayInputStream(imageBytes)) {
            BufferedImage img = ImageIO.read(sizeStream);
            if (img != null) {
                originalWidth = img.getWidth();
                originalHeight = img.getHeight();
                log.info("图片尺寸: {} x {}", originalWidth, originalHeight);
            }
        }

        // 递归压缩循环
        for (int attempt = 1; attempt <= DEFAULT_MAX_ATTEMPTS; attempt++) {
            log.info("压缩尝试 {}: 质量 {:.2f}", attempt, currentQuality);

            compressedBytes = compressSinglePass(compressedBytes, currentQuality, format,
                    originalWidth, originalHeight, attempt);

            long currentSizeKB = compressedBytes.length / 1024;
            log.info("第 {} 次压缩后: {}KB", attempt, currentSizeKB);

            // 检查是否达到目标
            if (compressedBytes.length <= maxSizeBytes) {
                log.info("✓ 压缩成功，最终大小: {}KB", currentSizeKB);
                return compressedBytes;
            }

            // 动态调整压缩参数
            CompressionParams nextParams = adjustCompressionParams(
                    currentQuality, currentSizeKB, maxSizeKB, attempt,
                    originalWidth, originalHeight
            );

            currentQuality = nextParams.quality;

            // 检查是否达到压缩极限
            if (currentQuality < DEFAULT_MIN_QUALITY && nextParams.scale < 0.5) {
                log.warn("⚠ 已达到压缩极限，当前大小: {}KB", currentSizeKB);
                break;
            }
        }

        log.info("ℹ 压缩完成，最终大小: {}KB", compressedBytes.length / 1024);
        return compressedBytes;
    }

    /**
     * 单次压缩过程
     */
    private static byte[] compressSinglePass(byte[] imageBytes, double quality, String format,
                                             int originalWidth, int originalHeight, int attempt) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Thumbnails.Builder<?> builder = Thumbnails.of(inputStream)
                    .outputFormat(format)
                    .outputQuality(quality)
                    .rendering(Rendering.QUALITY);

            // 根据尝试次数和原始尺寸决定是否缩放
            double scale = calculateScale(quality, originalWidth, originalHeight, attempt);
            if (scale < 1.0) {
                builder.scale(scale);
                log.info("  应用缩放: {:.2f}", scale);
            } else {
                builder.scale(1.0);
            }

            builder.toOutputStream(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 计算缩放比例
     */
    private static double calculateScale(double quality, int width, int height, int attempt) {
        // 如果是大图且已经尝试多次，考虑缩放
        if (attempt >= 3) {
            if (width > 4000 || height > 4000) {
                return Math.max(0.3, 0.9 - attempt * 0.1);
            } else if (width > 2000 || height > 2000) {
                return Math.max(0.5, 0.95 - attempt * 0.08);
            } else if (width > 1000 || height > 1000) {
                return Math.max(0.7, 1.0 - attempt * 0.05);
            }
        }

        // 质量很低时也考虑缩放
        if (quality < 0.5 && attempt >= 5) {
            return 0.8;
        }

        return 1.0; // 不缩放
    }

    /**
     * 动态调整压缩参数
     */
    private static CompressionParams adjustCompressionParams(double currentQuality, long currentSizeKB,
                                                             long targetSizeKB, int attempt,
                                                             int width, int height) {
        double sizeRatio = (double) currentSizeKB / targetSizeKB;
        CompressionParams params = new CompressionParams();

        // 根据大小比例动态调整质量
        if (sizeRatio > 5.0) {
            params.quality = currentQuality - 0.15;
        } else if (sizeRatio > 3.0) {
            params.quality = currentQuality - 0.12;
        } else if (sizeRatio > 2.0) {
            params.quality = currentQuality - 0.09;
        } else if (sizeRatio > 1.5) {
            params.quality = currentQuality - 0.06;
        } else {
            params.quality = currentQuality - 0.04;
        }

        // 确保质量在合理范围内
        params.quality = Math.max(DEFAULT_MIN_QUALITY, params.quality);

        // 根据尝试次数和图片尺寸决定缩放
        if (attempt >= 4 && (width > 1500 || height > 1500)) {
            params.scale = 0.8;
        } else if (attempt >= 6) {
            params.scale = 0.7;
        } else {
            params.scale = 1.0;
        }

        return params;
    }

    /**
     * 压缩参数容器类
     */
    private static class CompressionParams {
        double quality;
        double scale = 1.0;
    }

    /**
     * 获取图片信息
     */
    public static void printImageInfo(byte[] imageBytes) throws IOException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(imageBytes)) {
            BufferedImage image = ImageIO.read(input);
            if (image != null) {
                log.info("图片信息: {} x {} 像素, 大小: {}KB",
                        image.getWidth(), image.getHeight(), imageBytes.length / 1024);
            }
        }
    }
}
