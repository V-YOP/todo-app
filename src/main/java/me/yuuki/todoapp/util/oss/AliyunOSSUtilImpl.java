package me.yuuki.todoapp.util.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ListObjectsRequest;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import me.yuuki.todoapp.property.OSSProperty;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * 阿里云 OSS 工具实现类，基本所有操作都不是原子的……哈哈哈
 * <p>
 * 其实我也幻想，要是能用 redis 多好呀
 */
public class AliyunOSSUtilImpl extends OSSUtil {
    private final String bucketName;
    private final OSS ossClient;

    public AliyunOSSUtilImpl(OSSProperty ossProperty) {
        bucketName = ossProperty.getBucketName();
        ossClient = new OSSClientBuilder()
                .build(ossProperty.getEndpoint(),
                        ossProperty.getAccessKeyId(),
                        ossProperty.getAccessKeySecret());
        try {
            if (!ossClient.doesBucketExist(bucketName)) {
                ossClient.createBucket(bucketName);
            }
        } catch (Exception e) {
            throw new IllegalStateException("创建 OSS 客户端实例失败！", e);
        }
    }

    @Override
    public Stream<String> listAsStream(String prefix) {

        Iterator<List<String>> iter = new Iterator<List<String>>() {
            ObjectListing objectListing = ossClient.listObjects(new ListObjectsRequest(bucketName)
                    .withPrefix(prefix)
                    .withMaxKeys(1000));

            @Override
            public boolean hasNext() {
                return objectListing.getNextMarker() != null;
            }

            @Override
            public List<String> next() {
                List<String> res = objectListing
                        .getObjectSummaries().stream()
                        .map(OSSObjectSummary::getKey).collect(Collectors.toList());
                objectListing = ossClient.listObjects(new ListObjectsRequest(bucketName)
                        .withPrefix(prefix)
                        .withMarker(objectListing.getNextMarker())
                        .withMaxKeys(1000));
                return res;
            }
        };

        Spliterator<List<String>> listSpliterator = Spliterators.spliteratorUnknownSize(iter, Spliterator.NONNULL);

        return StreamSupport.stream(listSpliterator, false).flatMap(List::stream);
    }

    @Override
    public boolean exists(String key) {
        return ossClient.doesObjectExist(bucketName, key);
    }

    @Override
    public void put(String key, String value) {
        ossClient.putObject(bucketName, key, new ByteArrayInputStream(value.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public Optional<String> get(String key) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             InputStream inputStream = ossClient.getObject(bucketName, key).getObjectContent()) {
            byte[] b = new byte[10240];
            int n;
            while ((n = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, n);
            }
            return Optional.of(outputStream.toString());
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean delete(String key) {
        if (notExists(key))
            return false;
        ossClient.deleteObject(bucketName, key);
        return true;
    }
}
