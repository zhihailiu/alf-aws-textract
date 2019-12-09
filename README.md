# Alfresco Platform JAR Module - SDK 3

To run use `mvn clean install -DskipTests=true alfresco:run` or `./run.sh` and verify that it 

 * Runs the embedded Tomcat + H2 DB 
 * Runs Alfresco Platform (Repository)
 * Runs Alfresco Solr4
 * Packages both as JAR and AMP assembly
 
 Try cloning it, change the port and play with `enableShare`, `enablePlatform` and `enableSolr`. 
 
 Protip: This module will work just fine as a Share module if the files are changed and 
 if the enablePlatform and enableSolr is disabled.
 
# Test
$curl -uadmin:admin -X POST -F filedata=@TeraThink1.png 'http://localhost:8080/alfresco/api/-default-/public/alfresco/versions/1/nodes/880a0f47-31b1-4101-b20b-4d325e54e8b1/children'

$curl -uadmin:admin -X POST -d '
{
  "id":"pdf"
}
' 'http://localhost:8080/alfresco/api/-default-/public/alfresco/versions/1/nodes/82f9c749-4c0a-48b8-a8fe-1c4cba6fe7a0/renditions'