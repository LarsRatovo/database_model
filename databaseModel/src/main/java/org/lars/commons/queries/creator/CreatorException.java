package org.lars.commons.queries.creator;

public class CreatorException extends Exception{
    Exception original;
    public CreatorException(String message,Exception original){
        super(message);
        this.original=original;
    }

    public CreatorException(String message) {
        super(message);
    }

    public Exception getOriginal() {
        return original;
    }
}
