package org.lars.commons.queries.creator;

public class CreatorException extends Exception{
    Exception original;
    public CreatorException(){
        super("Creator exception occurred");
    }
    public CreatorException(String message,Exception original){
        super(message);
        this.original=original;
    }

    public Exception getOriginal() {
        return original;
    }
}
