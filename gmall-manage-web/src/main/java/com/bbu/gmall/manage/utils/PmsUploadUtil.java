package com.bbu.gmall.manage.utils;

import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.web.multipart.MultipartFile;

public class PmsUploadUtil {

    public static String uploadImage(MultipartFile multipartFile) {

        String imgUrl = "http://192.168.142.132";
        try {
            //读取配置文件
            String tracker = PmsUploadUtil.class.getResource("/tracker.conf").getPath();

            ClientGlobal.init(tracker);
            TrackerClient trackerClient = new TrackerClient();

            TrackerServer trackerServer = trackerClient.getConnection();

            StorageClient storageClient = new StorageClient(trackerServer, null);

            byte[] fileBytes = multipartFile.getBytes();

            String originalFilename = multipartFile.getOriginalFilename();

            String extName = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);
            String[] uploadInfos = storageClient.upload_file(fileBytes, extName, null);
            for (String uploadInfo : uploadInfos) {
                imgUrl += "/" + uploadInfo;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imgUrl;
    }
}
