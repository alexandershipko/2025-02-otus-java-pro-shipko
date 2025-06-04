package ru.otus.jdbc.mapper.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.executor.DbExecutor;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntitySQLMetaData;


/** Сохратяет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;


    public DataTemplateJdbc(DbExecutor dbExecutor,
                            EntitySQLMetaData entitySQLMetaData,
                            EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        String sql = entitySQLMetaData.getSelectByIdSql();
        return dbExecutor.executeSelect(connection, sql, List.of(id), rs -> {
            try {
                if (!rs.next()) {
                    return null;
                }
                T entity = entityClassMetaData.getConstructor().newInstance();
                for (Field field : entityClassMetaData.getAllFields()) {
                    field.setAccessible(true);
                    Object value = rs.getObject(field.getName());
                    field.set(entity, value);
                }
                return entity;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        String sql = entitySQLMetaData.getSelectAllSql();
        return dbExecutor.executeSelect(connection, sql, Collections.emptyList(), rs -> {
            List<T> result = new ArrayList<>();
            try {
                while (rs.next()) {
                    T entity = entityClassMetaData.getConstructor().newInstance();
                    for (Field field : entityClassMetaData.getAllFields()) {
                        field.setAccessible(true);
                        Object value = rs.getObject(field.getName());
                        field.set(entity, value);
                    }
                    result.add(entity);
                }
                return result;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).orElse(Collections.emptyList());
    }

    @Override
    public long insert(Connection connection, T object) {
        String sql = entitySQLMetaData.getInsertSql();
        List<Object> params = entityClassMetaData.getFieldsWithoutId().stream()
                .map(field -> {
                    try {
                        field.setAccessible(true);
                        return field.get(object);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        return dbExecutor.executeStatement(connection, sql, params);
    }

    @Override
    public void update(Connection connection, T object) {
        String sql = entitySQLMetaData.getUpdateSql();
        List<Object> params = new ArrayList<>();
        try {
            for (Field field : entityClassMetaData.getFieldsWithoutId()) {
                field.setAccessible(true);
                params.add(field.get(object));
            }
            Field idField = entityClassMetaData.getIdField();
            idField.setAccessible(true);
            params.add(idField.get(object));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        dbExecutor.executeStatement(connection, sql, params);
    }

}