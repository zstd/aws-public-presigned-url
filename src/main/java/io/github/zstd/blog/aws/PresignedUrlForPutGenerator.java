package io.github.zstd.blog.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.internal.StaticCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.Headers;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.util.Throwables;


import java.net.URISyntaxException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

/**
 * Generates presigned url for uploading file to Amazon S3.
 */
public class PresignedUrlForPutGenerator {

    private final S3Options s3Options;

    public PresignedUrlForPutGenerator(S3Options s3Options) {
        this.s3Options = s3Options;
    }

    public String generate(GenerationParams params) {
        String bucket = s3Options.getBucket();
        long millis = Calendar.getInstance().getTimeInMillis();
        Date expires = new Date(millis + params.getExpirationTimeMillis());
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, params.getResourceKey());
        generatePresignedUrlRequest
                .withMethod(HttpMethod.PUT)
                .withExpiration(expires)
                .withContentType(params.getContentType())
                .addRequestParameter("Content-Type", params.getContentType());
        // this parameter needed to make resource uploaded with presigned-url immediately public-available
        if(params.isPublicResource()) {
            generatePresignedUrlRequest.addRequestParameter(Headers.S3_CANNED_ACL, CannedAccessControlList.PublicRead.toString());
        }

        URL result = createS3Client(s3Options).generatePresignedUrl(generatePresignedUrlRequest);
        try {
            return result.toURI().toString();
        } catch (URISyntaxException ex) {
            throw Throwables.failure(ex);
        }
    }

    public String generateGet(GenerationParams params) {
        String bucket = s3Options.getBucket();
        long millis = Calendar.getInstance().getTimeInMillis();
        Date expires = new Date(millis + params.getExpirationTimeMillis());
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucket, params.getResourceKey());
        generatePresignedUrlRequest
                .withMethod(HttpMethod.GET)
                .withExpiration(expires);

        URL result = createS3Client(s3Options).generatePresignedUrl(generatePresignedUrlRequest);
        try {
            return result.toURI().toString();
        } catch (URISyntaxException ex) {
            throw Throwables.failure(ex);
        }
    }

    private AmazonS3Client createS3Client(S3Options s3Options) {
        return new AmazonS3Client(
                new StaticCredentialsProvider(
                        new BasicAWSCredentials(s3Options.getAccessId(), s3Options.getSecretKey())));
    }

}
