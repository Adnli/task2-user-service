package kz.userservice.middle.service;

import jakarta.ws.rs.core.Response;
import kz.userservice.middle.dto.UserDto;
import kz.userservice.middle.mapper.UserMapper;
import kz.userservice.middle.repository.UserRepository;
import kz.userservice.middle.utils.UserUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate;
    private final Keycloak keycloak;

    @Value("${keycloak.url}")
    private String url;
    @Value("${keycloak.realm}")
    private String realm;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;
    @Value("${keycloak.grand-type}")
    private String grandType;

    public Map<String, Object> signIn(UserDto userDto) {
        String token = url + "/realms/" + realm + "/protocol/openid-connect/token";
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        form.add("client_secret", clientSecret);
        form.add("grant_type", grandType);
        form.add("username", userDto.getLogin());
        form.add("password", userDto.getPassword());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/x-www-form-urlencoded");
        ResponseEntity<Map> response = restTemplate.postForEntity(token, new HttpEntity<>(form, httpHeaders), Map.class);
        Map<String, Object> responseBody = response.getBody();

        if (!response.getStatusCode().is2xxSuccessful() || responseBody == null) {
            log.error("Sign-in failed", responseBody);
            throw new RuntimeException("Sign-in failed");
        }
        return responseBody;
    }

    public UserDto signUp(UserDto userDto) {
        UserRepresentation userRepresentation = signUpUser(userDto);
        UserDto newUser = new UserDto();
        newUser.setLogin(userRepresentation.getUsername());
        newUser.setEmail(userRepresentation.getEmail());
        newUser.setFullName(userRepresentation.getFirstName());
        return newUser;
    }

    public boolean changePassword(String newPassword) {
        List<UserRepresentation> userRepresentations = keycloak.realm(realm)
                .users()
                .search(UserUtil.getCurrentUsername());
        if(userRepresentations.isEmpty()) {
            return false;
        }
        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(newPassword);
        credentialRepresentation.setTemporary(false);
        keycloak.realm(realm)
                .users()
                .get(userRepresentations.get(0).getId())
                .resetPassword(credentialRepresentation);
        return true;
    }

    public List<UserDto> getUsers() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    public UserDto getUser(Long id) {
        return userMapper.toDto(userRepository.findById(id).orElse(null));
    }

    public UserDto addUser(UserDto userDto) {
        return userMapper.toDto(userRepository.save(userMapper.toEntity(userDto)));
    }

    public UserDto updateUser(UserDto userDto) {
        return userMapper.toDto(userRepository.save(userMapper.toEntity(userDto)));
    }

    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(userRepository::delete);
    }

    private UserRepresentation signUpUser(UserDto userDto) {

        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(userDto.getLogin());
        userRepresentation.setEmail(userDto.getEmail());
        userRepresentation.setFirstName(userDto.getFullName());
        userRepresentation.setEnabled(true);
        userRepresentation.setEmailVerified(true);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(userDto.getPassword());
        credentialRepresentation.setTemporary(false);
        userRepresentation.setCredentials(List.of(credentialRepresentation));

        Response response = keycloak.realm(realm)
                .users()
                .create(userRepresentation);

        if (response.getStatus() != HttpStatus.CREATED.value()) {

            log.error("Sign-up failed", response);
            throw new RuntimeException("Sign-up failed");
        }
        List<UserRepresentation> users = keycloak.realm(realm).users().search(userDto.getLogin());
        return users.stream().findFirst().orElse(null);
    }
}
