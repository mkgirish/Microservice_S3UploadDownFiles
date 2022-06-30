package com.example.s3files.s3files.RestEndpoint;

import com.example.s3files.s3files.model.ObjectName;
import com.example.s3files.s3files.services.AWSS3StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
public class S3StorageEndpoint {

    @Autowired
    AWSS3StorageService service;
   

    @GetMapping("/list/files")
    public ResponseEntity<List<ObjectName>> getListOfFiles() {
        return new ResponseEntity<>(service.getS3Objects(), HttpStatus.OK);
    }
    
    @RequestMapping(value = "/user-profile/images", headers = ("content-type=multipart/*"), method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadFile( @RequestParam("filename") String filename,
            @PathVariable MultipartFile file) {
    	
    	if(contentType(filename) !=null) {
			
			 try { 
				 long megabytes= getFileSize(file,filename); 
				 if (megabytes >5000) {
					 return new ResponseEntity<>("Unsupported File Size", HttpStatus.UNSUPPORTED_MEDIA_TYPE); 
				 }
			 } catch
			 		(IllegalStateException | IOException e) { // TODO Auto-generated catch block
				 	e.printStackTrace(); 
			 }
			 
    		return new ResponseEntity<>(service.s3UploadFile(filename, file), HttpStatus.OK);
    	} else {
    		return new ResponseEntity<>("Unsupported Media type", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    	}
        
    }

    private MediaType contentType(String filename) {
        String[] fileArrSplit = filename.split("\\.");
        String fileExtension = fileArrSplit[fileArrSplit.length - 1];
        switch (fileExtension) {
            case "gif":
                return MediaType.IMAGE_GIF;
            case "tiff":
                return MediaType.valueOf("image/tiff");
            case "bmp":
                return MediaType.valueOf("image/bmp");
            case "png":
                return MediaType.IMAGE_PNG;
            case "jpg":
                return MediaType.IMAGE_JPEG;
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            
        }
		return null;
    }
    
    public long getFileSize(MultipartFile file,String filename) throws IllegalStateException, IOException {
   	
    	long KB = file.getSize()/1024;
        return KB;
    }


}
