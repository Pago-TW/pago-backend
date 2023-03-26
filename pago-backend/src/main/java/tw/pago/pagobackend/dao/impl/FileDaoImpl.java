package tw.pago.pagobackend.dao.impl;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;

import tw.pago.pagobackend.dao.FileDao;
import tw.pago.pagobackend.dto.CreateFileRequestDto;
import tw.pago.pagobackend.model.File;
import tw.pago.pagobackend.rowmapper.FileRowMapper;

@Component
public class FileDaoImpl implements FileDao {

    private static final String BUCKET_NAME = "pago-file-storage";

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private AmazonS3 amazonS3;

    @Override
    public URL uploadFile(MultipartFile file, CreateFileRequestDto createFileRequestDto) {
        try {
            // Upload file to S3
            String fileName = file.getOriginalFilename();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3.putObject(BUCKET_NAME, fileName, file.getInputStream(), metadata);

            URL resUrl = amazonS3.getUrl(BUCKET_NAME, fileName);

            // Store file data to database
            String sql = "INSERT INTO file (file_id, file_name, file_extension, file_path, file_creator, file_size, create_date,"
            + " update_date, object_id, object_type) VALUES (:fileId, :fileName, :fileExtension, :filePath, :fileCreator, "
            + ":fileSize, :createDate, :updateDate, :objectId, :objectType)";

            Map<String, Object> map = new HashMap<>();
            map.put("fileId", createFileRequestDto.getFileId());
            map.put("fileName", fileName);
            map.put("fileExtension", FilenameUtils.getExtension(fileName));
            map.put("filePath", resUrl.toString());
            map.put("fileCreator", createFileRequestDto.getFileCreator());
            map.put("fileSize", file.getSize());
            Date now = new Date();
            map.put("createDate", now);
            map.put("updateDate", now);
            map.put("objectId", createFileRequestDto.getObjectId());
            map.put("objectType", createFileRequestDto.getObjectType());

            namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

            return resUrl;
        } catch (AmazonS3Exception e) {
            // Log the error message and status code
            System.err.println("Amazon S3 error: " + e.getErrorCode());
            return null;
        } catch (Exception e) {
            // Log the error message and status code
            System.err.println("Unknown error: " + e.getMessage());
            return null;
        } 
    }

    @Override
    public URL getFileUrlByObjectIdnType(String objectId, String objectType) {
        String sql = "SELECT file_id, file_name, file_extension, file_path, file_creator, file_size, create_date, "
        + "update_date, object_id, object_type FROM file WHERE object_id = :objectId AND object_type = :objectType";
        Map<String, Object> map = new HashMap<>();
        map.put("objectId", objectId);
        map.put("objectType", objectType);

        File file = namedParameterJdbcTemplate.queryForObject(sql, new MapSqlParameterSource(map), new FileRowMapper());
        try {
            return new URL(file.getFilePath());
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void deleteFileByObjectIdnType(String objectId, String objectType) {
        // Get fileId
        String selectSql = "SELECT file_name FROM file WHERE object_id = :objectId AND object_type = :objectType";
        MapSqlParameterSource params = new MapSqlParameterSource()
            .addValue("objectId", objectId)
            .addValue("objectType", objectType);
        String fileName = namedParameterJdbcTemplate.queryForObject(selectSql, params, String.class);

        // Delete file from database
        String deleteSql = "DELETE FROM file WHERE object_id = :objectId AND object_type = :objectType";
        namedParameterJdbcTemplate.update(deleteSql, params);

        // Delete file from S3
        amazonS3.deleteObject(BUCKET_NAME, fileName);
    }
}

