package dc4;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dc4.arch.FreezeTime;
import dc4.db.DC4DB;
import dc4.db.SessionDB;
import dc4.model.Session;
import dc4.model.User;
import ox.Log;
import ox.util.Time;

public class AuthenticatorTest {

  private final SessionDB sessionDB = new SessionDB();

  private ModelWizard modelWizard;

  @BeforeEach
  public void setupCaller() {
    DC4DB.inMemoryDatabase();
    modelWizard = new ModelWizard();
  }

  /**
   * If a user arrives with a valid session, their expiry is moved forward 30 days.
   */
  @Test
  public void setExpiryForwardTest() {
    DC4ServerTest serverTest = new DC4ServerTest();
    Instant 
        jul11 = Time.timestamp(LocalDate.of(2021, 7, 11)), 
        jul14 = Time.timestamp(LocalDate.of(2021, 7, 14)),
        jul14P30 = jul14.plus(Duration.ofDays(30));

    FreezeTime.at(jul11);
    User user = modelWizard.createUser();
    Session session = modelWizard.createSession(user);

    FreezeTime.at(jul14);
    DC4TestRequest request = new DC4TestRequest("GET", "/").withCookie("token", session.token.toString());
    CapturedResponse response = serverTest.request(request);

    Session sessionAfter = sessionDB.get(session.id);
    Instant newExpiration = sessionAfter.expiration;
    Log.debug(newExpiration);
    assertTrue(newExpiration.equals(jul14P30));
  }
}
