package pap.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;


    @Autowired
    public UserService(UserRepository userRepository) {this.userRepository = userRepository;}


    public List<User> getUsers(){ return userRepository.findAll();}

    public User getUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "user with id " + userId + " does not exist"
                ));
    }

    public void addNewUser(User user) {
        Optional<User> userOptional = userRepository.findUserByEmail(user.getEmail());
        if (userOptional.isPresent()) {
            throw new IllegalStateException("email taken");
        }
        if(!user.getRole().equals("ADMIN") && !user.getRole().equals("CUSTOMER")){
            throw new IllegalStateException("role must be either ADMIN or CUSTOMER");
        }
        userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        boolean exists = userRepository.existsById(userId);
        if (!exists) {
            throw new IllegalStateException("user with id " + userId + " does not exist");
        }
        userRepository.deleteById(userId);
    }

    @Transactional
    public void updateUser(Long userId, String email, String password, String role,
                           String firstName, String lastName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException(
                        "user with id " + userId + " does not exist"
                ));

        if (email != null && !email.isEmpty() && !user.getEmail().equals(email)) {
            user.setEmail(email);
        }

        if (password != null && !password.isEmpty() && !user.getPassword().equals(password)) {
            user.setPassword(password);
        }

        if ((role.equals("ADMIN") || role.equals("CUSTOMER")) && !user.getRole().equals(role)) {
            user.setRole(role);
        }

        if (firstName != null && !firstName.isEmpty() && !user.getFirstName().equals(firstName)) {
            user.setFirstName(firstName);
        }

        if (lastName != null && !lastName.isEmpty() && !user.getLastName().equals(lastName)) {
            user.setLastName(lastName);
        }

    }
}
