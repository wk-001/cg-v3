package com.wk.utils;

import com.wk.file.FastDFSFile;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * FastDFS文件管理，实现文件上传、下载、删除、信息获取以及tracker和storage信息的获取
 */
public class FastDFSUtil {

    /**
     * 通过IP和端口号访问Tracker
     * 加载Tracker连接信息
     */
    static {
        //加载配置文件的信息
        try {
            //查找classpath下FastDFS配置文件的路径
            String fileName = new ClassPathResource("fdfs_client.conf").getPath();
            //初始化Tracker连接信息
            ClientGlobal.init(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取StorageClient
    private static StorageClient getStorageClient() {
        try {
            //创建一个Tracker访问的客户端对象TrackClient
            TrackerClient trackerClient = new TrackerClient();

            //通过TrackClient访问TrackServer服务，获取连接信息
            TrackerServer trackerServer = trackerClient.getConnection();

            //通过TrackServer的连接信息获取Storage的连接信息，创建StorageClient对象存储Storage的连接信息
            return new StorageClient(trackerServer, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 文件上传
     * @param file
     * @return
     */
    public static String[] upload(FastDFSFile file) throws Exception {
        StorageClient storageClient = getStorageClient();
        //附加信息
        NameValuePair[] nameValuePairs = new NameValuePair[]{new NameValuePair("作者",file.getAuthor())};
        /**
         * 通过StorageClient访问Storage，实现文件上传，并获取文件上传后的信息
         * 参数：1、上传文件的字节数组；2、上传文件的后缀；3、上传文件的附加信息，如作者等
         */
        String[] uploadResult = storageClient.upload_file(file.getContent(), file.getExt(), nameValuePairs);
        /**
         * uploadResult[0]：文件上传所存储Storage组的名字，如groupl
         * uploadResult[1]：文件存储到Storage上的文件名字，如M00/02/44/itheima.jpg
         */
        return uploadResult;
    }

    /**
     * 文件下载
     * @param groupName ：文件所在的组名
     * @param remoteFileName ：文件的存储路径
     */
    public static InputStream downloadFile(String groupName, String remoteFileName) throws Exception {
        StorageClient storageClient = getStorageClient();
        byte[] fileBuffer = storageClient.download_file(groupName, remoteFileName);
        return new ByteArrayInputStream(fileBuffer);
    }

    /**
     * 获取Storage中存储文件的IP、大小、创建时间信息
     * @param groupName ：文件所在的组名
     * @param remoteFileName ：文件的存储路径
     */
    public static FileInfo getFileInfo(String groupName, String remoteFileName) throws Exception {
        return getStorageClient().get_file_info(groupName,remoteFileName);
    }

    /**
     * 删除文件
     * @param groupName ：文件所在的组名
     * @param remoteFileName ：文件的存储路径
     */
    public static void deleteFile(String groupName, String remoteFileName) throws Exception {
        StorageClient storageClient = getStorageClient();
        storageClient.delete_file(groupName, remoteFileName);
    }

    /**
     * 获取Storage信息
     * @return
     */
    public static StorageServer getStorage() throws IOException {
        //创建一个Tracker访问的客户端对象TrackerClient
        TrackerClient trackerClient = new TrackerClient();

        //通过TrackerClient访问TrackerServer服务，获取连接信息
        TrackerServer trackerServer = trackerClient.getConnection();

        //获取Storage信息
        return trackerClient.getStoreStorage(trackerServer);
    }

    /**
     * 获取Storage的IP和端口信息
     * @return
     */
    public static ServerInfo[] getStorageIPPorts(String groupName, String remoteFileName) throws IOException {
        //创建一个Tracker访问的客户端对象TrackerClient
        TrackerClient trackerClient = new TrackerClient();

        //通过TrackerClient访问TrackerServer服务，获取连接信息
        TrackerServer trackerServer = trackerClient.getConnection();

        //获取Storage的IP和端口信息
        return trackerClient.getFetchStorages(trackerServer,groupName,remoteFileName);
    }


    //获取配置文件中设置的Tracker信息
    public static String getTrackerInfo() throws IOException {
        //创建一个Tracker访问的客户端对象TrackerClient
        TrackerClient trackerClient = new TrackerClient();

        //通过TrackerClient访问TrackerServer服务，获取连接信息
        TrackerServer trackerServer = trackerClient.getConnection();

        //Tracker的IP和HTTP端口
        String ip = trackerServer.getInetSocketAddress().getHostString();
        //获取配置文件中的配置信息
        int port = ClientGlobal.getG_tracker_http_port();
        String url = "http://"+ip+":"+port;
        return url;
    }

}
