package com.teamtreehouse.course;

import com.google.gson.Gson;
import com.teamtreehouse.course.dao.CourseDAO;
import com.teamtreehouse.course.dao.ReviewDAO;
import com.teamtreehouse.course.dao.Sql2oCourseDAO;
import com.teamtreehouse.course.dao.Sql2oReviewDAO;
import com.teamtreehouse.course.exc.APIError;
import com.teamtreehouse.course.exc.DAOException;
import com.teamtreehouse.course.model.Course;
import com.teamtreehouse.course.model.Review;
import org.sql2o.Sql2o;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

/**
 * Created by Fernando on 4/11/2016.
 */
public class API {
    public static void main(String[] args) {
        String datasource = "jdbc:h2:~/review.db";

        if (args.length > 0) {
            if (args.length != 2) {
                System.out.println("java API <port> <datasource>");
                System.exit(0);
            }
            port(Integer.parseInt(args[0]));
            datasource = args[1];
        }

        Sql2o sql2o = new Sql2o(
                String.format("%s;INIT=RUNSCRIPT from 'classpath:db/init.sql'", datasource), "", "");

        CourseDAO courseDAO = new Sql2oCourseDAO(sql2o);
        ReviewDAO reviewDAO = new Sql2oReviewDAO(sql2o);
        Gson gson = new Gson();


        post("/courses", "application/json", (request, response) -> {
            Course course = gson.fromJson(request.body(), Course.class);
            courseDAO.add(course);
            response.status(201);
//            response.type("application/json");not needed with the after()
            return course;
        }, gson::toJson);

        get("/courses", "application/json",
                (request, response) -> courseDAO.findAll(), gson::toJson);

        get("/courses/:id", "application/json", (request, response) -> {
            int id = Integer.parseInt(request.params("id"));
            Course course = courseDAO.findById(id);
            if (course == null) {
                throw new APIError(404, "Could not find course with id " + id);
            }
            return course;
        }, gson::toJson);

        post("/courses/:courseId/reviews", "application/json", (request, response) -> {
            int courseId = Integer.parseInt(request.params("courseId"));
            Review review = gson.fromJson(request.body(), Review.class);
            review.setCourseId(courseId);

            try {
                reviewDAO.add(review);//try to add to existing course, exception if course doesnt exist
            } catch (DAOException ex) {
                throw new APIError(500, ex.getMessage());
            }
            response.status(201);
            return review;
        }, gson::toJson);

        get("/courses/:courseId/reviews", "application/json", (request, response) -> {
            int courseId = Integer.parseInt(request.params("courseId"));
            return reviewDAO.findByCourseId(courseId);
        }, gson::toJson);

        exception(APIError.class, (exception, request, response) -> {
            APIError err = (APIError) exception;
            Map<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("status", err.getStatus());
            jsonMap.put("errorMessage", err.getMessage());
            response.type("application/json");
            response.status(err.getStatus());
            response.body(gson.toJson(jsonMap));
        });

        after((request, response) -> {
            response.type("application/json");
        });
    }
}
