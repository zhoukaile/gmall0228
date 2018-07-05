package com.atguigu.gmall.manger.test;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import java.io.IOException;

public class FileUpload {
    @Test
    public void textFileUpload() throws IOException, MyException {
        /*读取配置文件，取得服务器的ip地址*/
        String file = this.getClass().getResource("/tracker.conf").getFile();
        ClientGlobal.init(file);

        TrackerClient trackerClient=new TrackerClient();
        TrackerServer trackerServer=trackerClient.getConnection();

        StorageClient storageClient=new StorageClient(trackerServer,null);
        String orginalFilename="D:/照片/20161103154535.jpg";
        String[] upload_file = storageClient.upload_file(orginalFilename, "jpg", null);
        for (int i = 0; i < upload_file.length; i++) {
            String s = upload_file[i];
            System.out.println("s = " + s);
        }
    }
}
