/**
 * @package Showcase-OData-Quarkus
 *
 * @file Stupid integration test
 * @copyright 2023-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TodoResourceIntegrationTest {

    @Test
    @Order(1)
    public void shouldGetEmptyTodoList() {
        given()
          .when().get("/todo")
          .then()
             .statusCode(204);
    }

    @Test
    @Order(2)
    public void shouldNotFindAnyTodo() {
        given()
                .when()
                    .accept(ContentType.JSON)
                    .get("/todo/11")
                .then()
                    .statusCode(404);
    }

    @Test
    @Order(3)
    public void shouldCreateNewTodo() {
        createTodo();
    }

    @Test
    @Order(4)
    public void shouldGetEmptyTaskList() {
        given()
          .when().get("/todo/1/task")
          .then()
             .statusCode(204);
    }

    @Test
    @Order(5)
    public void shouldNotFindAnyTask() {
        given()
                .when()
                    .accept(ContentType.JSON)
                    .get("/todo/11/task/1")
                .then()
                    .statusCode(404);
    }

    @Test
    @Order(6)
    public void shouldCreateNewTask() {
        createTodo();
        createTask();
    }

    /**
     * Create an entry via REST
     **/

    private static void createTodo() {
        given()
                .when()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(TodoFixture.createTodo())
                    .post("/todo")
                .then()
                    .statusCode(201);
    }

    /**
     * Create an entry via REST
     **/

    private static void createTask() {
        given()
                .when()
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .body(TaskFixture.createTask())
                    .post("/todo/1/task")
                .then()
                    .statusCode(201);
    }
}