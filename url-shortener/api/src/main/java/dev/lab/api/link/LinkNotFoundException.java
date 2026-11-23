package dev.lab.api.link;

public class LinkNotFoundException extends RuntimeException {
    public LinkNotFoundException(String linkNotFound) {
        super(linkNotFound);
    }
}
