# IUDX Vocabulary Server

## NOTE: Better readme and documentation to come soon

## Prerequisites 
1. Ensure you have mvn locally installed for building the backend
1. Ensure you have angular and npm locally installed for building the UI

## Instructions to run 

1. Modify the configuration options in `./config/vocserver.json`. This will need jks files for server TLS.
2. Modify src/app/appSettings.ts and add appropriate BASE_URL corresponding to your domain name.
3. Install ui dependencies and build
   ` cd ui/` 
   `npm install` 
   `ng build --deploy-url /static/` 
   
4. Turn on the mongodb docker 
For local
` docker-compose up -d db-local` 
or
For production
` docker-compose up -d db` 

5. From the project root folder 
For local
` mvn clean package -Dmaven.test.skip=true && java -jar target/vocserver-1.0-fat.jar -conf config/vocserver.json
`
For production
` mvn clean package -Dmaven.test.skip=true && docker-compose up -d server
`


## Making a jks 

1. Obtain PEM from certbot 
`sudo certbot certonly --manual --preferred-challenges dns -d demo.example.com`
2. Concat all pems into one file 
`sudo cat /etc/letsencrypt/life/demo.example.com/*.pem > fullcert.pem`
3. Convert to pkcs format 
` openssl pkcs12 -export -out fullcert.pkcs12 -in fullcert.pem`
4. Create new temporary keystore using JDK keytool, will prompt for password 
`keytool -genkey -keyalg RSA -alias vockeystore -keystore vockeystore.ks` 
`keytool -delete -alias vockeystore -keystore vockeystore.ks` 
5. Make JKS, will prompt for password 
`keytool -v -importkeystore -srckeystore fullcert.pkcs12 -destkeystore vockeystore.ks -deststoretype JKS`
6. Store JKS in config directory and edit the keyfile name and password entered in previous step


## Getting Tokens for insertion requests
1. Ensure a JKS per the above procedure is made for IUDX Resource Server Certificates (Class 1)
2. Ensure a Provider (Class 3 certificate) has given you access to <voc-server-url>/*
3. Request for a token using the auth server token request [api](http://auth.iudx.org.in/token.html) using your class 2 certificate
4. Use token as a header field "token": "<token>"
