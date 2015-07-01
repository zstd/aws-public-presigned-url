package io.github.zstd.blog.aws;

import java.util.concurrent.TimeUnit;

/**
 * Params for urls generation.
 */
public class GenerationParams {

    public static final String DEFAULT_CONTENT_TYPE = "text/plain";
    public static final Long DEFAULT_EXPIRATION_TIME = TimeUnit.MINUTES.toMillis(10L);

    private final String contentType;
    private final Long expirationTimeMillis;
    private final String resourceKey;
    private final Boolean publicResource;

    public GenerationParams(String resourceKey, Boolean publicResource) {
        this(DEFAULT_CONTENT_TYPE,DEFAULT_EXPIRATION_TIME, resourceKey,publicResource);
    }

    public GenerationParams(String contentType, Long expirationTime, String resourceKey, Boolean publicResource) {
        this.contentType = contentType;
        this.expirationTimeMillis = expirationTime;
        this.resourceKey = resourceKey;
        this.publicResource = publicResource;
    }

    public String getContentType() {
        return contentType;
    }

    public Long getExpirationTimeMillis() {
        return expirationTimeMillis;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public Boolean isPublicResource() {
        return publicResource;
    }
}
