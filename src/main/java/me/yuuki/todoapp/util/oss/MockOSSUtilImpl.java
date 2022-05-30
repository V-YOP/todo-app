package me.yuuki.todoapp.util.oss;

import me.yuuki.todoapp.property.OSSProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public class MockOSSUtilImpl extends OSSUtil {

    private static final Logger logger = LoggerFactory.getLogger(MockOSSUtilImpl.class);

    private final Path baseDir;

    public MockOSSUtilImpl(OSSProperty ossProperty) {
        baseDir = new File(ossProperty.getBucketName()).toPath();
        if (baseDir.toFile().mkdirs()) {
            logger.info("创建文件夹 {}", baseDir.toAbsolutePath());
        }
    }

    private String formatKey(String key) {
        if (key.contains("..")) {
            throw new IllegalStateException(":(");
        }
        if (key.startsWith("/")) {
            throw new IllegalArgumentException("键不能以/起始！");
        }
        return key;
    }

    @Override
    public Stream<String> listAsStream(String prefix) {
        try {
            return Files.walk(baseDir).filter(path -> !Files.isDirectory(path)).map(Path::toString)
                    .filter(s -> s.startsWith(baseDir.resolve(formatKey(prefix)).toString()))
                    .map(path -> path.substring(baseDir.toString().length() + 1));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(String key) {
        return baseDir.resolve(formatKey(key)).toFile().exists();
    }

    @Override
    public void put(String key, String value) {
        Path filePath = baseDir.resolve(formatKey(key));
        if (filePath.getParent().toFile().mkdirs()) {
            logger.info("创建文件夹 {}", filePath.getParent().toAbsolutePath());
        }
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<String> get(String key) {
        try {
            return Optional.of(String.join("\n", Files.readAllLines(baseDir.resolve(formatKey(key)))));
        } catch (IOException e) {
            return Optional.empty();
        }

    }

    @Override
    public boolean delete(String key) {
        Path path = baseDir.resolve(formatKey(key));
        logger.info("尝试删除文件：{}", path);
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            logger.warn("删除文件 {} 失败，错误：{}", path, e.toString());
            return false;
        }
    }
}
