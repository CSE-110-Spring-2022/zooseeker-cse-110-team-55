package com.example.zooseeker.contracts;

public interface ICommand<T> {
    public void execute(T params);
}
