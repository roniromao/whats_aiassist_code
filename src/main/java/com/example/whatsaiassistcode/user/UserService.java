package com.example.whatsaiassistcode.user;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public UserResponse findById(Long id) {
        return UserResponse.fromEntity(getUserById(id));
    }

    public UserResponse create(UserRequest request) {
        var user = new User();
        BeanUtils.copyProperties(request, user);

        return UserResponse.fromEntity(userRepository.save(user));
    }

    public UserResponse update(Long id, UserRequest request) {
        var user = getUserById(id);
        BeanUtils.copyProperties(request, user, "id");

        return UserResponse.fromEntity(userRepository.save(user));
    }

    public void delete(Long id) {
        var user = getUserById(id);
        userRepository.delete(user);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
