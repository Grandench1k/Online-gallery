package java.com.online.gallery;

import com.online.gallery.repository.UserRepository;
import com.online.gallery.repository.VideoRepository;
import com.online.gallery.security.ApplicationConfiguration;
import com.online.gallery.security.JwtService;
import com.online.gallery.storage.s3.S3service;
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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ContextConfiguration(classes = TestAsyncConfiguration.class)
public class TestVideoController {
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
    @Autowired
    private VideoRepository videoRepository;
    private String jwtToken;

    @Autowired
    private MockMvc mockMvc;

    private final String userId = new ObjectId().toString();

    private static final String fileName = "video.mp4";

    private static String absolutePath;

    private final String videoId = "id";

    @BeforeEach
    void setUp() throws IOException {
        String encodedPassword = applicationConfiguration.passwordEncoder().encode("example");
        User user = User.builder().username("firstname").email("exapmle@mail.com").role(Role.USER).id(userId).password(encodedPassword).build();
        user.setEnabled(true);
        byte[] file = Files.readAllBytes(Path.of(absolutePath));
        s3service.putObject(bucketName, "videos/" + userId + "/" + fileName, file);
        userRepository.save(user);
        jwtToken = jwtService.generateAccessToken(user);
        videoRepository.save(Video.builder().userId(userId).name("video").uri(fileName).id(videoId).build());
    }

    @AfterEach
    void tearDown() {
        videoRepository.deleteById(videoId);
        userRepository.deleteById(userId);
        s3service.deleteObject(bucketName, "videos/" + userId + "/" + fileName);
    }

    @BeforeAll
    static void createVideo() throws IOException {
        File video = new File(fileName);
        video.createNewFile();
        absolutePath = video.getAbsolutePath();
        System.out.println("created new file in " + absolutePath);
    }
    @AfterAll
    static void deleteVideo() throws IOException {
        Files.delete(Path.of(absolutePath));
        System.out.println("deleted file in" + absolutePath);
    }

    @Test
    public void getAllVideos() throws Exception {
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/video")
                .header("Authorization", "Bearer " + jwtToken));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    public void getVideoById() throws Exception {
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/video/" + videoId)
                .header("Authorization", "Bearer " + jwtToken));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    public void saveVideo() throws Exception {
        //Given
        MockMultipartFile image = new MockMultipartFile("video", "", "application/json", "{\"name\": \"new Image\"}".getBytes());
        MockMultipartFile imageFile = new MockMultipartFile("videoFile", "video.mp4", "multipart/form-data", Files.readAllBytes(Path.of(absolutePath)));
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/video/save")
                .file(image)
                .file(imageFile)
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.MULTIPART_FORM_DATA));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    public void updateVideoById() throws Exception {
        String body = "{ \"name\" : " + "\"new Video\"}";
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .patch("/api/v1/video/" + videoId + "/update").header("Authorization", "Bearer " + jwtToken)
                .content(body)
                .contentType("application/json"));
        //Then
        response.andExpect(status().isOk());
    }

    @Test
    public void deleteVideoById() throws Exception {
        //When
        ResultActions response = mockMvc.perform(MockMvcRequestBuilders
                .delete("/api/v1/video/" + videoId + "/delete").header("Authorization", "Bearer " + jwtToken));
        //Then
        response.andExpect(status().isOk());
    }
}
