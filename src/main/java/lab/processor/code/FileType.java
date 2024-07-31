package lab.processor.code;

public enum FileType {
    TEXT(".txt"),
    JSON(".json"),
    LOG(".log"),
    XML(".xml"),
    DOC(".doc"),
    DOCX(".docx"),
    PDF(".pdf"),
    PPT(".ppt"),
    PPTX(".pptx"),
    HWP(".hwp"),
    XLS(".xls"),
    XLSX(".xlsx"),
    CSV(".csv"),
    PNG(".png"),
    JPG(".jpg"),
    JPEG(".jpeg"),
    GIF(".gif"),
    BMP(".bmp"),
    TIFF(".tiff"),
    RAW(".raw"),
    MP3(".mp3"),
    MP4(".mp4"),
    WAV(".wav"),
    WMV(".wmv"),
    WMA(".wma"),
    MID(".mid"),
    AVI(".avi"),
    MKV(".mkv"),
    ZIP(".zip"),
    GZIP(".gzip"),
    TAR(".tar");

    private final String extension;

    FileType(String extension) {
        this.extension = extension;
    }

    public String extension() {
        return extension;
    }
}
