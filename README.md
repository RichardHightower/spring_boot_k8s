# Spring Boot app that runs well in K8s
Spring Boot app that runs well in K8s.


### End points


#### Health

```sh 
curl http://localhost:8080/manage/health | jq .
``` 

#### Health Output 
```json 
{
  "status": "UP"
}
```

#### Mark node unhealthy 

```sh

$ curl -X POST http://localhost:8080/health/conf
false
$ curl -X POST http://localhost:8080/health/conf
true
$ curl -X POST http://localhost:8080/health/conf
false
```

POSTing to `/health/conf` toggles the state of the server from healthy to unhealthy. 

#### Health (unhealthy)

```sh 
curl http://localhost:8080/manage/health | jq .
``` 

#### Health Output 
```json 
{
  "status": "DOWN"
}
```


____

## Info 


#### Info request
```sh 

$ curl http://localhost:8080/manage/info | jq .

```

#### Info json
```json
{
  "app": {
    "encoding": "UTF-8",
    "java": {
      "source": "11.0.4",
      "target": "11.0.4"
    }
  },
  "git": {
    "branch": "master",
    "commit": {
      "id": "4231c2f",
      "time": "2019-10-21T16:39:27Z"
    }
  },
  "build": {
    "artifact": "spring_boot_k8s",
    "name": "spring_boot_k8s",
    "time": "2019-10-21T20:59:59.626Z",
    "version": "0.1.0",
    "group": "org.rick"
  }
}
```

## Maven

### Run app from command line w/o docker or k8s

```sh
mvn clean package && java -jar target/spring_boot_k8s-0.4.0.jar
```

### Build docker image 

Added this to settings.xml under m2.

```sh 
$ pwd
/Users/richardhightower/.m2

$ cat settings.xml 
```

settings.xml 

```xml 
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
                      https://maven.apache.org/xsd/settings-1.0.0.xsd">

                      <localRepository>${user.home}/.m2/repository</localRepository>
                      <interactiveMode>true</interactiveMode>
                      <offline>false</offline>                      
    <pluginGroups>
        <pluginGroup>com.spotify</pluginGroup>
    </pluginGroups>
</settings>

```

```sh 
mvn dockerfile:build
mvn dockerfile:push
```


```shell script
mvn clean package
docker build -t cloudurable/spring_boot_k8s:0.4.0 .
docker push cloudurable/spring_boot_k8s:0.4.0

```
Not sure why, but dockerfile:push did not work. 
I had to run: 

```sh
docker push cloudurable/spring_boot_k8s:latest
```

To run this image
```sh 
docker run -p 8080:8080 -t cloudurable/spring_boot_k8s
```

To stop it

```sh

$ docker ps
CONTAINER ID        IMAGE                         COMMAND                  CREATED             STATUS              PORTS                    NAMES
2a98fc5e7fcf        cloudurable/spring_boot_k8s   "java -cp app:app/li…"   40 seconds ago      Up 39 seconds       0.0.0.0:8080->8080/tcp   happy_curie

$ docker stop 2a98fc5e7fcf
2a98fc5e7fcf

```

To remove it 

```sh

$ docker ps -a
CONTAINER ID        IMAGE                         COMMAND                  CREATED             STATUS                            PORTS               NAMES
2a98fc5e7fcf        cloudurable/spring_boot_k8s   "java -cp app:app/li…"   2 minutes ago       Exited (143) About a minute ago                       happy_curie

$ docker rm 2a98fc5e7fcf
2a98fc5e7fcf


$ docker images
REPOSITORY                    TAG                 IMAGE ID            CREATED             SIZE
cloudurable/spring_boot_k8s   latest              2fffbd132708        6 hours ago         509MB
openjdk                       latest              8a8b42cf3239        6 days ago          490MB

$ docker rmi 2fffbd132708
Untagged: cloudurable/spring_boot_k8s:latest
Untagged: cloudurable/spring_boot_k8s@sha256:241737200e8e1c947cd79fec6bbc01bbe0ba5186d91951e4327760010922625d
Deleted: sha256:2fffbd1327080b588816a66905efd281ac6bb3d1f7b62514f261162325fb5edd
Deleted: sha256:0462f57970e05555714a0eec37679eeb1fd5568198a60180e38cb89695bcf461
Deleted: sha256:fa0a91186827062f4d662a45166a06a694efa67f8291f6af31f520980ff84e2d
Deleted: sha256:7438639e430673e23629375df74798bef42e301db93e6a18d7ec8b57718c9fe7

```

