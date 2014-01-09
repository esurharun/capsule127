package com.capsule127.hash;

/**
 * Created by marcus on 09/01/14.
 */
public interface IHash {


    public IHashTypeDescription hash_type();

    public byte[] key();

    public byte[] hash();

    public byte[] salt();

}
