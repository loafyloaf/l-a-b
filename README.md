
## Building individual microservices

### User service

```
mvn -pl :user-service -am package
docker build -t loyalty-user-service:1.0-SNAPSHOT user-service
```

### Event service
```
mvn -pl :event-service -am package -DskipTests
docker build -t loyalty-event-service:1.0-SNAPSHOT event-service
```


## Configuration

### Secrets

```
kubectl create secret generic loyalty-db-secret --from-literal=DB_SERVERNAME=48f106c1-94cb-4133-b99f-20991c91cb1a.bn2a2vgd01r3l0hfmvc0.databases.appdomain.cloud --from-literal=DB_PORTNUMBER=30389 --from-literal=DB_DATABASENAME=ibmclouddb --from-literal=DB_USER=ibm_cloud_0637cd24_8ac9_4dc7_b2d4_ebd080633f7f --from-literal=DB_PASSWORD=<password>
kubectl create secret generic loyalty-oidc-secret --from-literal=OIDC_JWKENDPOINTURL=https://us-south.appid.cloud.ibm.com/oauth/v4/3d17f53d-4600-4f32-bb2c-207f4e2f6060/publickeys --from-literal=OIDC_ISSUERIDENTIFIER=https://us-south.appid.cloud.ibm.com/oauth/v4/3d17f53d-4600-4f32-bb2c-207f4e2f6060 --from-literal=OIDC_AUDIENCES=38323bea-6959-432d-b564-76eb3f20544f
kubectl create secret generic loyalty-iam-secret --from-literal=IAM_APIKEY=<apikey> --from-literal=IAM_SERVICE_URL=https://iam.cloud.ibm.com/identity/token
kubectl create secret generic loyalty-appid-secret --from-literal=APPID_TENANTID=3d17f53d-4600-4f32-bb2c-207f4e2f6060 --from-literal=APPID_SERVICE_URL=https://us-south.appid.cloud.ibm.com
```


## Curl commands

### Users

```
curl -X POST -H "Content-Type: application/json" -d "{\"consentGiven\": \"true\"}" -k https://localhost:9443/loyalty/v1/users

curl -X GET -k https://localhost:9443/loyalty/v1/users/self

curl -X PUT -H "Content-Type: application/json" -d "{\"consentGiven\": \"false\"}" -k https://localhost:9443/loyalty/v1/users/self

curl -X DELETE -k https://localhost:9443/loyalty/v1/users/self
```


### User Events

```
curl -X POST -H "Content-Type: application/json" -d "{\"eventId\": \"871859e4-9fca-4bcf-adb5-e7d063d0747e\"}" -k https://localhost:9443/loyalty/v1/userEvents

curl -X GET -k https://localhost:9443/loyalty/v1/userEvents

curl -X GET -k https://localhost:9443/loyalty/v1/userEvents/info
```


### Events

```
curl -X POST -H "Content-Type: application/json" -d "{\"eventName\": \"Event name\", \"pointValue\": 100}" -k https://localhost:9444/loyalty/v1/events

curl -X GET -k https://localhost:9444/loyalty/v1/events/{eventId}

curl -X PUT -H "Content-Type: application/json" -d "{\"eventName\": \"Event name\", \"pointValue\": 100}" -k https://localhost:9444/loyalty/v1/events/{eventId}

curl -X GET -k https://localhost:9444/loyalty/v1/events

curl -X GET -k "https://localhost:9444/loyalty/v1/events?id=&id=&id="

```