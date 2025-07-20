package ru.otus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class DbServiceDemo {
    public static void main(String[] args) {
        SpringApplication.run(DbServiceDemo.class, args);
    }
}

/*
Домашнее задание
Веб-приложение на Spring Boot

Цель:
Нучиться создавать CRUD-приложения на Spring Boot.


Описание/Пошаговая инструкция выполнения домашнего задания:
Взять за основу ДЗ к вебинару Занятие «Web сервер. ДЗ», но без страница логина;
Вместо Jetty использовать Spring Boot;
Работу с базой данных реализовать на Spring Data Jdbc;
В качестве движка шаблонов использовать Thymeleaf;
Если Thymeleaf не нравится, используйте чистый HTML и JavaScript.
Авторизацию и аутентификацию делать не надо.
*/
