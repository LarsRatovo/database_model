package org.lars.commons.queries.creator;

public class CreatorException extends Exception{
    public CreatorException(){
        super("Creator exception occurred");
    }
    public CreatorException(String message){
        super(message);
    }
}
