/**
 * @package Showcase-OData-Quarkus
 *
 * @file Todo domain service
 * @copyright 2023-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.domain.todo;

import dev.unexist.showcase.todo.domain.CrudRepository;
import dev.unexist.showcase.todo.domain.task.Task;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@ApplicationScoped
public class TodoService {

    @Inject
    CrudRepository<Todo> todoRepository;

    /**
     * Create new {@link Todo} entry and store it in repository
     *
     * @param  base  A {@link TodoBase} entry
     *
     * @return Either id of the entry on success; otherwise {@code -1}
     **/

    public Optional<Todo> create(TodoBase base) {
        Todo todo = new Todo(base);

        boolean retval = this.todoRepository.add(todo);

        return Optional.ofNullable(retval ? todo : null);
    }

    /**
     * Update {@link Todo} at with given id
     *
     * @param  id    Id to update
     * @param  base  Values for the entry
     *
     * @return Either {@code true} on success; otherwise {@code false}
     **/

    public boolean update(int id, TodoBase base) {
        Optional<Todo> todo = this.findById(id);
        boolean ret = false;

        if (todo.isPresent()) {
            todo.get().update(base);

            ret = this.todoRepository.update(todo.get());
        }

        return ret;
    }

    /**
     * Delete {@link Todo} with given id
     *
     * @param  id  Id to delete
     *
     * @return Either {@code true} on success; otherwise {@code false}
     **/

    public boolean delete(int id) {
        return this.todoRepository.deleteById(id);
    }

    /**
     * Get all {@link Todo} entries
     *
     * @return List of all {@link Todo}; might be empty
     **/

    public List<Todo> getAll() {
        return this.todoRepository.getAll();
    }

    /**
     * Find all {@link Task} entries by given {@link Predicate}
     *
     * @param  filterBy  A {@link Predicate} to use
     *
     * @return List of all {@link Task}; might be empty
     **/

    public List<Todo> findAllByPredicate(Predicate<Todo> filterBy) {
        return this.todoRepository.findAllByPredicate(filterBy);
    }

    /**
     * Find {@link Todo} by given id
     *
     * @param  id  Id to look for
     *
     * @return A {@link Optional} of the entry
     **/

    public Optional<Todo> findById(int id) {
        return this.todoRepository.findById(id);
    }
}
