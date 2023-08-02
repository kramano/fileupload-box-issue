## Steps to reproduce

Put your box_config.json (credentials) to src/main/resources

`cd fileupload-box-issue`

`./mvnw jetty:run`


`curl -X POST --location "http://localhost:8080/upload" \
-H "Content-Type: multipart/form-data; boundary=boundary" \
-F "file=@./src/main/resources/1-MB-DOC.doc;filename=1-MB-DOC.doc;type=*/*"`