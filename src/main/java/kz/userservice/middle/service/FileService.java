package kz.userservice.middle.service;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import kz.userservice.middle.dto.UserDto;
import kz.userservice.middle.dto.UserProfilePhotoDto;
import kz.userservice.middle.mapper.UserProfilePhotoMapper;
import kz.userservice.middle.model.UserProfilePhoto;
import kz.userservice.middle.repository.UserProfilePhotoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FileService {
    private final UserProfilePhotoRepository userProfilePhotoRepository;
    private final UserProfilePhotoMapper userProfilePhotoMapper;
    private final UserService userService;
    private final MinioClient minioClient;
    @Value("${minio.bucket}")
    private String bucket;

    public String uploadFile(MultipartFile file, Long userId) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(file.getOriginalFilename())
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            UserProfilePhotoDto photo = UserProfilePhotoDto.builder()
                    .url(file.getOriginalFilename())
                    .name(new String(Base64.encodeBase64(Objects.requireNonNull(file.getOriginalFilename()).getBytes()), StandardCharsets.UTF_8))
                    .build();

            UserProfilePhoto userProfilePhoto = userProfilePhotoMapper.toEntity(photo);
            ;

            UserDto user = userService.getUser(userId);
            user.setPhotoId(userProfilePhotoRepository.save(userProfilePhoto).getId());
            userService.updateUser(user);
            return "file uploaded successfully";
        } catch (Exception e) {
            e.printStackTrace();
            return "error uploading file";
        }
    }

    public ByteArrayResource downloadFile(String fileName) {
        try {
            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucket)
                    .object(fileName)
                    .build();
            InputStream inputStream = minioClient.getObject(getObjectArgs);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            inputStream.close();
            return new ByteArrayResource(bytes);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
