import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class testClass {

    @Test
    public void test() {
        LocalDateTime time = LocalDateTime.of(2021, 11, 12, 02, 0, 0);
        ZonedDateTime temp = time.atZone(ZoneId.of("UTC"));
        System.out.println(temp.toInstant().toEpochMilli());
    }
}
