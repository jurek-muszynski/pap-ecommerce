package pap.backend.controllerTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import pap.backend.config.JwtService;
import pap.backend.user.*;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("testuser@example.com");
        testUser.setRole(UserRole.USER);
    }


    @BeforeEach
    void setUpAuthentication() {;
        User mockUserDetails = new User();
        mockUserDetails.setUsername("admin@example.com");
        mockUserDetails.setEmail("admin@example.com");
        mockUserDetails.setPassword("password");
        mockUserDetails.setRole(UserRole.ADMIN);


        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUserDetails, null, mockUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    @Test
    @WithMockUser(username = "testuser@example.com", roles = "USER")
    void getMe_ShouldReturnAuthenticatedUser() throws Exception {
        Mockito.when(userService.getMe()).thenReturn(testUser);

        mockMvc.perform(get("/api/v1/user/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testUser"))
                .andExpect(jsonPath("$.username").value("testuser@example.com"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void getUsers_ShouldReturnAllUsers() throws Exception {
        // Mock behavior of UserService
        List<User> users = Arrays.asList(testUser, new User("admin", "admin@example.com", "password", UserRole.ADMIN));
        Mockito.when(userService.getUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].email").value("testuser@example.com"))
                .andExpect(jsonPath("$[1].email").value("admin@example.com"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void addNewUser_ShouldAddUser() throws Exception {
        User newUser = new User();
        newUser.setId(2L);
        newUser.setUsername("newuser");
        newUser.setEmail("newuser@example.com");
        newUser.setPassword("password");
        newUser.setRole(UserRole.USER);

        Mockito.doNothing().when(userService).addNewUser(Mockito.any(User.class));

        mockMvc.perform(post("/api/v1/user/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(content().string("User added successfully"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void deleteUser_ShouldDeleteUser() throws Exception {
        Mockito.doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/v1/user/delete/{userId}", 1L)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void updateUser_ShouldUpdateUser() throws Exception {
        User updatedUser = new User("updatedUser", "updateduser@example.com", "newpassword", UserRole.ADMIN);

        Mockito.doNothing().when(userService).updateUser(1L, updatedUser);

        mockMvc.perform(put("/api/v1/user/update/{userId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully"));
    }
}