## Using Helm and K8s


### Create helm files
```sh 
$ cd src 
$ mkdir helm
$ cd helm 
$ helm create hello
```

### Install Spring Boot app into K8s using  helm first edit src/helm/hello/values.yaml
```yaml 
# Default values for hello.
# This is a YAML-formatted file.
# Declare variables to be passed into your templates.

replicaCount: 2

image:
  repository: cloudurable/spring_boot_k8s
  tag: latest
  pullPolicy: IfNotPresent

imagePullSecrets: []
nameOverride: ""
fullnameOverride: ""

service:
  type: ClusterIP
  port: 8080

ingress:
  enabled: false
  annotations: {}
    # kubernetes.io/ingress.class: nginx
    # kubernetes.io/tls-acme: "true"
  hosts:
    - host: chart-example.local
      paths: []

  tls: []
  #  - secretName: chart-example-tls
  #    hosts:
  #      - chart-example.local

resources: {}
  # We usually recommend not to specify default resources and to leave this as a conscious
  # choice for the user. This also increases chances charts run on environments with little
  # resources, such as Minikube. If you do want to specify resources, uncomment the following
  # lines, adjust them as necessary, and remove the curly braces after 'resources:'.
  # limits:
  #   cpu: 100m
  #   memory: 128Mi
  # requests:
  #   cpu: 100m
  #   memory: 128Mi

nodeSelector: {}

tolerations: []

affinity: {}

```



### Install the app with helm install

```sh

cd src/helm/

pwd
../spring_boot_k8s/src/helm

helm install --name hello ./hello

```

### Output of install

```sh

NAME:   hello
LAST DEPLOYED: Mon Oct 21 23:43:02 2019
NAMESPACE: default
STATUS: DEPLOYED

RESOURCES:
==> v1/Deployment
NAME   READY  UP-TO-DATE  AVAILABLE  AGE
hello  0/2    2           0          1s

==> v1/Pod(related)
NAME                    READY  STATUS             RESTARTS  AGE
hello-8466464cfb-bw8c7  0/1    ContainerCreating  0         1s
hello-8466464cfb-tvw9r  0/1    ContainerCreating  0         1s

==> v1/Service
NAME   TYPE       CLUSTER-IP    EXTERNAL-IP  PORT(S)  AGE
hello  ClusterIP  10.97.246.25  <none>       80/TCP   1s


NOTES:
1. Get the application URL by running these commands:
  export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=hello,app.kubernetes.io/instance=hello" -o jsonpath="{.items[0].metadata.name}")
  echo "Visit http://127.0.0.1:8080 to use your application"
  kubectl port-forward $POD_NAME 8080:80
```

See if/when it is running

```sh 

$ kubectl get pods
NAME                                                         READY   STATUS    RESTARTS   AGE
hello-8466464cfb-bw8c7                                       0/1     Running   0          84s
hello-8466464cfb-tvw9r                                       0/1     Running   0          84s

```

Forward the ports from localhost 8080 to 80 in the k8s cluster. 

```sh 
export POD_NAME=$(kubectl get pods --namespace default -l "app.kubernetes.io/name=hello,app.kubernetes.io/instance=hello" -o jsonpath="{.items[0].metadata.name}")
kubectl port-forward $POD_NAME 8080:8080
```

Now test it


## Run health checks and app

Reinstall 

```shell script
helm del --purge hello
helm install --name hello ./hello
export NODE_PORT=$(kubectl get --namespace default -o jsonpath="{.spec.ports[0].nodePort}" services hello)
export NODE_IP=$(kubectl get nodes --namespace default -o jsonpath="{.items[0].status.addresses[0].address}")
echo http://$NODE_IP:$NODE_PORT
curl http://$NODE_IP:$NODE_PORT ; echo " "

```

```sh
 curl  http://$NODE_IP:$NODE_PORT  ; echo " "
```

```sh
 curl  http://$NODE_IP:$NODE_PORT/manage/health | jq . ; echo " "
```

```sh 
 curl -X POST http://$NODE_IP:$NODE_PORT/health/conf ; echo " "
 curl -X POST http://$NODE_IP:$NODE_PORT/health/conf ; echo " "
```

```sh

kubectl describe pod hello-5fcf86b7f6-9skg5

```