package com.shoppinglist.shoppinglist.user;

import com.shoppinglist.shoppinglist.security.JwtUtil;
import com.shoppinglist.shoppinglist.user.dto.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    private JwtUtil jwtUtil;

    public UserDto createUser(UserEntity user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists.");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("Username already exists.");
        }
        user.setPassword(hashPassword(user.getPassword()));
        return mapUserToDto(userRepository.save(user));
    }

    public List<UserDto> getAllUsers() {
        List<UserEntity> userEntities = userRepository.findAll();

        List<UserDto> userDtos = new ArrayList<>();
        for (UserEntity userEntity : userEntities) {
            UserDto userDto = mapUserToDto(userEntity);
            userDtos.add(userDto);
        }

        return userDtos;
    }

    public UserDto mapUserToDto(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .build();
    }

    public UserDto getUserById(UUID id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        return mapUserToDto(userEntity);
    }

    public UserEntity getRawUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found.");
        }
        userRepository.deleteById(id);
    }

    public UserEntity updateUser(UUID id, UserEntity updatedUser) {
        UserEntity existingUser = getRawUserById(id);
        existingUser.setUsername(updatedUser.getUsername());
        existingUser.setEmail(updatedUser.getEmail());
        if (updatedUser.getPassword() != null) {
            existingUser.setPassword(hashPassword(updatedUser.getPassword()));
        }
        return userRepository.save(existingUser);
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
    }

    private String hashPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    public UserDto getUserByToken(String token) {
        String username = jwtUtil.getUsernameFromToken(token);
        UserEntity user = findByUsername(username);

        return mapUserToDto(user);
    }
}
