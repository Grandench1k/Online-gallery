package com.online.gallery;

import com.online.gallery.model.user.Role;
import com.online.gallery.model.user.User;
import com.online.gallery.repository.user.UserRepository;
import com.online.gallery.security.configuration.ApplicationConfiguration;
import com.online.gallery.security.service.JwtService;
import com.online.gallery.storage.s3.service.S3service;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TestUserController {
    @Autowired
    private S3service s3service;
    @Value("${aws.s3.buckets.main-bucket}")
    private String bucketName;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApplicationConfiguration applicationConfiguration;
    private String jwtToken;

    @Autowired
    private MockMvc mockMvc;

    private final String userId = new ObjectId().toString();

    static String fileName = "src/test/resources/static/profileImage.jpg";

    static String absolutePath;

    @BeforeEach
    void setUp() throws IOException {
        String encodedPassword = applicationConfiguration.passwordEncoder().encode("example");
        User user = User.builder().username("firstname").email("exapmle@mail.com").role(Role.USER).id(userId).profileImageId(fileName).password(encodedPassword).build();
        user.setEnabled(true);
        byte[] file = Files.readAllBytes(Path.of(absolutePath));
        s3service.putObject(bucketName, "profileImages/" + userId + "/" + fileName, file);
        userRepository.save(user);
        jwtToken = jwtService.generateAccessToken(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteById(userId);
        s3service.deleteObject(bucketName, "profileImages/" + userId + "/" + fileName);
    }

    @BeforeAll
    static void createProfileImage() throws IOException {
        File image = new File(fileName);
        image.createNewFile();
        absolutePath = image.getAbsolutePath();
        System.out.println("created new file in " + absolutePath);
    }

    @AfterAll
    static void deleteProfileImage() throws IOException {
        Files.delete(Path.of(absolutePath));
        System.out.println("deleted file in" + absolutePath);
    }

    @Test
    public void getProfileImage() throws Exception {
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/users/profileImage")
                .header("Authorization", "Bearer " + jwtToken));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    public void saveProfileImage() throws Exception {
        //Given
        MockMultipartFile imageFile = new MockMultipartFile("profileImageFile", "image.jpg", "multipart/form-data", Files.readAllBytes(Path.of(absolutePath)));
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/users/profileImage")
                .file(imageFile)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    public void updateProfileImage() throws Exception {
        //Given
        MockMultipartFile imageFile = new MockMultipartFile("profileImageFile", "image.jpg", "multipart/form-data", Files.readAllBytes(Path.of(absolutePath)));
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PATCH, "/api/v1/users/profileImage")
                .file(imageFile)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    public void updatePassword() throws Exception {
        //Given
        String passwords = "{\"old_password\": \"example\", \"new_password\": \"example1\"}";
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v1/users/password")
                .header("Authorization", "Bearer " + jwtToken)
                .content(passwords)
                .contentType("application/json"));
        //Then
        response.andExpect(status().isOk());
    }
}
