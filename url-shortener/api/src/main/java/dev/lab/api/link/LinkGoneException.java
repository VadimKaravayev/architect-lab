package dev.lab.api.link;

public class LinkGoneException extends RuntimeException {
    public LinkGoneException(String message) {
        super(message);
    }
}
