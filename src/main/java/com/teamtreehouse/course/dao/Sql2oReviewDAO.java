package com.teamtreehouse.course.dao;

import com.teamtreehouse.course.exc.DAOException;
import com.teamtreehouse.course.model.Review;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

/**
 * Created by Fernando on 4/12/2016.
 */
public class Sql2oReviewDAO implements ReviewDAO {

    private final Sql2o sql2o;

    public Sql2oReviewDAO(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    @Override
    public void add(Review review) throws DAOException {
        String sql = "INSERT INTO reviews(" +
                "course_id, rating, comment) VALUES (:courseId, :rating, :comment)";
        try (Connection con = sql2o.open()) {
            int id = (int) con.createQuery(sql)
                    .bind(review)
                    .executeUpdate()
                    .getKey();
            review.setId(id);
        } catch (Sql2oException ex) {
            throw new DAOException(ex, "problem adding review");
        }
    }

    @Override
    public List<Review> findAll() {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM reviews")
                    .executeAndFetch(Review.class);
        }
    }

    @Override
    public List<Review> findByCourseId(int courseId) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM reviews WHERE course_id = :courseId")
                    .addColumnMapping("COURSE_ID", "courseId")
                    .addParameter("courseId", courseId)
                    .executeAndFetch(Review.class);
        }
    }
}