package kz.userservice.middle.service;

import kz.userservice.middle.dto.UserDto;
import kz.userservice.middle.mapper.UserMapper;
import kz.userservice.middle.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDto> getUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    public UserDto getUser(Long id) {
        return userMapper.toDto(userRepository.findById(id).orElse(null));
    }

    public UserDto addUser(UserDto UserDto) {
        return userMapper.toDto(userRepository.save(userMapper.toEntity(UserDto)));
    }

    public UserDto updateUser(UserDto UserDto) {
        return userMapper.toDto(userRepository.save(userMapper.toEntity(UserDto)));
    }

    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(userRepository::delete);
    }
}
