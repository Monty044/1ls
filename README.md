@"
# 1ls / s3-cli-vg

## Build
mvn clean package

## Run (local)
java -jar target/*-with-dependencies.jar

## CI/CD
- CodePipeline builds and runs `mvn test` (see `buildspec.yml`).
- Artifact is the shaded JAR in `target/`.
  "@ | Set-Content -Encoding UTF8 README.md
