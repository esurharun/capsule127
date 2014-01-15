package com.capsule127.wordlist;

import java.io.Serializable;

/**
* Created by marcus on 15/01/14.
*/
class IDynamicWordlistBuilderParams implements Serializable {

    private String className;
    private Object[] params;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }
}
