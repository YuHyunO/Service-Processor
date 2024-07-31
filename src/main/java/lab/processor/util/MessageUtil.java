package lab.processor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

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

    public static String mapToJson(Map map, boolean prettyFormat) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String jsonString = objectMapper.writeValueAsString(map);

        if (prettyFormat) {
            objectMapper = new ObjectMapper();
            Object jsonObject = objectMapper.readValue(jsonString, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        }
        return jsonString;
    }

    public static Map<String, Object> jsonToMap(String jsonData) throws JsonProcessingException{
        if (jsonData == null)
            return null;
        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<Map<String, Object>> typeReference = new TypeReference<Map<String,Object>>() {};
        return objectMapper.readValue(jsonData, typeReference);
    }

    public static String mapToXml (Map map, boolean prettyFormat) throws JsonProcessingException {
        XmlMapper xmlMapper = XmlMapper.builder().build();
        if (prettyFormat) {
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        //modifying to not set class name for root node name
        String xmlString = xmlMapper.writeValueAsString(map);
        return xmlString;
    }

}
