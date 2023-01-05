kubectl delete -f dashboard-app/target/kubernetes/kubernetes.yml -n "$1"
kubectl delete -f movies-app/target/kubernetes/kubernetes.yml -n "$1"
kubectl delete -f user-app/target/kubernetes/kubernetes.yml -n "$1"
