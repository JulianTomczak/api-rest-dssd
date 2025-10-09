Crear db en PostgreSQL:

CREATE DATABASE curso;

Inicializar proyecto:

mvn clean install

mvn spring-boot:run

Acceso local: http://localhost:8080

Usuario de prueba:

INSERT INTO usuario (mail,name,"password","role") VALUES
	 ('test@example.com','Test','$2a$10$fLC4jqyE6vHlrgVMRxZDku.tXM8fA5Yy0BGT3v8ViZclF7vegDgJS','ADMIN');


Datos de login:

"username":"test@example.com",
"password":"Contraseña123"

Swagger:

http://localhost:8080/swagger-ui/index.html

http://localhost:8080/v3/api-docs

