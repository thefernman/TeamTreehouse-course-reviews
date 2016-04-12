package com.teamtreehouse.course.dao;

import com.teamtreehouse.course.exc.DAOException;
import com.teamtreehouse.course.model.Course;
import com.teamtreehouse.course.model.Review;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Fernando on 4/12/2016.
 */
public class Sql2oReviewDAOTest {
    private Sql2oReviewDAO reviewDAO;
    private Connection conn;
    private Sql2oCourseDAO courseDAO;
    private Course course;

    @Before
    public void setUp() throws Exception {
        String connectionString = "jdbc:h2:mem:testing;INIT=RUNSCRIPT from 'classpath:db/init.sql'";
        Sql2o sql2o = new Sql2o(connectionString, "", "");
        courseDAO = new Sql2oCourseDAO(sql2o);
        reviewDAO = new Sql2oReviewDAO(sql2o);
        //Keep connection open
        conn = sql2o.open();

        course = new Course("Test", "http://test.com");
        courseDAO.add(course);
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
    }

    @Test
    public void addingReviewSetNewId() throws Exception {
        Review review = new Review(course.getId(), 5, "test Comment");
        int originalId = review.getId();
        reviewDAO.add(review);
        assertNotEquals(originalId, review.getId());
    }

    @Test
    public void multipleReviewsAreFoundWhenTheyExistForACourse() throws Exception {
        reviewDAO.add(new Review(course.getId(), 5, "Test Comment 1"));
        reviewDAO.add(new Review(course.getId(), 1, "Test Comment 2"));

        List<Review> reviews = reviewDAO.findByCourseId(course.getId());
        assertEquals(2, reviews.size());
    }

    @Test(expected = DAOException.class)
    public void addingAReviewToANonExistingCourseFails() throws Exception {
        Review review = new Review(42, 5, "Test Comment");
        reviewDAO.add(review);


    }
}