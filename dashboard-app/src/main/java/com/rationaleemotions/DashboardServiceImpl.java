package com.rationaleemotions;

import com.rationaleemotions.generated.BasicDetails;
import com.rationaleemotions.generated.BasicDetailsRequest;
import com.rationaleemotions.generated.BasicMovieDetails;
import com.rationaleemotions.generated.DashboardRequest;
import com.rationaleemotions.generated.DashboardResponse;
import com.rationaleemotions.generated.DashboardResponse.Builder;
import com.rationaleemotions.generated.DashboardService;
import com.rationaleemotions.generated.MovieInformation;
import com.rationaleemotions.generated.MovieRequest;
import com.rationaleemotions.generated.MovieService;
import com.rationaleemotions.generated.PreferenceRequest;
import com.rationaleemotions.generated.Preferences;
import com.rationaleemotions.generated.UserService;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.grpc.GrpcService;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import java.util.List;

@GrpcService
public class DashboardServiceImpl implements DashboardService {

  @GrpcClient("user")
  UserService userService;

  @GrpcClient("movie")
  MovieService movieService;

  @Override
  public Uni<DashboardResponse> dashBoardDetails(DashboardRequest request) {
    Builder builder = DashboardResponse.newBuilder();
    return Uni.combine()
        .all()
        .unis(
            getUserBasicInfo(request),
            getUserPreferences(request.getUserName()))
        .asTuple()
        .onItem()
        .transformToUni(tuple -> {
          Preferences prefs = tuple.getItem2();
          return Uni.combine()
              .all()
              .unis(
                  getFavoriteMovies(prefs),
                  getRecentlyWatchedMovies(prefs)
              ).asTuple()
              .onItem()
              .transform(this::movieDetails)
              .onItem()
              .transform(each -> {
                each.getItem1().forEach(builder::addFavoriteMovies);
                each.getItem2().forEach(builder::addRecentlyWatchedMovies);
                return builder;
              })
              .onItem()
              .transform(a -> a.setBasicDetails(tuple.getItem1()).build());
        });
  }

  private Tuple2<List<BasicMovieDetails>, List<BasicMovieDetails>> movieDetails(
      Tuple2<MovieInformation, MovieInformation> movieTuple) {
    return Tuple2.of(
        movieTuple.getItem1().getMovieInfoList(),
        movieTuple.getItem2().getMovieInfoList()
    );
  }

  private Uni<MovieInformation> getFavoriteMovies(Preferences prefs) {
    List<Integer> movieIds = prefs.getMovies().getFavoriteMoviesList();
    return movieService.getMovieDetails(MovieRequest.newBuilder().addAllMovieId(movieIds).build());
  }

  private Uni<MovieInformation> getRecentlyWatchedMovies(Preferences prefs) {
    List<Integer> movieIds = prefs.getMovies().getRecentlyWatchedList();
    return movieService.getMovieDetails(MovieRequest.newBuilder().addAllMovieId(movieIds).build());
  }

  private Uni<Preferences> getUserPreferences(String userName) {
    return userService.preferences(
        PreferenceRequest.newBuilder()
            .setUsername(userName)
            .build());
  }

  private Uni<BasicDetails> getUserBasicInfo(DashboardRequest rqst) {
    BasicDetailsRequest basicDetailsRqst = BasicDetailsRequest.newBuilder()
        .setUsername(rqst.getUserName()).build();
    return userService.basicUserInfo(basicDetailsRqst);
  }
}
