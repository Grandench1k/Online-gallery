package ru.online.gallery;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.online.gallery.entity.User;
import ru.online.gallery.repository.UserRepository;
import ru.online.gallery.security.ApplicationConfiguration;
import ru.online.gallery.security.JwtService;
import ru.online.gallery.security.impl.JwtServiceImpl;
import ru.online.gallery.entity.Image;
import ru.online.gallery.entity.Role;
import ru.online.gallery.repository.ImageRepository;
import ru.online.gallery.s3.S3service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestAsyncConfiguration.class)
public class TestImageController {
    @Autowired
    private S3service s3service;
    @Value("${aws.s3.buckets.main-bucket}")
    private String bucketName;
    @Autowired
    private JwtService jwtService = new JwtServiceImpl();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ApplicationConfiguration applicationConfiguration;
    @Autowired
    private  ImageRepository imageRepository;
    private String accessToken;

    @Autowired
    private MockMvc mockMvc;

    private final String userId = new ObjectId().toString();

    private final String imageId = "id";


    static String fileName = "src/test/resources/static/image.jpg";

    static String absolutePath;

    @BeforeEach
    void setUp() throws IOException {
        String encodedPassword = applicationConfiguration.passwordEncoder().encode("example");
        User user = User.builder().username("firstname").email("exapmle@mail.com").role(Role.USER).id(userId).password(encodedPassword).build();
        user.setEnabled(true);
        byte[] file = Files.readAllBytes(Path.of(absolutePath));
        s3service.putObject(bucketName, "images/" + userId + "/" + fileName, file);
        userRepository.save(user);
        accessToken = jwtService.generateAccessToken(user);
        imageRepository.save(Image.builder().userId(userId).name("image").uri(fileName).id(imageId).build());
    }

    @AfterEach
    void tearDown() {
        imageRepository.deleteById(imageId);
        userRepository.deleteById(userId);
        s3service.deleteObject(bucketName, "image/" + userId + "/" + fileName);
    }

    @BeforeAll
    static void createImage() throws IOException {
        File image = new File(fileName);
        image.createNewFile();
        absolutePath = image.getAbsolutePath();
        System.out.println("created new file in " + absolutePath);
    }
    @AfterAll
    static void deleteImage() throws IOException {
        Files.delete(Path.of(absolutePath));
        System.out.println("deleted file in" + absolutePath);
    }

    @Test
    public void getAllImages() throws Exception {
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/image")
                .header("Authorization", "Bearer " + accessToken));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    public void getImageById() throws Exception {
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/image/" + imageId)
                .header("Authorization", "Bearer " + accessToken));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    public void saveImage() throws Exception {
        //Given
        MockMultipartFile image = new MockMultipartFile("image", "", "application/json", "{\"name\": \"new Image\"}".getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", fileName, "multipart/form-data", Files.readAllBytes(Path.of(absolutePath)));
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/image/save")
                .file(image)
                .file(imageFile)
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    public void updateImageById() throws Exception {
        String body = "{ \"name\" : " + "\"new Image\"}";
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v1/image/" + imageId + "/update").header("Authorization", "Bearer " + accessToken)
                .content(body)
                .contentType("application/json"));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    public void deleteImageById() throws Exception {
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/image/" + imageId + "/delete").header("Authorization", "Bearer " + accessToken));
        //Then
        response.andExpect(status().isOk());
    }
}
