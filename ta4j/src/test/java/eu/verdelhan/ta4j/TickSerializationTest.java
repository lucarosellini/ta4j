package eu.verdelhan.ta4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by luca on 12/4/17.
 */
public class TickSerializationTest {
  
  ObjectMapper mapper;


  @Before
  public void setUp(){
    mapper = new ObjectMapper();
    mapper.registerModule(new JodaModule());
  }
  
  @Test
  public void testTickSerDeser() throws IOException {
    
    Period period = Period.minutes(5);
    DateTime endTime = DateTime.now(DateTimeZone.UTC);
    
    Tick t0 = new Tick(period, endTime);
    byte[] t0ser = mapper.writeValueAsBytes(t0);
    
    Tick t0deser = mapper.readValue(t0ser, Tick.class);
    Assert.assertEquals(period, t0deser.getTimePeriod());
    Assert.assertEquals(endTime, t0deser.getEndTime());
    Assert.assertEquals(endTime.minus(period), t0deser.getBeginTime());
    
    Assert.assertEquals(Decimal.ZERO, t0deser.getAmount());
    Assert.assertNull(t0deser.getClosePrice());
    Assert.assertNull(t0deser.getOpenPrice());
    Assert.assertNull(t0deser.getMaxPrice());
    Assert.assertNull(t0deser.getMinPrice());
    Assert.assertEquals(0, t0deser.getTrades());
    Assert.assertEquals(Decimal.ZERO, t0deser.getVolume());
    
    Tick t1 =  new Tick(period, endTime);
    t1.addTrade(endTime.plusSeconds(1), Decimal.valueOf("15.2"), Decimal.valueOf("1.21"));
    
    System.out.println( mapper.writeValueAsString(t1));
    byte[] t1ser = mapper.writeValueAsBytes(t1);
    Tick t1deser = mapper.readValue(t1ser, Tick.class);
    
    Assert.assertEquals(period, t1deser.getTimePeriod());
    Assert.assertEquals(endTime.plusSeconds(1), t1deser.getEndTime());
    Assert.assertEquals(endTime.plusSeconds(1).minus(period), t1deser.getBeginTime());

    Assert.assertEquals(Decimal.valueOf("15.2"), t1deser.getAmount());
    Assert.assertEquals(Decimal.valueOf("1.21"), t1deser.getClosePrice());
    Assert.assertEquals(Decimal.valueOf("1.21"), t1deser.getOpenPrice());
    Assert.assertEquals(Decimal.valueOf("1.21"), t1deser.getMaxPrice());
    Assert.assertEquals(Decimal.valueOf("1.21"), t1deser.getMinPrice());
    Assert.assertEquals(1, t1deser.getTrades());
    Assert.assertEquals(Decimal.valueOf("1.21").multipliedBy(Decimal.valueOf("15.2")), t1deser.getVolume());
  }
}
