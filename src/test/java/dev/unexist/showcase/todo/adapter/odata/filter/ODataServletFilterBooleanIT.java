/**
 * @package Showcase-OData-Quarkus
 *
 * @file
 * @copyright 2023-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter.odata.filter;

import dev.unexist.showcase.todo.adapter.odata.ODataServletBaseIT;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.json;

@QuarkusTest
public class ODataServletFilterBooleanIT extends ODataServletBaseIT {

    @Test
    public void shouldFilterBoolAnd() {
        String jsonOut = given()
                .when()
                    .accept(ContentType.JSON)
                    .get("/odata/Todos?$filter=contains(Title, 'Todo') and ID eq 1")
                .then()
                    .statusCode(200)
                .and()
                    .extract()
                    .asString();

        assertThatJson(jsonOut)
                .inPath("$.value..[\"ID\"]")
                    .isArray()
                    .isEqualTo(json("[1]"));
    }


    @Test
    public void shouldFilterBoolOr() {
        String jsonOut = given()
                .when()
                    .accept(ContentType.JSON)
                    .get("/odata/Todos?$filter=contains(Title, 'Todo') or ID eq 1")
                .then()
                    .statusCode(200)
                .and()
                    .extract()
                    .asString();

        assertThatJson(jsonOut)
                .inPath("$.value..[\"ID\"]")
                    .isArray()
                    .isEqualTo(json("[1,2,3]"));
    }
}
