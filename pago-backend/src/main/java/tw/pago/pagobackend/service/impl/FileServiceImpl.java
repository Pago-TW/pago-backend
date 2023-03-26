package tw.pago.pagobackend.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import tw.pago.pagobackend.dao.FileDao;
import tw.pago.pagobackend.dto.CreateFileRequestDto;
import tw.pago.pagobackend.model.File;
import tw.pago.pagobackend.service.FileService;
import tw.pago.pagobackend.util.UuidGenerator;

@Component
public class FileServiceImpl implements FileService{
    
    @Autowired
    private FileDao fileDao;

    @Autowired
    private UuidGenerator uuidGenerator;

    @Override
    public URL uploadFile(MultipartFile file, CreateFileRequestDto createFileRequestDto) {
        //set file uuid
        String fileUuid = uuidGenerator.getUuid();
        createFileRequestDto.setFileId(fileUuid);
        return fileDao.uploadFile(file, createFileRequestDto);
    }

    @Override
    public URL getFileUrlByObjectIdnType(String objectId, String objectType) {
        return fileDao.getFileUrlByObjectIdnType(objectId, objectType);
    }

    @Override
    public void deleteFileByObjectIdnType(String objectId, String objectType) {
        fileDao.deleteFileByObjectIdnType(objectId, objectType);
    }

}
