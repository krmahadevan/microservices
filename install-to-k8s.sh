kubectl apply -f dashboard-app/target/kubernetes/kubernetes.yml -n "$1"
kubectl apply -f movies-app/target/kubernetes/kubernetes.yml -n "$1"
kubectl apply -f user-app/target/kubernetes/kubernetes.yml -n "$1"
