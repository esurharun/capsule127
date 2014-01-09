package com.capsule127.hash;

/**
 * Created by marcus on 09/01/14.
 */
public interface IHashTypeDescription {

    public String name();

    public String description();

    public String abbrev();

    public IHashGenerator[] generators();


}
