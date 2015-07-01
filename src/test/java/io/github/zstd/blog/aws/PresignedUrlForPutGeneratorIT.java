package io.github.zstd.blog.aws;


import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeNotNull;

public class PresignedUrlForPutGeneratorIT {

    private static final Logger LOG = LoggerFactory.getLogger(PresignedUrlForPutGeneratorIT.class);

    private S3Options s3Options;
    private PresignedUrlForPutGenerator generator;
    private CloseableHttpClient httpclient = HttpClients.createDefault();
    private static final String S3_URL_TEMPLATE = "http://%s.s3.amazonaws.com/%s";

    @Before
    public void setUp() throws Exception {
        s3Options = parseS3Options();
        generator = new PresignedUrlForPutGenerator(s3Options);
    }

    private S3Options parseS3Options() {
        Properties properties = new Properties();
        S3Options result = null;
        try (InputStream is = this.getClass().getResourceAsStream("/aws.properties")){
            properties.load(is);
            result = new S3Options(properties.getProperty("bucket"),
                                        properties.getProperty("access-id"),
                                        properties.getProperty("client-secret"));
            LOG.info("Loaded options: {}", result);
        } catch (IOException e) {
            LOG.error("Failed to load aws.properties. Tests will be skipped.");
        }
        return result;
    }

    @Test
    public void testPutResourceAsPublic() throws Exception {
        executePutGetTest(new GenerationParams("test-put/public.txt",true), HttpStatus.SC_OK);
    }

    public void executePutGetTest(GenerationParams params, int expectedStatusCode) throws Exception {
        assumeNotNull(s3Options);

        String presignedUrl = generatePresignedUrl(params);

        tryPut(presignedUrl, params);

        tryGet(generateDirectLink(params.getResourceKey()), expectedStatusCode);
    }

    private String generatePresignedUrl(GenerationParams generationParams) {
        String result = generator.generate(generationParams);
        LOG.info("presigned url: {}",result);
        return result;
    }

    private void tryPut(String presignedUrl, GenerationParams params) throws IOException {
        int putResult = executePut(presignedUrl, params);
        assertEquals(putResult, HttpStatus.SC_OK);
    }

    private void tryGet(String directUrl, int expectedStatusCode) throws IOException {
        int getResult = executeGet(directUrl);
        assertEquals(getResult, expectedStatusCode);
    }

    private String generateDirectLink(String resourceKey) {
        return String.format(S3_URL_TEMPLATE,s3Options.getBucket(),resourceKey);
    }

    private int executePut(String urlToPut, GenerationParams params) throws IOException {
        HttpPut put = new HttpPut(urlToPut);
        put.setEntity(new StringEntity("This is content to be put."));
        // the Content-Type of pre-signed request should be equal to the value used for presigned URL generation
        put.setHeader("Content-Type",params.getContentType());
        try (CloseableHttpResponse response = httpclient.execute(put)){
            LOG.info("executePut result: {}", EntityUtils.toString(response.getEntity()));
            return response.getStatusLine().getStatusCode();
        }
    }

    private int executeGet(String urlToGet) throws IOException {
        HttpGet put = new HttpGet(urlToGet);
        try (CloseableHttpResponse response = httpclient.execute(put)){
            LOG.info("executeGet result: {}", EntityUtils.toString(response.getEntity()));
            return response.getStatusLine().getStatusCode();
        }
    }

    @Test
    public void testPutResourceAsPrivate() throws Exception {
        executePutGetTest(new GenerationParams("test-put/private.txt",false), HttpStatus.SC_FORBIDDEN);
    }

}