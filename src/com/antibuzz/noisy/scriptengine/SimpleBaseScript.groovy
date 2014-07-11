package com.antibuzz.noisy.scriptengine;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;

public abstract class SimpleBaseScript extends Script {
    public SimpleBaseScript() {
        super();
    }

    public Object methodMissing(String name, Object args) {
        Object scriptDelegate = getProperty("__delegate");
        if (scriptDelegate != null) {
            try {
                return DefaultGroovyMethods.invokeMethod(scriptDelegate, name, args);
            } catch (MissingMethodException oops) {
                throw new MissingMethodException(name, SimpleBaseScript.class, args, false);
            }

        } else {
            throw new MissingMethodException(name, SimpleBaseScript.class, args, false);
        }

    }

}
