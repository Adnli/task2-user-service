package kz.userservice.middle.mapper;

import kz.userservice.middle.dto.UserProfilePhotoDto;
import kz.userservice.middle.model.UserProfilePhoto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserProfilePhotoMapper {

    UserProfilePhotoDto toDto(UserProfilePhoto userProfilePhoto);

    UserProfilePhoto toEntity(UserProfilePhotoDto userProfilePhotoDto);
}
