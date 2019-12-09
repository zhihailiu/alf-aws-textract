# Alfresco & Amazon Textract Integration

## Run
`mvn clean install -DskipTests=true alfresco:run` or `./run.sh` 


## Test
1. Verify that the Spring Boot REST service backed by Amazon Textract to create searchable PDF is running. Its endpoint is configured in alfresco-global.properties
pdf.generator.url=http://localhost:9090/png

2. Upload a PNG file to Alfresco
$curl -uadmin:admin -X POST -F filedata=@test.png 'http://localhost:8080/alfresco/api/-default-/public/alfresco/versions/1/nodes/880a0f47-31b1-4101-b20b-4d325e54e8b1/children'

3. Create PDF rendition of the PNG file
$curl -uadmin:admin -X POST -d '
{
  "id":"pdf"
}
' 'http://localhost:8080/alfresco/api/-default-/public/alfresco/versions/1/nodes/82f9c749-4c0a-48b8-a8fe-1c4cba6fe7a0/renditions'