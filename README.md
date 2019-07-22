# Scalable Architecture

I have taken this problem as designing a scalable, asynchronous architecture, which can be scaled to execute unit of problem easily in future. This problem is Kanapsack in this case. But can be replaces by any other problem easily.

## Ideal Architecture

I planed to design the architecture as below , but I have to take shortcut due to lack of time and technical issues I faces with Cassandra and RabbitMq

![Ideal Architecture](/image/idealArchitecture.png)

This Architecture has below elements

### task-service
- Rest service to take User requests
- Connected to Task Db for persistence. This is only point of contact to db
- Publish task to Task Request Queue, after task is persisted
- Consume tasks from Task Response Queue and update the status in db
- There can be multiple instance of task-service and can be loadbalanced

### Database
- Ideally a scalable, distributed db like cassandra

### Task Request Queue
- Message Broker queue for example RabbitMQ queue
- On this queue Task as published to be picked by knapsack-service consumers
- All knapsack-service consumers are connected to it. But one message only picked by single consumer

### Task Response Queue
- Message Broker queue for example RabbitMQ queue
- On this queue task update events are published by knapsack-service instances
- All task-service instances are connected to it. But one message only picked by single task-service instances

### knapsack-service
- Consume the from Task Request Queue
- Process the task asynchronous and publish the task update event to Task Response Queue
- Do not interact with db directly

#### Request Flow
1. Request posted to task-service
2. task-service persist the task with `submitted` status and publish the task on Task Request Queue
3. knapsack-service pick the task and publish task event with updated status `started` on Task Response Queue
4. task-service pick up the task from Task Response Queue and update the task in db
5. knapsack-service process the task publish task event with updated status `completed` and solution on Task Response Queue
6. task-service pick up the task from Task Response Queue and update the task in db

## Implemented Architecture

I had technical issues with Cassandra and RabbitMq thus I had to trim the architecture a bit. But this architecture can still work as v1 for Ideal Architecture.

![Ideal Architecture](/image/implementedArchitecture.png)

This Architecture has below elements

### task-service
- Rest service to take User requests
- Connected to Task Db for persistence. 
- Publish task to Task Request, after task is persisted
- There can be multiple instance of task-service and can be load-balanced

### Database
- Ideally a scalable, distributed db like cassandra
- I have used MongoDb here, We can use sharding here to make it distributed

### Task Request
- I have used Radis Pub/Sub here
- On this queue Task as published to be picked by knapsack-service consumers
- All knapsack-service consumers are connected to it. And due to Pub/Sub all the instances would receive the message. This should be fixed 


### knapsack-service
- Consume the from Task Request Queue
- Process the task asynchronous and update the task in db
- Interact with db directly


#### Request Flow
1. Request posted to task-service
2. task-service persist the task with `submitted` status and publish the task on Task Queue.
3. knapsack-service pick the task and update the task status to `started` in db.
4. knapsack-service process the task and update the task status to `completed` in db along with solution.


## Features

### Asynchronous By Design
- task-service return DeferredResult thus Request Thread can handle other requests meanwhile
- RxJava is used to write code in reactive manner. Thus most of the code is non-blocking
- Reactive mongo driver is used to make db calls non blocking
- services communicate using message queue

### Scalable - Stateless service
- Stateless services are used so they can be auto scaled easily.


### Multi-threaded
- knapsack-service run the knapsack on different thread. This main thread is not blocked
- Computational Thread pool of RXjava is used

### Reusable Architecture
- We can reuse this architecture for any scheduling architecture
- Code for solving Knapsack is contained only in `KnapsackSolver` class. Thus any other implementation can be swapped with current one.

## Problems
### Knapsack solver
- I have used very basic implementation of Knapsack solver.
- Knapsack solver is not accurate and can be replaced by better implementation.

## Containerization
- All the architecture elements are containerized using docker
- Docker Compose is used for orchestration
- Spring Boot service do not have Dockerfile. They use `docker-maven-plugin` to create image. Thus it is important to build the image before `docker-compose up`

## Api Testing
- I have written API test using `rest-assured` .All tests are in `com.bhanuchaddha.architecture.taskservice.KnapsackApiTest` class.
- To test please run `KnapsackApiTest` after docker is up

## Startup
- Go to root directory
- Run below command `mvn clean install -Ddocker-build && docker-compose up -d`

# Test
Kindly use below postman document or curl commands in problem document to test the solution

[https://documenter.getpostman.com/view/3772012/SVSPnRrq?version=latest](https://documenter.getpostman.com/view/3772012/SVSPnRrq?version=latest)

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

### Service Improvements
- Better exception handling
- Better validations and validation framework
- Error codes
- Logging
- Unit Testing
- Failovers
- External Environment configuration
- Java Docs
- Use of Interfaces
- Swager UI