package com.capsule127.hazelcast;

import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.LoggerFactory;

/**
 * Created by marcus on 09/01/14.
 */
public class C127LoggerFactory implements LoggerFactory {
    @Override
    public ILogger getLogger(String s) {
        return new C127Logger();
    }
}
