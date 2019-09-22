# Scalable Architecture

I have understood this problem to designing a scalable, asynchronous architecture, which can be scaled easily to execute high volume of longer running tasks. 
The task is Kanapsack in this case. But can be replaces by any other problem easily if needed. 


## Ideal Architecture

I planed to design the architecture as below, but I had to take shortcut due to lack of time. I spent around 8 hours in total on this assignment.
But quite some time was wasted on `Cassandra` and `RabbitMQ` container setup. At the end I decided to not use these and used MongoDB and Redis instead.

![Ideal Architecture](/image/idealArchitecture.png)

This Architecture has below elements

### task-service
- Rest service to handle User requests.
- Connected to Task Db for persistence. This is only point of contact to db.
- Publish task to Task Request Queue, after task is persisted.
- Consumes tasks from Task Response Queue and update the status in db.
- There can be multiple instance of task-service and can be loadbalanced.

### Database
- Ideally a scalable, distributed db like cassandra

### Task Request Queue
- Message Broker queue for example RabbitMQ queue
- On this queue tasks are published. These tasks are then picked by knapsack-service instances
- All knapsack-service instances are connected to it. But single message is picked only by one knapsack-service instance

### Task Response Queue
- Message Broker queue for example RabbitMQ queue.
- On this queue, task update events are published by knapsack-service instances.
- All task-service instances are connected to it. But one message is picked by only single task-service instance.

### knapsack-service
- Consumes task from Task Request Queue
- Process the task asynchronously and publish the task update event to Task Response Queue
- Do not interact with db directly

#### Request Flow
1. Request is posted to task-service
2. task-service persist the task with `submitted` status and publish the task on Task Request Queue
3. knapsack-service picks the task and publish 'task update event' with updated status `started` on Task Response Queue
4. task-service picks up the task from Task Response Queue and update the task in db
5. knapsack-service process the task and publish the 'task update event' on Task Response Queue. Now 'task update event' has status `completed` and solution as key
6. task-service pick up the task from Task Response Queue and update the task in db

## Implemented Architecture

I had technical issues with Cassandra and RabbitMq thus I had to trim the architecture a bit. But this architecture can still work as v1 for Ideal Architecture.

![Ideal Architecture](/image/implementedArchitecture.png)

This Architecture has below elements

### task-service
- Rest service to handle User requests
- Connected to Task Db for persistence. 
- Publish task to Task Request, after task is persisted
- There can be multiple instance of task-service and can be load-balanced

### Database
- Ideally a scalable, distributed db like cassandra
- I have used MongoDb here, We can use sharding here to make it distributed

### Task Queue
- I have used Radis Pub/Sub here
- On this queue Task are published to be picked by knapsack-service consumers
- All knapsack-service consumers are connected to it. Due to use of redis Pub/Sub, all the instances of knapsack-service would receive the message. 
    This is incorrect behaviour and should be fixed using any other Message Queue.


### knapsack-service
- Consumes the task from Task Queue
- Process the task asynchronously and update the task in db
- Interact with db directly


#### Request Flow
1. Request posted to task-service
2. task-service persist the task with `submitted` status and publish the task on Task Queue.
3. knapsack-service pick the task and update the task status to `started` in db.
4. knapsack-service process the task and update the task status to `completed` in db along with solution.

## Technology Stack
- Java
- Spring Boot
- RxJava
- Mongo Db
- Redis Pub/Sub
- Lombock
- Rest Assured
- Docker



## Features

### Asynchronous By Design
- task-service return DeferredResult, thus used Request Thread can handle other requests meanwhile.
- RxJava is used to write code in reactive manner. Thus most of the code is non-blocking.
- Reactive mongo driver is used to make db calls non-blocking.
- services communicate using message queues.

### Scalable - Stateless service
- Stateless services are used so they can be auto scaled easily.


### Multi-threaded
- knapsack-service run the knapsack solver on the different thread. Thus main thread is not blocked.
- Computational Thread pool of RXjava is used.

```
    public Single<Task> solve(Task task){
        return Single.just(task)
                .observeOn(Schedulers.computation()) // does the calculation on computational thread
                .map(t -> t.updateSolution(knapsackSolver.solve(task.getProblem())))
```

### Reusable Architecture
- We can reuse this architecture for any scheduling architecture
- Code for solving Knapsack is contained only in `KnapsackSolver` class. Thus any other implementation can be swapped with current one.

## Problems
### Knapsack solver
- I have used very basic implementation of Knapsack solver.
- Knapsack solver is not perfect and can be replaced by better implementation.

