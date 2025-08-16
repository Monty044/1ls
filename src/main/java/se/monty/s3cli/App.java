package se.monty.s3cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String region = envOrAsk(in, "AWS_REGION", "Region (e.g. eu-north-1): ");
        S3Service s3 = new S3Service(region);

        String currentBucket = null;

        while (true) {
            System.out.println("\n==== S3 CLI (VG) ====");
            System.out.println("Current bucket: " + (currentBucket == null ? "<none>" : currentBucket));
            System.out.println("1) Choose bucket");
            System.out.println("2) List objects");
            System.out.println("3) Upload file");
            System.out.println("4) Download file");
            System.out.println("5) Delete object");
            System.out.println("6) Search objects (contains)");
            System.out.println("7) Upload FOLDER as .zip");
            System.out.println("0) Exit");
            System.out.print("Select: ");
            String choice = in.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> currentBucket = chooseBucket(in, s3);

                    case "2" -> {
                        ensureBucket(currentBucket);
                        s3.listObjects(currentBucket);
                    }

                    case "3" -> {
                        ensureBucket(currentBucket);
                        System.out.print("Local file path: ");
                        Path p = Paths.get(in.nextLine().trim());
                        System.out.print("Key in S3 (leave blank to use filename): ");
                        String key = in.nextLine().trim();
                        if (key.isBlank()) key = p.getFileName().toString();
                        s3.uploadFile(currentBucket, p, key);
                    }

                    case "4" -> {
                        ensureBucket(currentBucket);
                        System.out.print("Key to download: ");
                        String key = in.nextLine().trim();
                        System.out.print("Download to local path: ");
                        Path out = Paths.get(in.nextLine().trim());
                        s3.downloadFile(currentBucket, key, out);
                    }

                    case "5" -> {
                        ensureBucket(currentBucket);
                        System.out.print("Key to delete: ");
                        String key = in.nextLine().trim();
                        s3.deleteObject(currentBucket, key);
                    }

                    case "6" -> {
                        ensureBucket(currentBucket);
                        System.out.print("Search text (case-sensitive contains): ");
                        String q = in.nextLine().trim();
                        s3.searchObjects(currentBucket, q);
                    }

                    case "7" -> {
                        ensureBucket(currentBucket);
                        System.out.print("Folder path to zip & upload: ");
                        Path folder = Paths.get(in.nextLine().trim());
                        System.out.print("Zip name/key (e.g. backup-2025-08-16.zip): ");
                        String key = in.nextLine().trim();
                        s3.zipAndUploadFolder(currentBucket, folder, key);
                    }

                    case "0" -> {
                        System.out.println("Bye.");
                        s3.close();
                        return;
                    }

                    default -> System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.err.println("ERROR: " + e.getMessage());
            }
        }
    }

    static String chooseBucket(Scanner in, S3Service s3) {
        var buckets = s3.listBuckets();
        if (buckets.isEmpty()) {
            System.out.println("No buckets available in your account/region.");
            return null;
        }
        for (int i = 0; i < buckets.size(); i++) {
            System.out.println((i + 1) + ") " + buckets.get(i));
        }
        System.out.print("Pick bucket #: ");
        int idx = Integer.parseInt(in.nextLine().trim()) - 1;
        if (idx < 0 || idx >= buckets.size()) {
            System.out.println("Invalid choice.");
            return null;
        }
        return buckets.get(idx);
    }

    static String envOrAsk(Scanner in, String env, String prompt) {
        String v = System.getenv(env);
        if (v != null && !v.isBlank()) return v;
        System.out.print(prompt);
        return in.nextLine().trim();
    }

    static void ensureBucket(String bucket) {
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException("Choose a bucket first (option 1).");
        }
    }
}
