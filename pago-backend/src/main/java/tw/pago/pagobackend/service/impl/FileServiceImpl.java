package tw.pago.pagobackend.service.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tw.pago.pagobackend.dao.FileDao;
import tw.pago.pagobackend.dto.CreateFileRequestDto;
import tw.pago.pagobackend.service.FileService;
import tw.pago.pagobackend.util.UuidGenerator;

@Component
public class FileServiceImpl implements FileService{
    
    @Autowired
    private FileDao fileDao;

    @Autowired
    private UuidGenerator uuidGenerator;

    @Transactional
    @Override
    public List<URL> uploadFile(List<MultipartFile> files, CreateFileRequestDto createFileRequestDto) {
        List<URL> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            // Generate a unique file ID for each file
            String fileUuid = uuidGenerator.getUuid();
            createFileRequestDto.setFileId(fileUuid);
    
            // Upload file and store file data to database
            urls.addAll(fileDao.uploadFile(Collections.singletonList(file), createFileRequestDto));
        }
        return urls;
    }    

    @Override
    public List<URL> getFileUrlsByObjectIdnType(String objectId, String objectType) {
        return fileDao.getFileUrlsByObjectIdnType(objectId, objectType);
    }

    @Override
    public void deleteFilesByObjectIdnType(String objectId, String objectType) {
        fileDao.deleteFilesByObjectIdnType(objectId, objectType);
    }

}
