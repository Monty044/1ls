# S3 CLI Application

This repository contains a simple Java console application that interacts with Amazon S3 using the AWS SDK for Java v2. The application allows you to:

- **List buckets** and choose which bucket to work with.
- **List objects** in the selected bucket.
- **Upload files** from your local machine to S3.
- **Download files** from S3 to your local machine.
- **Delete objects** from S3.
- **Search** for objects by substring.
- **Zip a local folder** and upload the ZIP.
- **Switch buckets** on demand.

## Prerequisites

- **Java 11 or higher** and **Maven** installed locally.
- AWS credentials configured (for example in `~/.aws/credentials`) with permissions to access S3 and upload artifacts if using CodeBuild.

## Running locally

Build the project and skip tests:

```
mvn -DskipTests package
```

This will produce a shaded JAR in the `target/` directory (for example `s3-cli-vg-1.0.0.jar`). Run the application with:

```
java -jar target/s3-cli-vg-1.0.0.jar
```

When the program starts, you will be asked to provide the AWS region (e.g., `eu-north-1`). After that you can select an S3 bucket and choose operations from the menu.

## CI/CD with CodeBuild

The included `buildspec.yml` automates packaging and uploads the generated JAR to an S3 bucket. To use it:

1. Create a CodeBuild project pointing to this repository.
2. In the project’s environment variables, set `ARTIFACTS_BUCKET` to the name of an S3 bucket where build artifacts should be uploaded.
3. Ensure the CodeBuild role has permission to put objects into that bucket.

During the build, Maven will compile and package the application and the post-build phase will copy the JAR to `s3://$ARTIFACTS_BUCKET/builds/s3-cli-vg-$COMMIT.jar`, where `COMMIT` is the commit SHA being built.

---

Feel free to extend this application or improve error handling as needed.
