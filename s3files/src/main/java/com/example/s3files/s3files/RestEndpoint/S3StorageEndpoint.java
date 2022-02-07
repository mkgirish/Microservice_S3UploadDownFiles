package com.example.s3files.s3files.RestEndpoint;

import com.example.s3files.s3files.model.ObjectName;
import com.example.s3files.s3files.services.AWSS3StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
public class S3StorageEndpoint {

    @Autowired
    AWSS3StorageService service;
    @Value("${upload.dir}")
    private String upload_dir;
    @Value("${download.dir}")
    private String download_dir;

    @GetMapping("/list/files")
    public ResponseEntity<List<ObjectName>> getListOfFiles() {
        return new ResponseEntity<>(service.getS3Objects(), HttpStatus.OK);
    }
    @RequestMapping(value = "/file/upload", headers = ("content-type=multipart/*"), method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
   // @RequestMapping(value = "/file/upload", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile( @RequestParam("filename") String filename,
            @PathVariable MultipartFile file) {
        return new ResponseEntity<>(service.s3UploadFile(filename, file), HttpStatus.OK);
    }

    @RequestMapping(value = "/download")
    public ResponseEntity<byte[]> downloadFile(@RequestParam("filename") String filename, HttpServletResponse response) {
        ByteArrayOutputStream downloadInputStream = service.s3DownloadFile(filename);

        return ResponseEntity.ok()
                .contentType(contentType(filename))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment")
                .body(downloadInputStream.toByteArray());
    }


    private MediaType contentType(String filename) {
        String[] fileArrSplit = filename.split("\\.");
        String fileExtension = fileArrSplit[fileArrSplit.length - 1];
        switch (fileExtension) {
            case "pdf":
                return MediaType.TEXT_PLAIN;
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
                return MediaType.IMAGE_JPEG;
            default:
                return MediaType.APPLICATION_OCTET_STREAM;
        }
    }


}
