package pap.frontend.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import pap.frontend.models.CartItem;
import pap.frontend.models.Product;
import pap.frontend.services.AuthService;
import pap.frontend.services.CartService;
import pap.frontend.services.ProductService;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class SummaryController extends AuthenticatedController {
    @FXML
    private VBox summaryPane;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private TextField deliveryAddressField;

    @FXML
    private TextField emailField;

    @FXML
    private Button placeOrderButton;

    private final CartService cartService = new CartService();
    private final ProductService productService = new ProductService();

    public SummaryController() {
        super(AuthService.getInstance());
    }

    private ScreenController screenController;

    @Override
    public void setScreenController(ScreenController screenController) {
        this.screenController = screenController;
    }

    @FXML
    public void initialize() {

        refreshData();

        // Disable Place Order button by default
        placeOrderButton.setDisable(true);

        // Add listeners to enable the button only when fields are filled
        deliveryAddressField.textProperty().addListener((observable, oldValue, newValue) -> checkForm());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> checkForm());
    }

    public void refreshData() {
        checkAuthentication();

        if (authService.isAuthenticated()){
            loadSummary();
        }

    }

    private void loadSummary() {
        try {
            Long userId = authService.getCurrentUserId();
            List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);
            updateSummaryView(cartItems);
        } catch (Exception e) {
            showAlert("Error", "Failed to load order summary: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void updateSummaryView(List<CartItem> cartItems) {
        summaryPane.getChildren().clear();
        double totalPrice = 0.0;

        for (CartItem cartItem : cartItems) {
            VBox itemBox = new VBox(10);

            Product product = productService.getProductById(cartItem.getProductId());

            Label productName = new Label("Product: " + product.getName());
            Label productPrice = new Label("Price: $" + product.getPrice());
            totalPrice += product.getPrice();

            itemBox.getChildren().addAll(productName, productPrice);
            summaryPane.getChildren().add(itemBox);
        }

        totalPriceLabel.setText("Total Price: $" + String.format("%.2f", totalPrice));
    }

    private void checkForm() {
        boolean isFormValid = !deliveryAddressField.getText().isEmpty() && !emailField.getText().isEmpty();
        placeOrderButton.setDisable(!isFormValid);
    }

    @FXML
    private void placeOrder() {
        String deliveryAddress = deliveryAddressField.getText();
        String email = emailField.getText();

        try {
            sendOrderConfirmationEmail(email, deliveryAddress);
            showAlert("Success", "Order placed successfully! Confirmation email sent.", Alert.AlertType.INFORMATION);

            if (screenController != null) {
                screenController.activate("userView");
            }
        } catch (Exception e) {
            showAlert("Error", "Failed to send confirmation email: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void sendOrderConfirmationEmail(String email, String deliveryAddress) throws MessagingException, IOException {
        // Odczytanie danych z pliku application.properties
        Properties properties = loadProperties();

        String from = properties.getProperty("mail.username");
        String password = properties.getProperty("mail.password");

        String subject = "Order Confirmation";
        StringBuilder messageBody = new StringBuilder("Thank you for your order!\n\n");

        // Szczegóły zamówienia
        messageBody.append("Delivery Address: ").append(deliveryAddress).append("\n\n");
        messageBody.append("Order Details:\n");

        Long userId = authService.getCurrentUserId();
        List<CartItem> cartItems = cartService.getCartItemsByUserId(userId);
        double totalPrice = 0.0;

        for (CartItem cartItem : cartItems) {
            Product product = productService.getProductById(cartItem.getProductId());
            messageBody.append("- ").append(product.getName()).append(": $").append(product.getPrice()).append("\n");
            totalPrice += product.getPrice();
        }

        messageBody.append("\nTotal Price: $").append(String.format("%.2f", totalPrice));

        // Konfiguracja SMTP dla Gmaila
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");  // Serwer SMTP Gmaila
        props.put("mail.smtp.port", "465");  // Port 465 dla SSL
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true"); // Włączenie SSL
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // Zaufaj serwerowi Gmaila

        // Sesja
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password); // Autentykacja za pomocą adresu e-mail i hasła
            }
        });

        // Tworzenie wiadomości
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
        message.setSubject(subject);
        message.setText(messageBody.toString());

        // Wysyłanie wiadomości
        Transport.send(message);
    }

    private Properties loadProperties() throws IOException {
        Properties properties = new Properties();
        // Ładowanie pliku properties
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("application.properties");
        if (inputStream == null) {
            throw new IOException("Property file 'application.properties' not found in the classpath.");
        }
        properties.load(inputStream);
        return properties;
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
