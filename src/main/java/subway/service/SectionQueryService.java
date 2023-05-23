package subway.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import subway.dao.SectionDao;
import subway.dao.SectionEntity;
import subway.domain.section.Section;
import subway.domain.station.Station;
import subway.domain.station.Stations;

@Service
@Transactional(readOnly = true)
public class SectionQueryService {

  private final SectionDao sectionDao;

  public SectionQueryService(final SectionDao sectionDao) {
    this.sectionDao = sectionDao;
  }

  public List<Section> searchSectionsByLineId(final Long lineId) {
    return sectionDao.findSectionsByLineId(lineId)
        .stream()
        .map(this::mapToSectionFrom)
        .collect(Collectors.toList());
  }

  private Section mapToSectionFrom(final SectionEntity sectionEntity) {
    final Stations stations = new Stations(
        new Station(sectionEntity.getCurrentStationName()),
        new Station(sectionEntity.getNextStationName()),
        sectionEntity.getDistance()
    );

    return new Section(sectionEntity.getId(), stations);
  }
}
