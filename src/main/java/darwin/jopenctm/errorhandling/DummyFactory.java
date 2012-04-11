/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package darwin.jopenctm.errorhandling;

/**
 *
 * @author daniel
 */
public class DummyFactory extends LoggerFactory
{

    @Override
    public LoggerBase getLogger(Class cl)
    {
        return new DummyLogger();
    }

    @Override
    public LoggerBase getLogger(String name)
    {
        return new DummyLogger();
    }

    private static class DummyLogger implements LoggerBase
    {

        @Override
        public void warn(Object message, Throwable t)
        {
        }

        @Override
        public void warn(Object message)
        {
        }

        @Override
        public void info(Object message, Throwable t)
        {
        }

        @Override
        public void info(Object message)
        {
        }

        @Override
        public void fatal(Object message, Throwable t)
        {
        }

        @Override
        public void fatal(Object message)
        {
        }

        @Override
        public void error(Object message, Throwable t)
        {
        }

        @Override
        public void error(Object message)
        {
        }

        @Override
        public void debug(Object message, Throwable t)
        {
        }

        @Override
        public void debug(Object message)
        {
        }

        @Override
        public void trace(Object message, Throwable t)
        {
        }

        @Override
        public void trace(Object message)
        {
        }
    }
}
