package pap.backend.user;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {this.userRepository = userRepository;}


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
    public void updateUsername(String newUsername) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // The principal (email/username)
        // Znalezienie użytkownika po ID
        User user = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Walidacja nowej nazwy użytkownika
        if (newUsername == null || newUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (newUsername.length() > 50) {
            throw new IllegalArgumentException("Username cannot exceed 50 characters");
        }

        // Aktualizacja nazwy użytkownika
        user.setUsername(newUsername);

        // Zapisanie zmian w bazie danych
        userRepository.save(user);
    }

//    @Transactional
//    public void updateUser(Long userId, String email, String password, UserRole role,
//                           String firstName, String lastName) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new NoSuchElementException(
//                        "user with id " + userId + " does not exist"
//                ));
//
//        if (email != null && !email.isEmpty() && !user.getEmail().equals(email)) {
//            user.setEmail(email);
//        }
//
//        if (password != null && !password.isEmpty() && !user.getPassword().equals(password)) {
//            user.setPassword(password);
//        }
//
//        if (role != null && (role.equals("ADMIN") || role.equals("CUSTOMER")) && !user.getRole().equals(role)) {
//            user.setRole(role);
//        }
//
//    }
}
