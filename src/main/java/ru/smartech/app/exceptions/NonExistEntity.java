package ru.smartech.app.exceptions;

public class NonExistEntity extends RuntimeException{

    public NonExistEntity(String message) {
        super(message);
    }
}
