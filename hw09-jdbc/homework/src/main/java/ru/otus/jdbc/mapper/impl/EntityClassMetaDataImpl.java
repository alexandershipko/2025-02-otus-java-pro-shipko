package ru.otus.jdbc.mapper.impl;

import ru.otus.jdbc.mapper.EntityClassMetaData;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;


public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    //TODO
    @Override
    public String getName() {
        return null;
    }

    @Override
    public Constructor<T> getConstructor() {
        return null;
    }

    @Override
    public Field getIdField() {
        return null;
    }

    @Override
    public List<Field> getAllFields() {
        return List.of();
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return List.of();
    }

}