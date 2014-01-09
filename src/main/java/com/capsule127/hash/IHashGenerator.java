package com.capsule127.hash;

/**
 * Created by marcus on 09/01/14.
 */
public interface IHashGenerator {


    public String generate(String user, String password, String salt) throws Exception;

    public String generate(byte[] input, byte[] salt) throws Exception;

}
