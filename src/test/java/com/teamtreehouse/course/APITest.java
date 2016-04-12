package com.teamtreehouse.course;

import com.google.gson.Gson;
import com.teamtreehouse.course.dao.Sql2oCourseDAO;
import com.teamtreehouse.course.dao.Sql2oReviewDAO;
import com.teamtreehouse.course.model.Course;
import com.teamtreehouse.course.model.Review;
import com.teamtreehouse.testing.APIClient;
import com.teamtreehouse.testing.APIResponse;
import org.junit.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by Fernando on 4/12/2016.
 */
public class APITest {

    private static final String PORT = "4568";
    private static final String TEST_DATASOURCE = "jdbc:h2:mem:testing";
    private Connection conn;
    private APIClient client;
    private Gson gson;
    private Sql2oCourseDAO courseDAO;
    private Sql2oReviewDAO reviewDAO;

    @BeforeClass
    public static void startServer() {
        String[] args = {PORT, TEST_DATASOURCE};
        API.main(args);
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
    }

    @Before
    public void setUp() throws Exception {
        Sql2o sql2o = new Sql2o(TEST_DATASOURCE + ";INIT=RUNSCRIPT from 'classpath:db/init.sql'", "", "");
        courseDAO = new Sql2oCourseDAO(sql2o);
        reviewDAO = new Sql2oReviewDAO(sql2o);
        conn = sql2o.open();
        client = new APIClient("http://localhost:" + PORT);
        gson = new Gson();
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingCoursesReturnsCreatedStatus() throws Exception {
        Map<String, String> values = new HashMap<>();
        values.put("name", "Test");
        values.put("url", "http://test.com");

        APIResponse response = client.request("POST", "/courses", gson.toJson(values));

        assertEquals(201, response.getStatus());

    }

    @Test
    public void coursesCanBeAccessedById() throws Exception {
        Course course = newTestCourse();
        courseDAO.add(course);

        APIResponse response = client.request("GET",
                "/courses/" + course.getId());
        Course retrieved = gson.fromJson(response.getBody(), Course.class);

        assertEquals(course, retrieved);
    }

    @Test
    public void missingCoursesReturnNotFoundStatus() throws Exception {
        APIResponse response = client.request("GET", "/courses/42");

        assertEquals(404, response.getStatus());
    }

    @Test
    public void addingReviewGivesCreatedStatus() throws Exception {
        Course course = newTestCourse();
        courseDAO.add(course);
        Map<String, Object> values = new HashMap<>();
        values.put("rating", 5);
        values.put("comment", "Test Comment");

        APIResponse response = client.request("POST",
                String.format("/courses/%d/reviews", course.getId()),
                gson.toJson(values));
        assertEquals(201, response.getStatus());
    }

    @Test
    public void addingReviewtoUnknownCourseThrowsError() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("rating", 5);
        values.put("comment", "Test Comment");

        APIResponse response = client.request("POST", "/courses/42/reviews", gson.toJson(values));
        assertEquals(500, response.getStatus());
    }

    @Test
    public void mulitpleReviewsReturnedForCourse() throws Exception {
        Course course = newTestCourse();
        courseDAO.add(course);
        reviewDAO.add(new Review(course.getId(), 5, "Test Comment 1"));
        reviewDAO.add(new Review(course.getId(), 4, "Test Comment 2"));

        APIResponse response = client.request("GET",
                String.format("/courses/%d/reviews", course.getId()));
        Review[] reviews = gson.fromJson(response.getBody(), Review[].class);
        assertEquals(2, reviews.length);
    }

    private Course newTestCourse() {
        return new Course("Test", "http://test.com");
    }
}