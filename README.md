# A Kubernetes + Istio + Quarkus Demo

## Pre-requisites

1. JDK17
2. Docker Desktop (or) Rancher Desktop (or) Colima [ These are all if You are on OSX]
3. minikube installed
4. Istio installed

## Steps to be followed after cloning this repository.

1. Start minikube using the command `minikube start --cpus 6 --memory 8192`
2. Enable add-ons on minikube using `minikube addons enable ingress`
3. Open up the tunnel in a new terminal using `minikube tunnel`
4. Install istio on your k8s cluster via `istioctl install`
5. Create a namespace (This is where our apps will be deployed) via `kubectl create ns rationale-emotions`
6. Enable Envoy proxy auto injection via Istio: `kubectl label ns rationale-emotions istio-injection=enabled`
7. Setup minikube docker daemon for accessing our locally built image: `eval $(minikube -p minikube docker-env)`
8. Build the docker images using `./mvnw clean package -Dquarkus.container-image.build=true`