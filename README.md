# IUDX Vocabulary Server


## Instructions to run 

1. Modify the configuration options in ./config/vocserver.json

2. From the project root folder 
` mvn clean package -Dmaven.test.skip=true && java -jar target/vocserver-1.0-fat.jar -conf config/vocserver.json
`

## Making a jks 

1. Obtain PEM from certbot 
`sudo certbot certonly --manual --preferred-challenges dns -d demo.example.com`
2. Concat all pems into one file 
`sudo cat /etc/letsencrypt/life/demo.example.com/*.pem > fullcert.pem`
3. Convert to pkcs format 
` penssl pkcs12 -export -out fullcert.pkcs12 -in fullcert.pem`
4. Create new temporary keystore using JDK keytool, will prompt for password 
`keytool -genkey -keyalg RSA -alias vockeystore -keystore vockeystore.ks` 
`keytool -delete -alias vockeystore -keystore vockeystore.ks` 
5. Make JKS, will prompt for password 
`keytool -v -importkeystore -srckeystore fullcert.pkcs12 -destkeystore vockeystore.ks -deststoretype JKS`
6. Store JKS in config directory and edit the keyfile name and password entered in previous step
