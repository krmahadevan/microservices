ghz --insecure --import-paths dashboard-app/src/main/proto \
--proto dashboard-app/src/main/proto/dashboard.proto \
--call com.rationaleemotions.generated.DashboardService/dashBoardDetails \
-d '{"userName":"rajnikanth"}' localhost:10030 -z 1m
