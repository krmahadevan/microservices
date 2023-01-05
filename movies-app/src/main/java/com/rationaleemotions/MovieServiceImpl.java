package com.rationaleemotions;

import com.rationaleemotions.generated.BasicMovieDetails;
import com.rationaleemotions.generated.ListMovieRequest;
import com.rationaleemotions.generated.MovieInformation;
import com.rationaleemotions.generated.MovieRequest;
import com.rationaleemotions.generated.MovieService;
import io.quarkus.grpc.GrpcService;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.enterprise.event.Observes;
import org.jboss.logging.Logger;

@GrpcService
public class MovieServiceImpl implements MovieService {

  private List<BasicMovieDetails> cache;

  private static final Logger LOGGER = Logger.getLogger(MovieServiceImpl.class);

  @Override
  public Uni<MovieInformation> getMovieDetails(MovieRequest request) {
    LOGGER.info("Obtained a request for movies :" + request.getMovieIdList());
    List<BasicMovieDetails> found = cache.stream()
        .filter(each -> request.getMovieIdList().contains(each.getMovieId()))
        .peek(each -> LOGGER.info("Found the movie :" + each))
        .toList();

    return Uni.createFrom()
        .item(() -> MovieInformation.newBuilder()
            .addAllMovieInfo(found)
            .build());
  }

  @Override
  public Uni<MovieInformation> upcomingMovies(ListMovieRequest request) {
    LOGGER.info("Obtained a request for movies of language:" + request.getLanguage());
    return Uni.createFrom()
        .item(() -> MovieInformation.newBuilder()
            .addAllMovieInfo(cache.stream()
                .filter(each -> each.getLanguage().equalsIgnoreCase(request.getLanguage()))
                .peek(each -> LOGGER.info("Found the movie :" + each))
                .toList()
            ).build());
  }

  void onApplicationStart(@Observes StartupEvent event) {
    LOGGER.info("Initialising for the event :" + event);
    cache = Arrays.asList(
        englishMovie(11), englishMovie(12),
        englishMovie(21), englishMovie(22),
        englishMovie(101), englishMovie(102),
        englishMovie(201), englishMovie(202),
        tamilMovie(31), tamilMovie(32),
        tamilMovie(301), tamilMovie(302)
    );
  }

  private static final Random random = new Random();

  private static BasicMovieDetails englishMovie(int id) {
    return movie(id, "english");
  }

  private static BasicMovieDetails tamilMovie(int id) {
    return movie(id, "tamil");
  }

  private static BasicMovieDetails movie(int id, String language) {
    return BasicMovieDetails.newBuilder()
        .setMovieId(id)
        .setLanguage(language)
        .setDurationInMins(random.nextInt(100))
        .setMovieName(UUID.randomUUID().toString())
        .build();

  }
}
