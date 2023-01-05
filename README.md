# A Kubernetes + Istio + Quarkus Demo

## Pre-requisites

1. JDK17 - If you have [sdkman](https://sdkman.io/) and have installed [temurin 17.0.5-tem](https://sdkman.io/jdks#tem) then you can just run `sdk env` (This project already has an sdk env initialisation config file) to activate `Temurin 17.0.5`.
2. [Docker Desktop](https://www.docker.com/products/docker-desktop/) (or) [Rancher Desktop](https://rancherdesktop.io/) (or) [Colima](https://github.com/abiosoft/colima)
3. [minikube](https://minikube.sigs.k8s.io/docs/start/) installed
4. [Istio](https://istio.io/latest/docs/setup/install/) installed
5. [grpCurl](https://github.com/fullstorydev/grpcurl) is installed (This is just for testing needs)
6. The command `docker ps` works fine (This is to just test if docker is up and running properly)
7. minikube add-ons enabled using the commands:

    a. `minikube addons enable dashboard`
    b. `minikube addons enable metrics-server`

8. [gRPC benchmarking and load testing tool](https://ghz.sh/) is installed.

## Steps to be followed after cloning this repository.

1. Start minikube using the command `minikube start --cpus 6 --memory 8192`
everytime you delete your deployment and trigger a fresh one so that the request reaches your k8s cluster)
2. Install istio on your k8s cluster via `istioctl install`
3. Install all the istio default addons via `kubectl apply -f ~/tools/istio/istio-1.16.1/samples/addons` (Here we are assuming that the istio installation directory is `~/tools/istio/istio-1.16.1`). This will help us view the [Kiali](https://kiali.io/), [Grafana](https://grafana.com/), [Prometheus](https://prometheus.io/), [Jaegar](https://www.jaegertracing.io/) dashboards.
4. Install the otel collector (Open Telemetry Collector) by running `kubectl apply -f deploy/otel-collector-config.yml` This will ensure that our distributed tracing works fine.
5. Create a namespace (This is where our apps will be deployed) via `kubectl create ns rationale-emotions`. For additional details read at the bottom.
6. Enable Envoy proxy auto injection via Istio: `kubectl label ns rationale-emotions istio-injection=enabled`
7. Setup minikube docker daemon for accessing our locally built image: `eval $(minikube -p minikube docker-env)`
8. Build the docker images using `./mvnw clean package -Dquarkus.container-image.build=true`
9. Once the images have been built now deploy the images into kubernetes using the shell script `./install-to-k8s.sh rationale-emotions` (Here `rationale-emotions` is our namespace)
10. Open up the tunnel in a new terminal using `minikube tunnel` (Remember to kill this and restart this, 
11. Now you can open up an RPC client such as grpcurl (or) BloomRPC and then interact with the service. Try accessing the end point `com.rationaleemotions.generated.DashboardService.dashBoardDetails` with the username as `rajnikanth`
12. Remember to run (8) in the same terminal wherein you ran (7)
13. If you would like to delete our app, run the shell script `./delete-from-k8s.sh rationale-emotions`
14. If you would like to generate the kubernetes manifest files (Yes, Quarkus lets you generate them too!!!) then you can do it using `./mvnw clean package`
15. To view the Jaegar dashboard you can run `istioctl dashboard jaeger` (In a new terminal because this should be running)


Here's an example of how a grpcurl invocation looks like

<details>
  <summary>grpCurl Example</summary>

```bash
➜  microservices git:(main) grpcurl -d '{"userName":"rajnikanth"}' --plaintext \
--import-path dashboard-app/src/main/proto \
--proto dashboard-app/src/main/proto/dashboard.proto \
localhost:10030 com.rationaleemotions.generated.DashboardService/dashBoardDetails
{
  "basicDetails": {
    "username": "rajnikanth",
    "fullName": {
      "firstName": "Sivaji",
      "lastName": "Rao"
    },
    "emailAddress": "sivaji.rao@india.com"
  },
  "favoriteMovies": [
    {
      "movieId": 31,
      "language": "tamil",
      "movieName": "b3d03050-76d7-40bb-a40b-536061bf3f6e",
      "durationInMins": 73
    },
    {
      "movieId": 32,
      "language": "tamil",
      "movieName": "e90bb8b8-6269-476e-b233-f9015362ec0f",
      "durationInMins": 51
    }
  ],
  "recentlyWatchedMovies": [
    {
      "movieId": 301,
      "language": "tamil",
      "movieName": "e578fb45-4871-4ad6-984c-c7312e690800",
      "durationInMins": 18
    },
    {
      "movieId": 302,
      "language": "tamil",
      "movieName": "9b9edd74-26de-45fe-a757-277a486e09f3",
      "durationInMins": 1
    }
  ]
}
```
</details>

### Shell scripts

There are some useful shell scripts created.

1. `install-to-k8s.sh` - Allows you to install our demo microservices to a namespace of your choice. Invoke using `./install-to-k8s.sh my-fancy-namespace`
2. `delete-from-k8s.sh` - Deletes whatever demo microservices you installed via (1). Invoke using `./delete-from-k8s.sh my-fancy-namespace`
3. `fire.sh` - Generates one request against our demo dashboard.
4. `generate_load.sh` - Generates a load for 1 minute against our demo dashboard.


### Some fancy commands

1. `istioctl dashboard jaeger` - Opens up the Jaegar UI
2. `istioctl dashboard` - Tells you what other things you can open up.
3. `minikube dashboard` - Opens up the Minikube dashboard which shows the internals of the k8s cluster.


#### Namespaces

There are essentially two ways in which one can deal with namespaces viz.,

1. When applying a manifest file (You will find the apply commands in the `install-to-k8s.sh` script) using the `-n <namespaceNameGoesHere>` option in the `kubectl apply` command.
2. In the Quarkus application configuration file `src/main/resourcs/application.properties` via the parameter `quarkus.kubernetes.namespace`

In this demo project, I have chosen to go with (1).


#### References

* [What is OpenTelemetry? A Straightforward Guide](https://www.aspecto.io/blog/what-is-opentelemetry-the-infinitive-guide/)
* [Distributed Tracing with OpenTelemetry Collector on Kubernetes – Part 1](https://www.aspecto.io/blog/distributed-tracing-with-opentelemetry-collector-on-kubernetes/)
* You can find additional instructions in [this gist of mine](https://gist.github.com/krmahadevan/f67ba986d153c05ca899f9eb6649de5d)
