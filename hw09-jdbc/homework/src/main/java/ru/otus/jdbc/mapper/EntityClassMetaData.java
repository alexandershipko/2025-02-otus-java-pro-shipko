package ru.otus.jdbc.mapper;

import ru.otus.annotation.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.List;


/** "Разбирает" объект на составные части */
public interface EntityClassMetaData<T> {
    String getName();

    Constructor<T> getConstructor();

    //TODO
    // Поле Id должно определять по наличию аннотации Id
    // Аннотацию @Id надо сделать самостоятельно
    @Id
    Field getIdField();

    List<Field> getAllFields();

    List<Field> getFieldsWithoutId();

}