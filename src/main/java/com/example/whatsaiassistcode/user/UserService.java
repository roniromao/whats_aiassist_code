package com.example.whatsaiassistcode.user;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserResponse> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserResponse::fromEntity);
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
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }
}
