package se.monty.s3cli;

import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class S3Service implements AutoCloseable {
    private final S3Client s3;

    public S3Service(String region) {
        this.s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    public List<String> listBuckets() {
        var resp = s3.listBuckets();
        var list = new ArrayList<String>();
        resp.buckets().forEach(b -> list.add(b.name()));
        return list;
    }

    public void listObjects(String bucket) {
        ListObjectsV2Request req = ListObjectsV2Request.builder().bucket(bucket).build();
        ListObjectsV2Response resp;
        System.out.printf("%-40s %12s %s%n", "Key", "Size", "LastModified");
        do {
            resp = s3.listObjectsV2(req);
            resp.contents().forEach(o ->
                System.out.printf("%-40s %12d %s%n",
                        o.key(), o.size(),
                        o.lastModified().atZone(java.time.ZoneId.systemDefault())
                                .format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            );
            req = req.toBuilder().continuationToken(resp.nextContinuationToken()).build();
        } while (resp.isTruncated());
    }

    public void uploadFile(String bucket, Path file, String key) throws IOException {
        PutObjectRequest put = PutObjectRequest.builder().bucket(bucket).key(key).build();
        s3.putObject(put, RequestBody.fromFile(file));
        System.out.println("Uploaded: " + key);
    }

    public void downloadFile(String bucket, String key, Path out) {
        GetObjectRequest get = GetObjectRequest.builder().bucket(bucket).key(key).build();
        s3.getObject(get, out);
        System.out.println("Downloaded to: " + out);
    }

    public void deleteObject(String bucket, String key) {
        DeleteObjectRequest del = DeleteObjectRequest.builder().bucket(bucket).key(key).build();
        s3.deleteObject(del);
        System.out.println("Deleted: " + key);
    }

    public void searchObjects(String bucket, String contains) {
        ListObjectsV2Request req = ListObjectsV2Request.builder().bucket(bucket).build();
        ListObjectsV2Response resp;
        System.out.println("Matches:");
        do {
            resp = s3.listObjectsV2(req);
            resp.contents().stream()
                .filter(o -> o.key().contains(contains))
                .forEach(o -> System.out.println(" - " + o.key()));
            req = req.toBuilder().continuationToken(resp.nextContinuationToken()).build();
        } while (resp.isTruncated());
    }

    public void zipAndUploadFolder(String bucket, Path folder, String key) throws IOException {
        Path tmpZip = java.nio.file.Files.createTempFile("s3cli-", ".zip");
        ZipUtils.zipFolder(folder, tmpZip);
        uploadFile(bucket, tmpZip, key);
        java.nio.file.Files.deleteIfExists(tmpZip);
    }

    @Override public void close() { s3.close(); }
}
