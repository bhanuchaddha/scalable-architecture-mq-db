package com.bhanuchaddha.architecture.taskservice;

import com.bhanuchaddha.architecture.taskservice.data.entity.Solution;
import com.bhanuchaddha.architecture.taskservice.dto.ProblemDto;
import com.bhanuchaddha.architecture.taskservice.dto.SolutionDto;
import com.bhanuchaddha.architecture.taskservice.dto.TaskDto;
import com.bhanuchaddha.architecture.taskservice.tasks.CreateTaskRequest;
import io.restassured.RestAssured;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.stream.Collectors;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

@Ignore
public class KnapsackApiTest {

    @Test
    public void task_is_created_with_submitted_status_and_timestamp(){
        RestAssured.baseURI = "http://localhost:6543";

        given()
                .contentType("application/json")
                .body(createTaskRequest1())

                .when()
                .post("/knapsack")

                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body("task",is(notNullValue())
                        ,"status",equalTo("submitted")
                        ,"timestamps.submitted",is(notNullValue())
                );
    }

    @Test
    public void
    task_has_null_started_and_completed_timestamps(){
        RestAssured.baseURI = "http://localhost:6543";

        given()
                .contentType("application/json")
                .body(createTaskRequest1())

                .when()
                .post("/knapsack")

                .then()
                .assertThat()
                .statusCode(200)
                .and()
                .body(
                        "status",equalTo("submitted")
                        ,"timestamps.started",is(nullValue())
                        ,"timestamps.completed",is(nullValue())
                );
    }

    @Test
    public void
    incorrect_problem_result_service_error(){
        RestAssured.baseURI = "http://localhost:6543";

        given()
                .contentType("application/json")
                .body(incorrectCreateTaskRequest())

                .when()
                .post("/knapsack")

                .then()
                .assertThat()
                .statusCode(500)
                .and()
                .body(
                        "error",equalTo("Internal Server Error"),
                        "message",equalTo("values and weights array should have same size")
                );
    }


    @Test
    public void
    task_goes_to_started_when_knapsack_solver_start_processing() throws InterruptedException {
        RestAssured.baseURI = "http://localhost:6543";

        String taskId =
                given()
                .contentType("application/json")
                .body(createTaskRequest1())
                .when()
                .post("/knapsack")

                .then()
                .assertThat()

                .extract().path("task")
        ;

        given()
                .contentType("application/json")
                .pathParam("taskId",taskId)
                .when()
                .get("/knapsack/{taskId}")
                .then()
                .statusCode(200)
                .body("status",equalTo("started"))
                .body("timestamps.started",is(notNullValue()))
                .body("timestamps.completed",is(nullValue()));


    }

    @Test
    public void
    task_goes_to_completed_state_when_knapsack_solver_has_completed_processing() throws InterruptedException {
        RestAssured.baseURI = "http://localhost:6543";

        String taskId =
                given()
                        .contentType("application/json")
                        .body(createTaskRequest1())
                        .when()
                        .post("/knapsack")

                        .then()

                        .extract().path("task")
                ;


        // In the Implementation we have introduced the timeout to simulate heavy work load.
        // Using the same delay here
        Thread.sleep(10000);

        given()
                .contentType("application/json")
                .pathParam("taskId",taskId)
                .when()
                .get("/knapsack/{taskId}")
                .then()
                .statusCode(200)
                .body("status",equalTo("completed"))
                .body("timestamps.completed",is(notNullValue()))
                .body("solution",is(notNullValue()))
        ;

    }

    @Test
    public void
    solution_key_is_only_present_when_task_is_completed() throws InterruptedException {
        RestAssured.baseURI = "http://localhost:6543";

        String taskId =
                given()
                        .contentType("application/json")
                        .body(createTaskRequest1())
                        .when()
                        .post("/knapsack")

                        .then()
                        .assertThat()

                        .body(not(hasKey("solution")))
                        .extract().path("task")
                ;

        given()
                .contentType("application/json")
                .pathParam("taskId",taskId)
                .when()
                .get("/knapsack/{taskId}")
                .then()
                .statusCode(200)
                .body("status",equalTo("started"))
                .body(not(hasKey("solution")));



        // In the Implementation we have introduced the timeout to simulate heavy work load.
        // Using the same delay here
        Thread.sleep(10000);

        given()
                .contentType("application/json")
                .pathParam("taskId",taskId)
                .when()
                .get("/knapsack/{taskId}")
                .then()
                .statusCode(200)
                .body("status",equalTo("completed"))

                //.body(hasKey("solution"))
                .body("solution",is(notNullValue()))
        ;

    }

