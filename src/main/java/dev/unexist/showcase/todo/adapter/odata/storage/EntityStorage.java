/**
 * @package Showcase-OData-Quarkus
 *
 * @file OData todo entity storage
 * @copyright 2023-present Christoph Kappel <christoph@unexist.dev>
 * @version $Id$
 *
 * This program can be distributed under the terms of the Apache License v2.0.
 * See the file LICENSE for details.
 **/

package dev.unexist.showcase.todo.adapter.odata.storage;

import dev.unexist.showcase.todo.adapter.odata.entity.TaskEntityFactory;
import dev.unexist.showcase.todo.adapter.odata.entity.TodoEntityFactory;
import dev.unexist.showcase.todo.domain.task.Task;
import dev.unexist.showcase.todo.domain.todo.Todo;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.edm.EdmKeyPropertyRef;
import org.apache.olingo.commons.api.edm.EdmPrimitiveType;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeException;
import org.apache.olingo.commons.api.edm.EdmProperty;
import org.apache.olingo.commons.api.edm.EdmType;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.uri.UriParameter;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Locale;

@ApplicationScoped
public class EntityStorage {

    @Inject
    TodoEntityFactory todoFactory;

    @Inject
    TaskEntityFactory taskFactory;

    /**
     * Read data from an entity collection
     *
     * @param  edmEntitySet  A {@link EdmEntitySet} to use
     *
     * @return Either found {@link EntityCollection} on success; otherwise {@code null}
     *
     * @throws ODataApplicationException
     **/

    public EntityCollection readEntitySetData(EdmEntitySet edmEntitySet) {
        EntityCollection retVal = null;

        if (TodoEntityFactory.ES_NAME.equals(edmEntitySet.getName())) {
            retVal = this.todoFactory.getAll();
        } else if (TaskEntityFactory.ES_NAME.equals(edmEntitySet.getName())) {
            retVal = this.taskFactory.getAll();
        }

        return retVal;
    }

    /**
     * Read data from an entity
     *
     * @param  edmEntitySet  A {@link EdmEntitySet} to use
     * @param  keyParams     A list of URI parameters
     *
     * @return Either found {@link Entity} on success; otherwise {@code null}
     *
     * @throws ODataApplicationException
     */

