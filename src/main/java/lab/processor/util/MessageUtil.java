package lab.processor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        if (prettyFormat) {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }
        String jsonString = objectMapper.writeValueAsString(map);

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
        return mapToXml(map, null, prettyFormat);
    }

    public static String mapToXml (Map map, String rootName, boolean prettyFormat) throws JsonProcessingException {
        XmlMapper xmlMapper = XmlMapper.builder().build();
        xmlMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        if (prettyFormat) {
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        }

        if (rootName == null || rootName.isEmpty()) {
            List<Object> keyList = new ArrayList<>(map.keySet());
            String root = "data";
            if (keyList.size() == 1) {
                Object key = keyList.get(0);
                Object value = map.get(key);
                if (key instanceof String) {
                    root = String.valueOf(key);
                }
                return xmlMapper.writer().withRootName(root).writeValueAsString(value);
            }
            return xmlMapper.writer().withRootName(root).writeValueAsString(map);
        } else {
            return xmlMapper.writer().withRootName(rootName).writeValueAsString(map);
        }
    }

}
