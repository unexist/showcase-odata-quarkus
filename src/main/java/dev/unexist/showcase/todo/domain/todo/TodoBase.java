/**
 * @package Showcase-OData-Quarkus
 *
 * @file Todo base class
 * @copyright 2023-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.domain.todo;

import com.fasterxml.jackson.annotation.JsonInclude;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodoBase {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    private DueDate dueDate;

    /**
     * Get title of the entry
     *
     * @return Title of the entry
     **/

    public String getTitle() {
        return this.title;
    }

    /**
     * Set title of the entry
     *
     * @param  title  Title of the entry
     **/

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get description of entry
     *
     * @return Description of the entry
     **/

    public String getDescription() {
        return this.description;
    }

    /**
     * Set description of the entry
     *
     * @param description
     *          Description of the entry
     **/

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get due state of the entry
     *
     * @return Due state of the entry
     **/

    public DueDate getDueDate() {
        return this.dueDate;
    }

    /**
     * Set due date of the entry
     *
     * @param  dueDate  Due date of the entry
     **/

    public void setDueDate(DueDate dueDate) {
        Objects.requireNonNull(dueDate, "DueDate cannot be null");

        this.dueDate = dueDate;
    }
}
