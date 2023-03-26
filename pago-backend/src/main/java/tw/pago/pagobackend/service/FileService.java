package tw.pago.pagobackend.service;

import java.io.IOException;
import java.net.URL;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import tw.pago.pagobackend.dto.CreateFileRequestDto;
import tw.pago.pagobackend.model.File;

public interface FileService {
    URL getFileUrlByObjectIdnType(String objectId, String objectType);
    URL uploadFile(MultipartFile file, CreateFileRequestDto createFileRequestDto);
    void deleteFileByObjectIdnType(String objectId, String objectType);
}
