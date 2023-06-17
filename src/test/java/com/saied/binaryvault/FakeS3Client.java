package com.saied.binaryvault;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@Primary
@Configuration
public class FakeS3Client implements S3Client {

    private static final String PATH = System.getProperty("user.home") + "/.temp/fakeS3Client";

    @Override
    public String serviceName() {
        return "FakeS3Client";
    }

    @Override
    public void close() {

    }

    @Override
    public PutObjectResponse putObject(PutObjectRequest objectRequest, RequestBody requestBody) {
        InputStream inputStream = requestBody.contentStreamProvider().newStream();
        try {
            byte[] bytes = IOUtils.toByteArray(inputStream);
            FileUtils.writeByteArrayToFile(
                new File(
                    buildObjectFullPath(
                        objectRequest.bucket(),
                        objectRequest.key()
                    )
                ),
                bytes
            );
            return PutObjectResponse.builder().build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseInputStream<GetObjectResponse> getObject(
        GetObjectRequest getObjectRequest
    ) throws AwsServiceException, SdkClientException {
        try {
            FileInputStream fileInputStream = new FileInputStream(
                buildObjectFullPath(
                    getObjectRequest.bucket(),
                    getObjectRequest.key()
                )
            );
            return new ResponseInputStream<>(
                GetObjectResponse.builder().build(),
                fileInputStream
            );
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildObjectFullPath(String bucketName, String key) {
        return PATH + "/" + bucketName + "/" + key;
    }

}
