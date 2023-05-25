package subway.policy.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.MockedStatic;
import subway.line.domain.Line;
import subway.line.domain.Section;
import subway.line.domain.Station;
import subway.line.domain.Stations;
import subway.policy.domain.SubwayFarePolicy;
import subway.route.application.JgraphtRouteFinder;
import subway.value_object.Distance;
import subway.value_object.Money;

class DistanceFarePolicyTest {

  private SubwayFarePolicy subwayFarePolicy;

  MockedStatic<JgraphtRouteFinder> jgraphtRouteFinder;

  @BeforeEach
  void setUp() {
    jgraphtRouteFinder = mockStatic(JgraphtRouteFinder.class);

    subwayFarePolicy = new DistanceFarePolicy();
  }

  @AfterEach
  void tearDown() {
    jgraphtRouteFinder.close();
  }

  @ParameterizedTest
  @MethodSource("calculatePriceFromDistance")
  @DisplayName("calculate() : 거리에 따라 요금을 계산할 수 있다.")
  void test_calculate(final Station departure, final Station arrival, final Money price,
      final Distance distance) throws Exception {
    //given
    final List<Line> lines = createDefaultLines();

    jgraphtRouteFinder.when(() -> JgraphtRouteFinder.findShortestRouteDistance(any(), any(), any()))
        .thenReturn(distance);

    //when
    final Money result = subwayFarePolicy.calculate(lines, departure, arrival);

    //then
    assertEquals(result, price);
  }

  static Stream<Arguments> calculatePriceFromDistance() {

    final Station start1 = new Station("A");
    final Station end1 = new Station("G");
    final Money money1 = new Money(1350);
    final Distance distance1 = new Distance(13);

    final Station start2 = new Station("A");
    final Station end2 = new Station("H");
    final Money money2 = new Money(1250);
    final Distance distance2 = new Distance(8);

    final Station start3 = new Station("G");
    final Station end3 = new Station("C");
    final Money money3 = new Money(1250);
    final Distance distance3 = new Distance(10);

    final Station start4 = new Station("F");
    final Station end4 = new Station("H");
    final Money money4 = new Money(1350);
    final Distance distance4 = new Distance(11);

    return Stream.of(
        Arguments.of(start1, end1, money1, distance1),
        Arguments.of(start2, end2, money2, distance2),
        Arguments.of(start3, end3, money3, distance3),
        Arguments.of(start4, end4, money4, distance4)
    );
  }

  private List<Line> createDefaultLines() {
    final Stations stations1 = new Stations(new Station("A"), new Station("B"), 1);
    final Stations stations2 = new Stations(new Station("B"), new Station("C"), 2);
    final Stations stations3 = new Stations(new Station("C"), new Station("D"), 3);

    final Section section1 = new Section(stations1);
    final Section section2 = new Section(stations2);
    final Section section3 = new Section(stations3);

    final Line line1 = new Line(1L, "1호선", List.of(section1, section2, section3));

    final Stations stations4 = new Stations(new Station("B"), new Station("F"), 4);
    final Stations stations5 = new Stations(new Station("F"), new Station("G"), 11);
    final Stations stations6 = new Stations(new Station("G"), new Station("H"), 5);
    final Stations stations7 = new Stations(new Station("H"), new Station("D"), 4);

    final Section section4 = new Section(stations4);
    final Section section5 = new Section(stations5);
    final Section section6 = new Section(stations6);
    final Section section7 = new Section(stations7);

    final Line line2 = new Line(2L, "2호선",
        List.of(section4, section5, section6, section7));

    return List.of(line1, line2);
  }
}
