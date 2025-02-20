package kz.userservice.middle.controller;

import kz.userservice.middle.dto.UserDto;
import kz.userservice.middle.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping(value = "/signIn")
    public ResponseEntity<Map<String, Object>> signIn(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.signIn(userDto), HttpStatus.OK);
    }

    @PostMapping(value = "/signUp")
    public ResponseEntity<UserDto> signUp(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.signUp(userDto), HttpStatus.OK);
    }

    @GetMapping(value = "/getUsers")
    public ResponseEntity<List<UserDto>> getUsers() {
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable(name = "id") Long id) {
        return new ResponseEntity<>(userService.getUser(id), HttpStatus.OK);
    }

    @PostMapping("/updateUser")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserDto userDTO) {
        return new ResponseEntity<>(userService.updateUser(userDTO), HttpStatus.OK);
    }

    @PostMapping("/addUser")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDTO) {
        return new ResponseEntity<>(userService.addUser(userDTO), HttpStatus.OK);
    }

    @PostMapping("/deleteUser")
    public void deleteUser(@RequestBody UserDto userDto) {
        userService.deleteUser(userDto.getId());
    }
}