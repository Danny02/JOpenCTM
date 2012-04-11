/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.errorhandling;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 *
 * @author daniel
 */
public abstract class LoggerFactory
{

    public static final LoggerFactory MAIN;

    static {
        Iterator<LoggerFactory> factorys = ServiceLoader.load(LoggerFactory.class).iterator();
        MAIN = factorys.hasNext() ? factorys.next() : new DummyFactory();
    }

    public abstract LoggerBase getLogger(Class cl);

    public abstract LoggerBase getLogger(String name);
}
