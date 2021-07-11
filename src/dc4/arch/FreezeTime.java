package dc4.arch;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import java.time.Instant;
import java.time.LocalDate;

import ox.util.Time;

public class FreezeTime {

  public static LocalDate at(LocalDate date) {
    Time.timeWrapper = spy(Time.TimeWrapper.class);
    doReturn(date).when(Time.timeWrapper).now();
    doReturn(date.atStartOfDay()).when(Time.timeWrapper).nowLocalDateTime();
    doReturn(date.atStartOfDay(Time.CENTRAL).toInstant()).when(Time.timeWrapper).nowInstant();
    return date;
  }
  
  public static Instant at(Instant instant) {
    Time.timeWrapper = spy(Time.TimeWrapper.class);
    doReturn(Time.toDate(instant)).when(Time.timeWrapper).now();
    doReturn(Time.toDateTime(instant).toLocalDateTime()).when(Time.timeWrapper).nowLocalDateTime();
    doReturn(instant).when(Time.timeWrapper).nowInstant();
    return instant;
  }

}
