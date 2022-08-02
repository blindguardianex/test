package ru.smartech.app.exceptions;

public class EntityAlreadyExist extends RuntimeException{
    public EntityAlreadyExist(String message) {
        super(message);
    }
}
