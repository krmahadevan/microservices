package com.rationaleemotions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;

import com.rationaleemotions.generated.BasicDetails;
import com.rationaleemotions.generated.BasicDetailsRequest;
import com.rationaleemotions.generated.UserService;
import io.grpc.StatusRuntimeException;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.test.junit.QuarkusTest;
import java.time.Duration;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class UserServiceImplTest {

  // This annotation can be used ONLY if you have your proto buf files available as part of
  // src/main/proto. Else it will throw an exception.
  // More details are available in https://github.com/quarkusio/quarkus/issues/23277
  @GrpcClient
  UserService userService;

  @Test
  public void testGetValidUser() {
    BasicDetails response = userService.basicUserInfo(
            BasicDetailsRequest.newBuilder().setUsername("rajnikanth").build()
        )
        .await()
        .atMost(Duration.ofSeconds(1));
    assertEquals("sivaji.rao@india.com", response.getEmailAddress());
  }

  @Test
  public void testInvalidUser() {
    assertThrowsExactly(StatusRuntimeException.class,
        () -> userService.basicUserInfo(
                BasicDetailsRequest.newBuilder().setUsername("sachin").build())
            .await()
            .atMost(Duration.ofSeconds(1)));
  }
}
