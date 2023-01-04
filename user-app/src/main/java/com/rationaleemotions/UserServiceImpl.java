package com.rationaleemotions;

import com.rationaleemotions.generated.BasicDetails;
import com.rationaleemotions.generated.BasicDetailsRequest;
import com.rationaleemotions.generated.FullName;
import com.rationaleemotions.generated.MoviePrefs;
import com.rationaleemotions.generated.PreferenceRequest;
import com.rationaleemotions.generated.Preferences;
import com.rationaleemotions.generated.UserService;
import io.quarkus.grpc.GrpcService;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.event.Observes;

@SuppressWarnings("unused")
@GrpcService
public class UserServiceImpl implements UserService {

  private final Map<String, Pair<BasicDetails, Preferences>> cache = new HashMap<>();

  @Override
  public Uni<BasicDetails> basicUserInfo(BasicDetailsRequest request) {
    return Uni.createFrom()
        .item(
            () -> {
              if (cache.containsKey(request.getUsername())) {
                return cache.get(request.getUsername()).getLeft();
              }
              throw new IllegalArgumentException(request.getUsername() + " not found");
            }
        );
  }

  @Override
  public Uni<Preferences> preferences(PreferenceRequest request) {
    return Uni.createFrom()
        .item(
            () -> {
              if (cache.containsKey(request.getUsername())) {
                return cache.get(request.getUsername()).getRight();
              }
              throw new IllegalArgumentException(request.getUsername() + " not found");
            }
        );
  }

  void onApplicationStart(@Observes StartupEvent event) {
    BasicDetails basic = BasicDetails.newBuilder()
        .setUsername("hujackman")
        .setEmailAddress("hugh.jackman@hollywood.com")
        .setFullName(FullName.newBuilder().setFirstName("Hugh").setLastName("Jackman").build())
        .build();
    MoviePrefs moviePrefs = MoviePrefs.newBuilder()
        .addAllFavoriteMovies(Arrays.asList(11, 12))
        .addAllRecentlyWatched(Arrays.asList(101, 102))
        .build();
    Preferences prefs = Preferences.newBuilder()
        .setLanguage("english")
        .setMovies(moviePrefs)
        .build();
    cache.put(basic.getUsername(), new Pair<>(basic, prefs));

    basic = BasicDetails.newBuilder()
        .setUsername("systallone")
        .setEmailAddress("sylvester.stallone@hollywood.com")
        .setFullName(
            FullName.newBuilder().setFirstName("Sylvester").setLastName("Stallone").build())
        .build();
    moviePrefs = MoviePrefs.newBuilder()
        .addAllFavoriteMovies(Arrays.asList(21, 22))
        .addAllRecentlyWatched(Arrays.asList(201, 202))
        .build();
    prefs = Preferences.newBuilder()
        .setLanguage("english")
        .setMovies(moviePrefs)
        .build();
    cache.put(basic.getUsername(), new Pair<>(basic, prefs));

    basic = BasicDetails.newBuilder()
        .setUsername("rajnikanth")
        .setEmailAddress("sivaji.rao@india.com")
        .setFullName(FullName.newBuilder().setFirstName("Sivaji").setLastName("Rao").build())
        .build();
    moviePrefs = MoviePrefs.newBuilder()
        .addAllFavoriteMovies(Arrays.asList(31, 32))
        .addAllRecentlyWatched(Arrays.asList(301, 302))
        .build();
    prefs = Preferences.newBuilder()
        .setLanguage("tamil")
        .setMovies(moviePrefs)
        .build();
    cache.put(basic.getUsername(), new Pair<>(basic, prefs));
  }

}
