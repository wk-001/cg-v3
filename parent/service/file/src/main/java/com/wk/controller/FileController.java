package com.wk.controller;

import com.wk.file.FastDFSFile;
import com.wk.utils.FastDFSUtil;
import entity.Result;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("file")
@CrossOrigin
public class FileController {

    /**
     * 文件上传
     */
    @PostMapping("upload")
    public Result upload(@RequestParam("file")MultipartFile file) throws Exception {
        //封装文件信息
        FastDFSFile fastDFSFile = new FastDFSFile(
                file.getOriginalFilename()     //文件名
                ,file.getBytes()                //文件字节数组
                ,StringUtils.getFilenameExtension(file.getOriginalFilename())   //文件扩展名
                );

        //调用工具类将文件传入到FastDFS中
        String[] fileUpload = FastDFSUtil.upload(fastDFSFile);

        /**
         * 拼接访问地址http://192.168.0.167:8080/group1/M00/00/00/wKjThF1aW9CAOUJGAAClQrJOYvs424.jpg
         * 用户访问FastDFS的文件需要先经过Nginx，Nginx的访问端口默认是8080
         */
        String url = FastDFSUtil.getTrackerInfo()+"/"+fileUpload[0]+"/"+fileUpload[1];
        return new Result(url);
    }

}
