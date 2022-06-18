package me.yuuki.todoapp.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

import static me.yuuki.todoapp.property.OSSProperty.OSSTarget.MOCK;

@Component
@ConfigurationProperties("oss")
public class OSSProperty {

    private Boolean enabled = false;

    /**
     * Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com
     */
    private String endpoint;

    private String accessKeyId;

    private String accessKeySecret;

    /**
     * bucketName，如果是 MOCK 的 OSS 的话则代表文件的存储位置（相对工作目录）
     */
    private String bucketName;

    /**
     * 为了防止有人整蛊，设置一个最大Size，最多就 10 K
     */
    private Integer maxSize = 10240;

    /**
     * OSS 目标，当前仅实现 MOCK 和 ALIYUN
     */
    private OSSTarget ossTarget = MOCK;

    @PostConstruct
    private void nullCheck() {
        if (!enabled) {
            return;
        }

        Assert.notNull(ossTarget, "配置项 oss.oss-target 必须被给定！");
        Assert.notNull(bucketName, "配置项 oss.bucket-name 必须被给定！");

        if (MOCK == ossTarget)
            return;

        Assert.notNull(endpoint, "配置项 oss.endpoint 必须被给定！");
        Assert.notNull(accessKeyId, "配置项 oss.access-key-id 必须被给定！");
        Assert.notNull(accessKeySecret, "配置项 oss.access-key-secret 必须被给定！");
        Assert.notNull(maxSize, "配置项 oss.max-size 必须被给定！");
    }

    public Boolean enabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Integer getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(Integer maxSize) {
        this.maxSize = maxSize;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public OSSTarget getOssTarget() {
        return ossTarget;
    }

    public void setOssTarget(OSSTarget ossTarget) {
        this.ossTarget = ossTarget;
    }

    public enum OSSTarget {
        MOCK, ALIYUN
    }
}
