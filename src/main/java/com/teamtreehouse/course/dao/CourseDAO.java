package com.teamtreehouse.course.dao;

import com.teamtreehouse.course.exc.DAOException;
import com.teamtreehouse.course.model.Course;

import java.util.List;

/**
 * Created by Fernando on 4/11/2016.
 */
public interface CourseDAO {
    void add(Course course) throws DAOException;
    List<Course> findAll();

    Course findById(int id);
}
