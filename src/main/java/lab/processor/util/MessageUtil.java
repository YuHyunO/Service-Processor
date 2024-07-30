package lab.processor.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MessageUtil {

    public static StringBuffer toStringBuf(Throwable throwable) {
        if (throwable == null)
            return null;
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.getBuffer();
    }

    public static String toString(Throwable throwable) {
        return toStringBuf(throwable).toString();
    }


}
