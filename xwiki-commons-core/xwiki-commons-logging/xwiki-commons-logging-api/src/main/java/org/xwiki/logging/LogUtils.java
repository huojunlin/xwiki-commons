/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.logging;

import org.slf4j.Marker;
import org.xwiki.logging.event.BeginLogEvent;
import org.xwiki.logging.event.EndLogEvent;
import org.xwiki.logging.event.LogEvent;
import org.xwiki.logging.internal.helpers.MessageParser;
import org.xwiki.logging.internal.helpers.MessageParser.MessageElement;
import org.xwiki.logging.internal.helpers.MessageParser.MessageIndex;

/**
 * @version $Id$
 * @since 5.4RC1
 */
public final class LogUtils
{
    private LogUtils()
    {
        // Utility class
    }

    /**
     * Create and return a new {@link LogEvent} instance based on the passed parameters.
     * 
     * @param marker the log marker
     * @param level the log level
     * @param message the log message
     * @param argumentArray the event arguments to insert in the message
     * @param throwable the throwable associated to the event
     * @return the {@link LogEvent}
     */
    public static LogEvent newLogEvent(Marker marker, LogLevel level, String message, Object[] argumentArray,
        Throwable throwable)
    {
        if (marker != null) {
            if (marker.contains(LogEvent.MARKER_BEGIN)) {
                return new BeginLogEvent(marker, level, message, argumentArray, throwable);
            } else if (marker.contains(LogEvent.MARKER_END)) {
                return new EndLogEvent(marker, level, message, argumentArray, throwable);
            }
        }

        return new LogEvent(marker, level, message, argumentArray, throwable);
    }

    /**
     * @param logEvent the {@link LogEvent} to translate
     * @param translatedMessage the translated version of the {@link LogEvent} message
     * @return the translated version of the passed {@link LogEvent}
     */
    public static LogEvent translate(LogEvent logEvent, String translatedMessage)
    {
        if (translatedMessage != null) {
            MessageParser parser = new MessageParser(translatedMessage, true);

            Object[] defaultArguments = logEvent.getArgumentArray();
            Object[] arguments = new Object[defaultArguments.length];
            StringBuilder message = new StringBuilder();

            int index = 0;
            for (MessageElement element = parser.next(); element != null; element = parser.next()) {
                if (element instanceof MessageIndex) {
                    message.append(MessageParser.ARGUMENT_STR);
                    arguments[index++] = defaultArguments[((MessageIndex) element).getIndex()];
                } else {
                    message.append(element.getString());
                }
            }

            for (; index < arguments.length; ++index) {
                arguments[index] = defaultArguments[index];
            }

            return new LogEvent(logEvent.getMarker(), logEvent.getLevel(), message.toString(), arguments,
                logEvent.getThrowable());
        }

        return logEvent;
    }
}