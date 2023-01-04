# A Kubernetes + Istio + Quarkus Demo

## Pre-requisites

1. JDK17
2. Docker Desktop (or) Rancher Desktop (or) Colima [ These are all if You are on OSX]
3. minikube installed
4. Istio installed

## Steps to be followed after cloning this repository.

1. Start minikube using the command `minikube start --cpus 6 --memory 8192`
2. Open up the tunnel in a new terminal using `minikube tunnel`
3. Install istio on your k8s cluster via `istioctl install`
4. Create a namespace (This is where our apps will be deployed) via `kubectl create ns rationale-emotions`
5. Enable Envoy proxy auto injection via Istio: `kubectl label ns rationale-emotions istio-injection=enabled`
6. Setup minikube docker daemon for accessing our locally built image: `eval $(minikube -p minikube docker-env)`
7. Build the docker images using `./mvnw clean package -Dquarkus.container-image.build=true`
8. Once the images have been built now deploy the images into kubernetes using the shell script `./install-to-k8s.sh`
9. Now you can open up an RPC client such as grpcurl (or) BloomRPC and then interact with the service. Try accessing the end point `com.rationaleemotions.generated.DashboardService.dashBoardDetails` with the username as `rajnikanth`
10. Remember to run (10) in the same terminal wherein you ran (6)