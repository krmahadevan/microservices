grpcurl -d '{"userName":"rajnikanth"}' --plaintext \
--import-path dashboard-app/src/main/proto \
--proto dashboard-app/src/main/proto/dashboard.proto \
localhost:10030 com.rationaleemotions.generated.DashboardService/dashBoardDetails