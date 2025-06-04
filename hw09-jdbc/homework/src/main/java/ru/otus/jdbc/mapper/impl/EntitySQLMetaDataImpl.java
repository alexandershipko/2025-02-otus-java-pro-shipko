package ru.otus.jdbc.mapper.impl;

import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntitySQLMetaData;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;


public class EntitySQLMetaDataImpl<T> implements EntitySQLMetaData {

    private final String selectAllSql;
    private final String selectByIdSql;
    private final String insertSql;
    private final String updateSql;


    public EntitySQLMetaDataImpl(EntityClassMetaData<T> metaData) {
        String tableName = metaData.getName();
        List<Field> allFields = metaData.getAllFields();
        List<Field> fieldsWithoutId = metaData.getFieldsWithoutId();
        String idColumn = metaData.getIdField().getName();

        String allColumns = allFields.stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));

        String placeholders = fieldsWithoutId.stream()
                .map(f -> "?")
                .collect(Collectors.joining(", "));

        String columnsWithoutId = fieldsWithoutId.stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));

        String updateAssignments = fieldsWithoutId.stream()
                .map(f -> f.getName() + " = ?")
                .collect(Collectors.joining(", "));

        this.selectAllSql = "SELECT " + allColumns + " FROM " + tableName;
        this.selectByIdSql = selectAllSql + " WHERE " + idColumn + " = ?";
        this.insertSql = "INSERT INTO " + tableName + " (" + columnsWithoutId + ") VALUES (" + placeholders + ")";
        this.updateSql = "UPDATE " + tableName + " SET " + updateAssignments + " WHERE " + idColumn + " = ?";
    }

    @Override
    public String getSelectAllSql() {
        return selectAllSql;
    }

    @Override
    public String getSelectByIdSql() {
        return selectByIdSql;
    }

    @Override
    public String getInsertSql() {
        return insertSql;
    }

    @Override
    public String getUpdateSql() {
        return updateSql;
    }

}