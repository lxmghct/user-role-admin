package com.example.common.utils;

import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

/**
 * 文件工具类
 */
public class FileUtils {

    /**
     * 创建或更新MultipartFile
     *
     * @param folderPath    文件夹路径
     * @param fileName      文件名
     * @param multipartFile MultipartFile
     */
    public static boolean createOrUpdateMultipartFile(String folderPath, String fileName, MultipartFile multipartFile) {
        if (multipartFile == null) {
            return false;
        }
        File folder = new File(folderPath);
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return false;
            }
        }
        File file = new File(folder.getAbsolutePath() + File.separator + fileName);
        try {
            multipartFile.transferTo(file);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 删除文件或目录
     *
     * @param path 要删除的文件或目录的路径
     * @return 删除操作是否成功
     */
    public static boolean deleteFileOrDirectory(String path) {
        try {
            Path filePath = Paths.get(path);
            if (Files.notExists(filePath)) {
                System.out.println("文件或目录不存在");
                return false;
            }

            // 删除目录及其内容
            try {
                Files.walk(filePath)
                        .sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                e.printStackTrace();
                                throw new RuntimeException("删除文件失败: " + p, e);
                            }
                        });
            } catch (RuntimeException ignored) {
                return false;
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 将图片写入response
     *
     * @param path     图片路径
     * @param response HttpServletResponse
     * @throws IOException IO异常
     */
    public static void writeImageToResponse(String path, HttpServletResponse response) throws IOException {
        try (InputStream imageInputStream = new FileInputStream(path)) {
            response.setContentType(MediaType.IMAGE_JPEG_VALUE);
            IOUtils.copy(imageInputStream, response.getOutputStream());
        }
    }

}
