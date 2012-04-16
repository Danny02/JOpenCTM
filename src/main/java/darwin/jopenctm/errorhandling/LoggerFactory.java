/*
 * Copyright (C) 2012 daniel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
