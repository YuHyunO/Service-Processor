package lab.processor.handler;

import lab.processor.code.FileType;
import lab.processor.context.ContextData;
import lab.processor.core.ErrorHandler;
import lab.processor.core.Instruction;
import lab.processor.util.MessageUtil;
import lab.processor.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Default error handler class which provides convenient error trace logging work
 * */
@Slf4j
public class LoggingErrorTraceToFile implements ErrorHandler {
    /**
     * Location which the error log file to be saved.
     * */
    private String logFileLocation = "." + File.separator + "error_trace";
    /**
     * Maximum number of log files to be retained. If Maximum count reached, oldest modified file is removed sequentially.
     * Lesser or equal than 0, means no limitation to be retained.
     * */
    private int maxBackupIndex = 0;
    private final String PREFIX = "ERROR_";
    private String extension = ".log";
    private FileType fileType = FileType.LOG;


    @Override
    public void handleError(ContextData contextData) throws Exception {
        Path dirPath = Paths.get(logFileLocation);
        if (!Files.exists(dirPath))
            Files.createDirectories(dirPath);

        Path filePath = Paths.get(dirPath + File.separator + contextData.getOperationId() + extension);
        //* Get error trace log content from the ContextData.
        byte[] fileContent = getErrorTraceContent(contextData).toString().getBytes();
        if (!Files.exists(filePath))
            Files.createFile(filePath);
        Files.write(filePath, fileContent);

        if (maxBackupIndex > 0) {
            //* Get a list of files to maintain the number of backup files only when maxBackupIndex is greater than 0.
            Stream<Path> stream = Files.list(dirPath)
                    .filter(path -> !Files.isDirectory(path))
                    .filter(file -> file.getFileName().startsWith(PREFIX) && file.endsWith(extension))
                    .sorted(Comparator.comparing(file -> file.toFile().lastModified()));
            List<Path> fileList = stream.collect(Collectors.toList());

            int totalFileCount = fileList.size();
            if (totalFileCount > maxBackupIndex) {
                int idx = totalFileCount - 1;
                while (true) {
                    if (fileList.size() == maxBackupIndex) {
                        break;
                    }
                    Files.deleteIfExists(fileList.get(idx));
                    fileList.remove(idx);
                    --idx;
                }
            }
        }
    }

    public void setLoggingMode(FileType fileType) {
        switch (fileType) {
            case LOG: case TEXT: case JSON: case XML: {
                this.fileType = fileType;
                this.extension = fileType.extension();
            }
            default: throw new IllegalArgumentException("Unsupported file type: " + fileType);
        }
    }

    public FileType getFileType() {
        return fileType;
    }

    public void setLogFileLocation(String logFileLocation) {
        if (logFileLocation == null || logFileLocation.isEmpty())
            throw new IllegalArgumentException("logFileLocation is null or empty");

        logFileLocation = logFileLocation.trim();
        //Remove the last file separator character
        while (true) {
            if (logFileLocation.endsWith(File.separator)) {
                logFileLocation = logFileLocation.substring(0, logFileLocation.length() - 1);
                continue;
            }
            break;
        }
        this.logFileLocation = logFileLocation.trim();
    }

    private StringBuffer getErrorTraceContent(ContextData contextData) throws IOException {
        StringBuffer buffer = new StringBuffer();
        Map<String, Object> data = new LinkedHashMap<>();
        Instruction instr = contextData.getInstruction();
        data.put("operationId", contextData.getOperationId());
        data.put("instructionId", instr.getInstructionId());
        data.put("processId", instr.getProcessId());
        data.put("active", instr.isActive());
        data.put("ignoreError", instr.isIgnoreError());
        data.put("startTime", TimeUtil.getFormattedTime(contextData.getStartTime(), "yyyy-MM-dd HH:mm:ss.SSS"));
        data.put("endTime", TimeUtil.getFormattedTime(contextData.getStartTime(), "yyyy-MM-dd HH:mm:ss.SSS"));
        data.put("serviceTrace", contextData.getServiceTraceMessage());
        data.put("errorTrace", contextData.getErrorTraceMessage());

        switch (fileType) {
            case LOG: case TEXT: {
                buffer.append("[Error log]\n");
                for (String key : data.keySet()) {
                    buffer.append("-");
                    buffer.append(key);
                    buffer.append(": ");
                    buffer.append(data.get(key));
                    buffer.append("\n");
                }
                if (!buffer.isEmpty())
                    buffer.setLength(buffer.length() - "\n".length());
                break;
            }
            case JSON: {
                Map<String, Map<String, Object>> wrapper = new LinkedHashMap<>();
                wrapper.put("errorLog", data);
                buffer.append(MessageUtil.mapToJson(wrapper, true));
                break;
            }
            case XML: {
                Map<String, Map<String, Object>> wrapper = new LinkedHashMap<>();
                wrapper.put("errorLog", data);
                buffer.append(MessageUtil.mapToXml(wrapper, true));
            }
        }
        return buffer;
    }

}
