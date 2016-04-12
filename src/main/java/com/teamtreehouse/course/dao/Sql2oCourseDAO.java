package com.teamtreehouse.course.dao;

import com.teamtreehouse.course.exc.DAOException;
import com.teamtreehouse.course.model.Course;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.Sql2oException;

import java.util.List;

/**
 * Created by Fernando on 4/11/2016.
 */
public class Sql2oCourseDAO implements CourseDAO{
    private final Sql2o sql2o;

    public Sql2oCourseDAO(Sql2o sql2o) {
        this.sql2o = sql2o;
    }

    public void add(Course course) throws DAOException {
        String sql = "INSERT INTO courses(name, url) VALUES (:name, :url)";
        try (Connection con = sql2o.open()){
            int id = (int) con.createQuery(sql)
                    .bind(course)
                    .executeUpdate()
                    .getKey();
            course.setId(id);
        } catch (Sql2oException ex) {
            throw new DAOException(ex, "problem adding course");
        }
    }

    public List<Course> findAll() {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM courses")
                    .executeAndFetch(Course.class);
        }
    }

    @Override
    public Course findById(int id) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM courses WHERE id = :id")
                    .addParameter("id", id)
                    .executeAndFetchFirst(Course.class);
        }
    }
}
