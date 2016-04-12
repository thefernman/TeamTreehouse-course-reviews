package com.teamtreehouse.course.dao;

import com.teamtreehouse.course.exc.DAOException;
import com.teamtreehouse.course.model.Review;

import java.util.List;

/**
 * Created by Fernando on 4/11/2016.
 */
public interface ReviewDAO {
    void add(Review review) throws DAOException;
    List<Review> findAll();
    List<Review> findByCourseId(int courseId);
}
