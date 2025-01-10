package pap.backend.auth;

import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pap.backend.cart.Cart;
import pap.backend.cart.CartService;
import pap.backend.config.JwtService;
import pap.backend.mail.EmailService;
import pap.backend.user.User;
import pap.backend.user.UserRepository;
import pap.backend.user.UserRole;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CartService cartService;
    private final EmailService emailService;

    public AuthService(UserRepository repository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, CartService cartService, EmailService emailService) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.cartService = cartService;
        this.emailService = emailService;
    }

    public ResponseEntity<?> register(RegisterRequest request) {
        Optional<User> existingEmail = repository.findUserByEmail(request.getEmail());
        if (existingEmail.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with this email already exists");
        }

        Optional<User> existingUsername = repository.findUserByUsername(request.getUsername());
        if (existingUsername.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with this username already exists");
        }

        try {
            User user = new User(request.getUsername(), request.getEmail(), passwordEncoder.encode(request.getPassword()), UserRole.USER);
            repository.save(user);
            String token = jwtService.generateToken(user);
            var response = new AuthResponse(token);

          // <- CART CREATION -> //
            Cart cart = new Cart();
            cart.setUser(user);
            cartService.addNewCart(cart);
          // <- CART CREATION -> //

            sendRegistrationConfirmationEmail(user);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    public ResponseEntity<?> authenticate(AuthRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        Optional<User> user = repository.findUserByEmail(request.getEmail());
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User with this email already exists");
        }
        String token = jwtService.generateToken(user.get());
        var response = new AuthResponse(token);
        return ResponseEntity.ok(response);
    }

    private void sendRegistrationConfirmationEmail(User user) {
        String subject = "Welcome to Our Platform!";
        String htmlMessage = "<html>"
                + "<body style=\"font-family: Arial, sans-serif;\">"
                + "<div style=\"background-color: #f5f5f5; padding: 20px;\">"
                + "<h2 style=\"color: #333;\">Welcome, " + user.getName() + "!</h2>"
                + "<p style=\"font-size: 16px;\">Thank you for registering on our platform. We are excited to have you on board!</p>"
                + "<div style=\"background-color: #fff; padding: 20px; border-radius: 5px; box-shadow: ***REMOVED*** 10px rgba(0,0,0,0.1);\">"
                + "<h3 style=\"color: #333;\">Your Registration Details:</h3>"
                + "<p style=\"font-size: 16px;\"><strong>Username:</strong> " + user.getName() + "</p>"
                + "<p style=\"font-size: 16px;\"><strong>Email:</strong> " + user.getEmail() + "</p>"
                + "</div>"
                + "<p style=\"font-size: 14px; color: #888;\">Feel free to explore our platform and make the most out of our services.</p>"
                + "<p style=\"font-size: 14px; color: #888;\">If you have any questions or need assistance, please do not hesitate to reach out to our support team.</p>"
                + "<p style=\"font-size: 14px; color: #888;\">Thank you,</p>"
                + "<p style=\"font-size: 14px; color: #888;\"><strong>The Team</strong></p>"
                + "</div>"
                + "</body>"
                + "</html>";

        try {
            emailService.sendEmail(user.getEmail(), subject, htmlMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}