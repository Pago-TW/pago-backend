package tw.pago.pagobackend.dao;

import java.net.URL;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import tw.pago.pagobackend.dto.CreateFileRequestDto;
import tw.pago.pagobackend.model.File;

public interface FileDao {
    URL uploadFile(MultipartFile file, CreateFileRequestDto createFileRequestDto);
    URL getFileUrlByObjectIdnType(String objectId, String objectType);
    void deleteFileByObjectIdnType(String objectId, String objectType);
}
