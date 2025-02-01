package kz.userservice.middle.mapper;

import kz.userservice.middle.dto.UserDto;
import kz.userservice.middle.model.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);

    User toEntity(UserDto userDto);

    List<UserDto> toDtoList(List<User> users);

}