## Containerization
- All the architecture elements are containerized using docker
- Docker Compose is used for orchestration
- Spring Boot service do not have Dockerfile. They use `docker-maven-plugin` to create image. 
    Thus it is important to build the image before `docker-compose up`. Which can be done using `mvn clean install -Ddocker-build`.

## Api Testing
- I have written API test using `rest-assured`. All tests are in `com.bhanuchaddha.architecture.taskservice.KnapsackApiTest` class.
- To test, please run `KnapsackApiTest` after docker is up.

## Startup
- Go to root directory
- Run below command `mvn clean install -Ddocker-build && docker-compose up -d`

# Test
Kindly use below postman document or curl commands in problem document to test the solution

[https://documenter.getpostman.com/view/3772012/SVSPnRrq?version=latest](https://documenter.getpostman.com/view/3772012/SVSPnRrq?version=latest)

__OR__

```bash
$ curl -XPOST -H 'Content-type: application/json' http://localhost:6543/knapsack \
   -d '{"problem": {"capacity": 60, "weights": [10, 20, 33], "values": [10, 3, 30]}}'
{"task": "nbd43jhb", "status": "submitted", "timestamps": {"submitted": 1505225308, "started": null, "completed": null}, "problem": {"capacity": 60, "weights": [10, 20, 33], "values": [10, 3, 30]}, "solution": {}}

$ curl -XGET -H http://localhost:6543/knapsack/nbd43jhb
{"task": "nbd43jhb", "status": "started", "timestamps": {"submitted": 1505225308, "started": 1505225342, "completed": null}, "problem": {"capacity": 60, "weights": [10, 20, 33], "values": [10, 3, 30]}, "solution": {}}

$ curl -XGET -H http://localhost:6543/knapsack/nbd43jhb
{"task": "nbd43jhb", "status": "completed", "timestamps": {"submitted": 1505225308, "started": 1505225342, "completed": 1505225398}, "problem": {"capacity": 60, "weights": [10, 20, 33], "values": [10, 3, 30]}, "solution": {"packed_items": [0, 2], "total_value": 40}
```


## Future Improvements
### Architecture Improvement
- Autoscaling
- Kubernets
- Authentication
- Caching

### Service Improvements
- Better exception handling
- Better validations and validation framework
- Error codes
- Logging
- Unit Testing
- Failovers
- Circuit breaker
- External Environment configuration
- Java Docs
- Use of Interfaces
- Swagger UI
- Service Contract
- Make generic architecture to handle more than one task



## Kubernets
### Create
```
kubectl create -f k8.yml
```

### Update
```
kubectl apply  -f k8.yml
```

### Kubernets Dashboard


#### Create User and get token
```
kubectl apply -f k8-dashboard/dashboard-adminuser.yaml

kubectl -n kubernetes-dashboard describe secret $(kubectl -n kubernetes-dashboard get secret | grep admin-user | awk '{print $1}')

```

#### Access dashboard
```
http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/#/login
```

## Kompose
Convert docker-compose file to kubernets configuration

### Install Kompose
```
Follow steps from below
https://github.com/kubernetes/kompose
```


### Convert docker-compose file to k8 menifest
```
~ kompose convert                                                                                                             ✔  939  12:08:18

INFO Kubernetes file "database-service.yaml" created
INFO Kubernetes file "kafka-service.yaml" created
INFO Kubernetes file "task-service-service.yaml" created
INFO Kubernetes file "zookeeper-service.yaml" created
INFO Kubernetes file "database-deployment.yaml" created
INFO Kubernetes file "database-claim0-persistentvolumeclaim.yaml" created
INFO Kubernetes file "kafka-deployment.yaml" created
INFO Kubernetes file "kafka-claim0-persistentvolumeclaim.yaml" created
INFO Kubernetes file "knapsack-service-deployment.yaml" created
INFO Kubernetes file "task-service-deployment.yaml" created
INFO Kubernetes file "zookeeper-deployment.yaml" created
```

### Start K8 artifacts 
```
All the files in a directory can be started at once
kubectl create -f k8-configs
```

## Troubleshoot
### Access kafka from outside docker
```
Docker for Mac (native)
Docker for Mac is particularly problematic because of networking limitations. The solution is as follows:

sudo ifconfig lo0 alias 10.200.10.1/24  # (where 10.200.10.1 is some unused IP address)
export DOCKER_HOST_IP=10.200.10.1
```

### Local docker registry 
```
docker run -d -p 5000:5000 --restart=always --name registry registry:2

docker tag knapsack-service:latest bhanuchaddha/knapsack-service:latest

docker push bhanuchaddha/knapsack-service:latest
```

## Reference
### Kafka
https://dev.to/thegroo/one-to-run-them-all-1mg6
[Kafka Listener Explained](https://rmoff.net/2018/08/02/kafka-listeners-explained/)