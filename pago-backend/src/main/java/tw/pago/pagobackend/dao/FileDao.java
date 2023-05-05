package tw.pago.pagobackend.dao;

import java.net.URL;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import tw.pago.pagobackend.dto.CreateFileRequestDto;

public interface FileDao {
    List<URL> uploadFile(List<MultipartFile> files, CreateFileRequestDto createFileRequestDto);
    List<URL> getFileUrlsByObjectIdnType(String objectId, String objectType);
    void deleteFilesByObjectIdnType(String objectId, String objectType);
}
