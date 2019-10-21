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