    public Entity readEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams)
            throws ODataApplicationException
    {
        Entity retVal = null;
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        if (TodoEntityFactory.ET_NAME.equals(edmEntityType.getName())) {
            retVal = this.todoFactory.findEntity(edmEntityType, keyParams);
        } else if (TaskEntityFactory.ET_NAME.equals(edmEntityType.getName())) {
            retVal = this.taskFactory.findEntity(edmEntityType, keyParams);
        }

        return retVal;
    }

    /**
     * Create new entity from request entity
     *
     * @param  edmEntitySet   A {@link EdmEntitySet} to use
     * @param  requestEntity  A {@link Entity} to update
     *
     * @return Either updated {@link Entity} on success; otherwise {@code null}
     */

    public Entity createEntityData(EdmEntitySet edmEntitySet, Entity requestEntity) {
        Entity retVal = null;
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        if (TodoEntityFactory.ET_NAME.equals(edmEntityType.getName())) {
            retVal = this.todoFactory.createEntity(edmEntityType, requestEntity);
        } else if (TaskEntityFactory.ET_NAME.equals(edmEntityType.getName())) {
            retVal = this.taskFactory.createEntity(edmEntityType, requestEntity);
        }

        return retVal;
    }

    /**
     * Update entity based on given update entity
     *
     * @param  edmEntitySet   A {@link EdmEntitySet} to use
     * @param  keyParams      A list of URI parameters
     * @param  updateEntity   A {@link Entity} to update
     * @param  httpMethod
     *
     * @throws ODataApplicationException
     */

    public void updateEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams,
                                 Entity updateEntity, HttpMethod httpMethod)
            throws ODataApplicationException
    {
        updateEntity(edmEntitySet.getEntityType(), keyParams, updateEntity, httpMethod);

    }

    /**
     * Delete entity based on given data
     *
     * @param  edmEntitySet   A {@link EdmEntitySet} to use
     * @param  keyParams      A list of URI parameters
     *
     * @throws ODataApplicationException
     **/

    public void deleteEntityData(EdmEntitySet edmEntitySet, List<UriParameter> keyParams)
            throws ODataApplicationException {
        EdmEntityType edmEntityType = edmEntitySet.getEntityType();

        if (TodoEntityFactory.ET_NAME.equals(edmEntityType.getName())) {
            this.todoFactory.deleteEntity(edmEntityType, keyParams);
        } else if (TaskEntityFactory.ET_NAME.equals(edmEntityType.getName())) {
            this.taskFactory.deleteEntity(edmEntityType, keyParams);
        }
    }

    public Entity getRelatedEntity(Entity entity, EdmEntityType relatedEntityType) {
        EntityCollection collection = getRelatedEntityCollection(entity, relatedEntityType);

        if (collection.getEntities().isEmpty()) {
            return null;
        }

        return collection.getEntities().get(0);
    }

    public Entity getRelatedEntity(Entity entity, EdmEntityType relatedEntityType,
                                   List<UriParameter> keyPredicates)
            throws ODataApplicationException
    {
        EntityCollection relatedEntities = getRelatedEntityCollection(entity, relatedEntityType);

        return findEntity(relatedEntityType, relatedEntities, keyPredicates);
    }

    public EntityCollection getRelatedEntityCollection(Entity sourceEntity, EdmEntityType targetEntityType) {
        EntityCollection navigationTargetEntityCollection = new EntityCollection();
        FullQualifiedName relatedEntityFqn = targetEntityType.getFullQualifiedName();
        String sourceEntityFqn = sourceEntity.getType();

        if (sourceEntityFqn.equals(TodoEntityFactory.ET_FQN.getFullQualifiedNameAsString())
                && relatedEntityFqn.equals(TaskEntityFactory.ET_FQN))
        {
            int todoId = (Integer) sourceEntity.getProperty("ID").getValue();

            for (Task task : this.taskService.getAllByTodoId(todoId)) {
                navigationTargetEntityCollection.getEntities()
                        .add(createTaskEntity(task));
            }
        } else if (sourceEntityFqn.equals(TaskEntityFactory.ET_FQN.getFullQualifiedNameAsString())
                && relatedEntityFqn.equals(TodoEntityFactory.ET_FQN))
        {
            int todoId = (Integer) sourceEntity.getProperty("TodoID").getValue();

             for (Todo todo : this.todoService.getAllById(todoId)) {
                 navigationTargetEntityCollection.getEntities()
                         .add(createTodoEntity(todo));
             }
        }

        if (navigationTargetEntityCollection.getEntities().isEmpty()) {
          return null;
        }

        return navigationTargetEntityCollection;
    }

    /**
     * Update entity based on given parameters
     *
     * @param  edmEntityType  A {@link EdmEntityType} to use
     * @param  keyParams      A list of URI parameters
     * @param  entity         A {@link Property} to update
     * @param  httpMethod     {@link HttpMethod} used for the update call
     *
     * @throws ODataApplicationException
     **/

    private void updateEntity(EdmEntityType edmEntityType, List<UriParameter> keyParams,
                              Entity entity, HttpMethod httpMethod)
            throws ODataApplicationException
    {
        Entity foundEntity = getEntity(edmEntityType, keyParams);

        if (null == foundEntity) {
            throw new ODataApplicationException("Entity not found",
                    HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
        }

        /* Loop over all properties and replace the values with the values of the given payload */
        List<Property> existingProperties = foundEntity.getProperties();

        for (Property existingProp : existingProperties) {
            String propName = existingProp.getName();

            /* Ignore the key properties, they aren't updateable */
            if (isKey(edmEntityType, propName)) {
                continue;
            }

            Property updateProperty = entity.getProperty(propName);
            if (null == updateProperty) {
                /* If a property has NOT been added to the request payload depending on the
                HttpMethod, our behavior is different */
                if (httpMethod.equals(HttpMethod.PATCH)) {
                    /* As of the OData spec, in case of PATCH, the existing property is not touched */
                    continue;
                } else if (httpMethod.equals(HttpMethod.PUT)) {
                    existingProp.setValue(existingProp.getValueType(), null);

                    continue;
                }
            }

            existingProp.setValue(existingProp.getValueType(),
                    updateProperty.getValue());
        }

        /* Finally update entity */
        if (TodoEntityFactory.ET_NAME.equals(edmEntityType.getName())) {
            this.taskFactory.updateEntity(edmEntityType, foundEntity);
        } else if (TaskEntityFactory.ET_NAME.equals(edmEntityType.getName())) {
            this.taskFactory.updateEntity(edmEntityType, foundEntity);
        }
    }

    /**
     * Get a single entity based on given data
     *
     * @param  edmEntityType  A {@link EdmEntityType} to use
     * @param  keyParams      A list of URI parameters
     *
     * @return Either found {@link Entity}; otherwise {@code null}
     *
     * @throws ODataApplicationException
     **/

    private Entity getEntity(EdmEntityType edmEntityType, List<UriParameter> keyParams)
            throws ODataApplicationException
    {
        Entity requestedEntity = findEntity(edmEntityType, keyParams);

        if (null == requestedEntity) {
            throw new ODataApplicationException("Entity for requested key doesn't exist",
                    HttpStatusCode.NOT_FOUND.getStatusCode(), Locale.ENGLISH);
        }

        return requestedEntity;
    }

    /**
     * Find entity based on given parameters
     *
     * @param  edmEntityType  A {@link EdmEntityType} to use
     * @param  entitySet      A {@link EntityCollection} to use
     * @param  keyParams      A list of URI parameters
     *
     * @return Either found {@ink Entity}; otherwise {@code null}
     * @throws ODataApplicationException
     **/

    public Entity findEntity(EdmEntityType edmEntityType, EntityCollection entitySet,
                             List<UriParameter> keyParams)
            throws ODataApplicationException
    {
        Entity retVal = null;
        List<Entity> entityList = entitySet.getEntities();

        for (Entity entity : entityList) {
            boolean foundEntity = entityMatchesAllKeys(edmEntityType, entity, keyParams);

            if (foundEntity) {
                retVal = entity;
                break;
            }
        }

        return retVal;
    }

    /**
     *  Check whether given property is a reference key
     *
     * @param  edmEntityType  A {@link EdmEntityType} to use
     * @param  propertyName   Property name to check
     *
     * @return Either {@code true} if the key is a ref key; otherwise {@code false}
     **/

    private boolean isKey(EdmEntityType edmEntityType, String propertyName) {
        boolean retVal = false;

        List<EdmKeyPropertyRef> keyPropertyRefs = edmEntityType.getKeyPropertyRefs();

        for (EdmKeyPropertyRef propRef : keyPropertyRefs) {
            String keyPropertyName = propRef.getName();

            if (keyPropertyName.equals(propertyName)) {
                retVal = true;

                break;
            }
        }

        return retVal;
    }

    /**
     * Match all given keys
     *
     * @param  edmEntityType  A {@link EdmEntityType} to use
     * @param  rt_entity      A {@link Entity} to return
     * @param  keyParams      A list of URI parameters
     *
     * @return Either {@code true} if the entity matches; otherwise {@code false}
     * @throws ODataApplicationException
     **/

    public boolean entityMatchesAllKeys(EdmEntityType edmEntityType, Entity rt_entity, List<UriParameter> keyParams)
            throws ODataApplicationException
    {
        for (final UriParameter key : keyParams) {
            String keyName = key.getName();
            String keyText = key.getText();

            /* We need this info for the comparison below */
            EdmProperty edmKeyProperty = (EdmProperty) edmEntityType.getProperty(keyName);

            Boolean isNullable = edmKeyProperty.isNullable();
            Integer maxLength = edmKeyProperty.getMaxLength();
            Integer precision = edmKeyProperty.getPrecision();
            Boolean isUnicode = edmKeyProperty.isUnicode();
            Integer scale = edmKeyProperty.getScale();

            /* get the EdmType in order to compare */
            EdmType edmType = edmKeyProperty.getType();
            EdmPrimitiveType edmPrimitiveType = (EdmPrimitiveType) edmType;

            /* Runtime data: the value of the current entity */
            Object valueObject = rt_entity.getProperty(keyName).getValue(); // null-check is done in FWK

            /* Now need to compare the valueObject with the keyText String */
            String valueAsString = null;
            try {
                valueAsString = edmPrimitiveType.valueToString(valueObject, isNullable, maxLength,
                        precision, scale, isUnicode);
            } catch (EdmPrimitiveTypeException e) {
                throw new ODataApplicationException("Failed to retrieve String value",
                        HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH, e);
            }

            if (valueAsString == null || !valueAsString.equals(keyText)) {
                return false;
            }
        }

        return true;
    }
}