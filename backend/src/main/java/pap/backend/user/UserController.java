package pap.backend.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pap.backend.order.Order;
import pap.backend.order.OrderService;


import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("api/v1/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMe() {
        return new ResponseEntity<User>(userService.getMe(), HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<User>> getUsers() {
        return new ResponseEntity<List<User>>(userService.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/get/{userId}")
    public ResponseEntity<User> getUser(@PathVariable("userId") Long userId) {
        return new ResponseEntity<User>(userService.getUser(userId), HttpStatus.OK);
    }

    @PostMapping("/add")
    public ResponseEntity<String> addNewUser(@RequestBody User user) {
        try {
            userService.addNewUser(user);
            return new ResponseEntity<String>("User added successfully", HttpStatus.OK);
        } catch (IllegalStateException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error adding user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable("userId") Long userId) {
        try {
            userService.deleteUser(userId);
            return new ResponseEntity<String>("User deleted successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<String>("Error deleting user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

//    @PutMapping("/update/{userId}")
//    public ResponseEntity<String> updateUser(
//            @PathVariable("userId") Long userId,
//            @RequestParam(required = false) String email,
//            @RequestParam(required = false) String password,
//            @RequestParam(required = false) String role,
//            @RequestParam(required = false) String firstName,
//            @RequestParam(required = false) String lastName) {
//
//        try {
//            userService.updateUser(userId, email, password, role, firstName, lastName);
//            return new ResponseEntity<String>("User updated successfully", HttpStatus.OK);
//        } catch (NoSuchElementException e) {
//            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        } catch (IllegalStateException e) {
//            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
//        } catch (Exception e) {
//            return new ResponseEntity<String>("Error updating user", HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
