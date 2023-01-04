package com.rationaleemotions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.rationaleemotions.generated.ListMovieRequest;
import com.rationaleemotions.generated.MovieInformation;
import com.rationaleemotions.generated.MovieRequest;
import com.rationaleemotions.generated.MovieService;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Duration;
import org.junit.jupiter.api.Test;

// Our Movie service is just using in-memory data. So we don't need to be mocking anything
// and can directly interact with it.
@QuarkusTest
public class MovieServiceImplTest {

  @GrpcClient
  MovieService movieService;

  @Test
  public void testGetMovieDetailsValidScenario() {
    MovieInformation response = movieService.getMovieDetails(
        MovieRequest.newBuilder()
            .addMovieId(11)
            .build()
    ).await().atMost(Duration.ofSeconds(1));
    assertEquals(1, response.getMovieInfoCount());
    assertEquals("english", response.getMovieInfo(0).getLanguage());
  }

  @Test
  public void testGetMovieDetailsInvalidScenario() {
    MovieInformation response = movieService.getMovieDetails(
        MovieRequest.newBuilder()
            .addMovieId(99)
            .build()
    ).await().atMost(Duration.ofSeconds(1));
    assertEquals(0, response.getMovieInfoCount());
  }

  @Test
  public void testUpcomingMoviesValidScenario() {
    MovieInformation response = movieService.upcomingMovies(
        ListMovieRequest.newBuilder()
            .setLanguage("english")
            .build()
    ).await().atMost(Duration.ofSeconds(1));
    assertEquals(8, response.getMovieInfoCount());
  }

  @Test
  public void testUpcomingMoviesInvalidScenario() {
    MovieInformation response = movieService.upcomingMovies(
        ListMovieRequest.newBuilder()
            .setLanguage("french")
            .build()
    ).await().atMost(Duration.ofSeconds(1));
    assertEquals(0, response.getMovieInfoCount());
  }
}
