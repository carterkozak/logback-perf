package ch.qos.logback.perf;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.ByteBufferDestination;
import org.apache.logging.log4j.core.pattern.DatePatternConverter;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.util.StringBuilderFormattable;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Map;

@Plugin(name = "SimpleLayout", category = Core.CATEGORY_NAME, elementType = Appender.ELEMENT_TYPE)
public final class SimpleLog4jLayout extends AbstractStringLayout {

    private static final DatePatternConverter DATE = DatePatternConverter.newInstance(null);
    private static final byte[] NO_BYTES = new byte[0];

    public SimpleLog4jLayout() {
        super(StandardCharsets.UTF_8);
    }

    @PluginFactory
    public static SimpleLog4jLayout instance() {
        return new SimpleLog4jLayout();
    }

    @Override
    public byte[] getFooter() {
        return NO_BYTES;
    }

    @Override
    public byte[] getHeader() {
        return NO_BYTES;
    }

    @Override
    public byte[] toByteArray(LogEvent event) {
        return toSerializable(event).getBytes(StandardCharsets.UTF_8);
//        return toSerializable(event).getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public String toSerializable(LogEvent event) {
        return toBuilder(event).toString();
    }

    public StringBuilder toBuilder(LogEvent event) {
        //%d %p [%t] %logger - %m%n
        StringBuilder builder = getStringBuilder();
        DATE.format(event, builder);
        builder.append(' ');
        builder.append(event.getLevel());
        builder.append(" [").append(event.getThreadName()).append("] ");
        builder.append(event.getLoggerName());
        builder.append(" - ");
        Message message = event.getMessage();
        if (message instanceof StringBuilderFormattable) {
            StringBuilderFormattable fmt = (StringBuilderFormattable) message;
            fmt.formatTo(builder);
        } else if (message != null) {
            builder.append(message.getFormattedMessage());
        }
        builder.append(System.lineSeparator());
        // todo, throwable formatter
        return builder;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public Map<String, String> getContentFormat() {
        return null;
    }

    @Override
    public void encode(LogEvent source, ByteBufferDestination destination) {
        getStringBuilderEncoder().encode(toBuilder(source), destination);
    }
}
