package pap.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public List<User> getUsers(){ return userRepository.findAll();}

    public User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException(
                        "user with id " + userId + " does not exist"
                ));
    }

    public void addNewUser(User user) {
        Optional<User> userOptional = userRepository.findUserByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            throw new IllegalStateException("email taken");
        }

        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new NoSuchElementException("user with id " + userId + " does not exist");
        }
        userRepository.deleteById(userId);
    }

    public User getMe() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // The principal (email/username)

        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void updateUser(Long userId, User updatedUser) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));


        if (updatedUser.getEmail() != null && !updatedUser.getEmail().equals(existingUser.getEmail())) {
            existingUser.setEmail(updatedUser.getEmail());
        } else if (updatedUser.getEmail() == null) {
            throw new IllegalStateException("Email cannot be null");
        }

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().equals(existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        } else if (updatedUser.getPassword() == null) {
            throw new IllegalStateException("Password cannot be null");
        }

        if (updatedUser.getRole() != null && (updatedUser.getRole() == UserRole.ADMIN || updatedUser.getRole() == UserRole.USER)) {
            existingUser.setRole(updatedUser.getRole());
        } else if (updatedUser.getRole() == null) {
            throw new IllegalStateException("Role cannot be null");
        }

        if (updatedUser.getUsername() != null && !updatedUser.getUsername().equals(existingUser.getUsername())) {
            existingUser.setUsername(updatedUser.getUsername());
        } else if (updatedUser.getUsername() == null) {
            throw new IllegalStateException("Username cannot be null");
        }

        userRepository.save(existingUser);
    }

}
