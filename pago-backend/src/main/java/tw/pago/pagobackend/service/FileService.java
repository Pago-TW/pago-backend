package tw.pago.pagobackend.service;

import java.net.URL;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import tw.pago.pagobackend.dto.CreateFileRequestDto;

public interface FileService {
    List<URL> getFileUrlsByObjectIdnType(String objectId, String objectType);
    List<URL> uploadFile(List<MultipartFile> files, CreateFileRequestDto createFileRequestDto);
    void deleteFilesByObjectIdnType(String objectId, String objectType);
}