    @Test
    public void
    task_is_solved_correctly() throws InterruptedException {
        RestAssured.baseURI = "http://localhost:6543";

        String taskId =
                given()
                        .contentType("application/json")
                        .body(createTaskRequest2())
                        .when()
                        .post("/knapsack")

                        .then()

                        .extract().path("task")
                ;


        // In the Implementation we have introduced the timeout to simulate heavy work load.
        // Using the same delay here
        Thread.sleep(10000);

        given()
                .contentType("application/json")
                .pathParam("taskId",taskId)
                .when()
                .get("/knapsack/{taskId}")
                .then()
                .statusCode(200)
                .body("status",equalTo("completed"))
                .body("timestamps.completed",is(notNullValue()))
                .body("solution",is(notNullValue()))
                .body("solution.total_value", equalTo(expectedSolution2().getTotalValue().intValue()))
                .body("solution.packed_items", containsInAnyOrder(expectedSolution2().getPackedItems().get(0), expectedSolution2().getPackedItems().get(1)))

        ;

    }



    private CreateTaskRequest createTaskRequest1(){
        return CreateTaskRequest.builder()
                .problem(ProblemDto.builder()
                        .values(Arrays.stream(new int[]{360, 83, 59, 130, 431, 67, 230, 52, 93, 125, 670, 892, 600, 38, 48, 147, 78, 256, 63, 17, 120, 164, 432, 35,
                                92, 110, 22, 42, 50, 323, 514, 28, 87, 73, 78, 15, 26, 78, 210, 36, 85, 189, 274, 43, 33, 10, 19, 389, 276, 312}).boxed().map(BigInteger::valueOf).collect(Collectors.toList()))
                        .weights(Arrays.stream(new int[]{7, 0, 30, 22, 80, 94, 11, 81, 70, 64, 59, 18, 0, 36, 3, 8, 15, 42, 9, 0, 42, 47, 52, 32, 26, 48, 55, 6, 29,
                                8, 2, 4, 18, 56, 7, 29, 93, 44, 71, 3, 86, 66, 31,65, 0, 79, 20, 65, 52, 13}).boxed().map(BigInteger::valueOf).collect(Collectors.toList()))
                        .capacity(BigInteger.valueOf(850))
                        .build())
                .build();
    }

    private CreateTaskRequest incorrectCreateTaskRequest(){
        return CreateTaskRequest.builder()
                .problem(ProblemDto.builder()
                        .values(Arrays.stream(new int[]{ 274, 43, 33, 10, 19, 389, 276, 312}).boxed().map(BigInteger::valueOf).collect(Collectors.toList()))
                        .weights(Arrays.stream(new int[]{7, 0, 30, 22, 80, 94, 11, 81, 70, 64, 59, 18, 0, 36, 3, 8, 15, 42, 9, 0, 42, 47, 52, 32, 26, 48, 55, 6, 29,
                                8, 2, 4, 18, 56, 7, 29, 93, 44, 71, 3, 86, 66, 31,65, 0, 79, 20, 65, 52, 13}).boxed().map(BigInteger::valueOf).collect(Collectors.toList()))
                        .capacity(BigInteger.valueOf(850))
                        .build())
                .build();
    }

    private CreateTaskRequest createTaskRequest2(){
        return CreateTaskRequest.builder()
                .problem(ProblemDto.builder()
                        .values(Arrays.stream(new int[]{10, 3, 30}).boxed().map(BigInteger::valueOf).collect(Collectors.toList()))
                        .weights(Arrays.stream(new int[]{10, 20, 33}).boxed().map(BigInteger::valueOf).collect(Collectors.toList()))
                        .capacity(BigInteger.valueOf(60))
                        .build())
                .build();
    }

    private SolutionDto expectedSolution2(){
        return SolutionDto.builder()
                .totalValue(BigInteger.valueOf(40))
                .packedItems(Arrays.asList(0, 2))
                .build();
    }


}
