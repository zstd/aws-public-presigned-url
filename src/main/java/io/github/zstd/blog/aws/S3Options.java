package io.github.zstd.blog.aws;

/**
 * Assess options for S3.
 */
public class S3Options {
    private final String bucket;
    private final String accessId;
    private final String secretKey;

    S3Options(String bucket, String accessId, String secretKey) {
        this.bucket = bucket;
        this.accessId = accessId;
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public String getAccessId() {
        return accessId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public String toString() {
        return "S3Options{" +
                "bucket='" + bucket + '\'' +
                ", accessId='" + accessId + '\'' +
                ", secretKey='" + secretKey + '\'' +
                '}';
    }
}
