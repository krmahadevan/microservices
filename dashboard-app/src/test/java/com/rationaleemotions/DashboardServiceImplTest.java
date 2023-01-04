package com.rationaleemotions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import com.rationaleemotions.generated.BasicDetails;
import com.rationaleemotions.generated.BasicMovieDetails;
import com.rationaleemotions.generated.DashboardRequest;
import com.rationaleemotions.generated.DashboardResponse;
import com.rationaleemotions.generated.DashboardService;
import com.rationaleemotions.generated.MovieInformation;
import com.rationaleemotions.generated.MoviePrefs;
import com.rationaleemotions.generated.MovieRequest;
import com.rationaleemotions.generated.MovieService;
import com.rationaleemotions.generated.Preferences;
import com.rationaleemotions.generated.UserService;
import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import io.smallrye.mutiny.Uni;
import java.time.Duration;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
public class DashboardServiceImplTest {

  @InjectMock
  @GrpcClient("user")
  UserService userService;

  @InjectMock
  @GrpcClient("movie")
  MovieService movieService;

  @GrpcClient
  DashboardService dashboardService;

  @Test
  void testGetDashBoardDetailsForInvalidUser() {
    assertThrowsExactly(StatusRuntimeException.class,
        () -> {
          Mockito.when(userService.basicUserInfo(Mockito.any()))
              .thenThrow(new IllegalArgumentException("Invalid request"));
          DashboardRequest request = DashboardRequest.newBuilder()
              .setUserName("rajnikanth")
              .build();
          DashboardResponse response = dashboardService.dashBoardDetails(
                  request)
              .await()
              .atMost(Duration.ofSeconds(5));
        });
  }

  @Test
  void testGetDashBoardDetails() {
    setupUserServiceMocking();
    setupMovieServiceMocking();
    DashboardRequest request = DashboardRequest.newBuilder()
        .setUserName("rajnikanth")
        .build();
    DashboardResponse response = dashboardService.dashBoardDetails(
            request)
        .await()
        .atMost(Duration.ofSeconds(5));
    assertEquals("sivaji.rao@india.com", response.getBasicDetails().getEmailAddress());
    assertEquals("Kung-Fu Panda", response.getFavoriteMovies(0).getMovieName());
    assertEquals("Lucifer", response.getRecentlyWatchedMovies(0).getMovieName());
  }

  private void setupUserServiceMocking() {
    Mockito.when(userService.basicUserInfo(Mockito.any()))
        .thenReturn(
            Uni.createFrom().item(
                BasicDetails.newBuilder().setEmailAddress("sivaji.rao@india.com").build()
            )
        );

    Mockito.when(userService.preferences(Mockito.any()))
        .thenReturn(
            Uni.createFrom().item(
                Preferences.newBuilder()
                    .setMovies(
                        MoviePrefs.newBuilder()
                            .addAllFavoriteMovies(Collections.singletonList(1))
                            .addAllRecentlyWatched(Collections.singletonList(100))
                            .build()
                    )
                    .build()
            )
        );
  }

  private void setupMovieServiceMocking() {
    Mockito.when(
            movieService.getMovieDetails(
                MovieRequest.newBuilder().addMovieId(1).build()
            )
        )
        .thenReturn(
            Uni.createFrom().item(
                MovieInformation.newBuilder()
                    .addMovieInfo(BasicMovieDetails.newBuilder()
                        .setLanguage("english")
                        .setDurationInMins(100)
                        .setMovieId(1)
                        .setMovieName("Kung-Fu Panda")
                        .build())
                    .build()
            )
        );
    Mockito.when(
            movieService.getMovieDetails(
                MovieRequest.newBuilder().addMovieId(100).build()
            )
        )
        .thenReturn(
            Uni.createFrom().item(
                MovieInformation.newBuilder()
                    .addMovieInfo(BasicMovieDetails.newBuilder()
                        .setLanguage("malayalam")
                        .setDurationInMins(150)
                        .setMovieId(1)
                        .setMovieName("Lucifer")
                        .build())
                    .build()
            )

        );
  }


}
