package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tw.pago.pagobackend.model.File;

public class FileRowMapper implements RowMapper<File>{
    @Override
    public File mapRow(ResultSet resultSet, int mapRow) throws SQLException {
        File uploadFile = new File();
        uploadFile.setFileId(resultSet.getString("file_id"));
        uploadFile.setFileName(resultSet.getString("file_name"));
        uploadFile.setFileExtension(resultSet.getString("file_extension"));
        uploadFile.setFilePath(resultSet.getString("file_path"));
        uploadFile.setFileSize(resultSet.getDouble("file_size"));
        uploadFile.setFileCreator(resultSet.getString("file_creator"));
        uploadFile.setCreateDate(resultSet.getDate("create_date"));
        uploadFile.setUpdateDate(resultSet.getDate("update_date"));
        uploadFile.setObjectId(resultSet.getString("object_id"));
        uploadFile.setObjectType(resultSet.getString("object_type"));

        return uploadFile;
    }
}
