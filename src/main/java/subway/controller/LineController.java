package subway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import subway.domain.Line;
import subway.service.LineCommandService;
import subway.service.LineQueryService;
import subway.service.dto.LineResponse;
import subway.service.dto.RegisterLineRequest;
import subway.service.dto.SectionResponse;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LineController {

    private final LineCommandService lineCommandService;
    private final LineQueryService lineQueryService;

    public LineController(final LineCommandService lineCommandService, final LineQueryService lineQueryService) {
        this.lineCommandService = lineCommandService;
        this.lineQueryService = lineQueryService;
    }

    @GetMapping("/lines")
    @ResponseStatus(HttpStatus.OK)
    public List<LineResponse> showLines(@RequestParam(value = "lineName", required = false) String lineName) {
        final List<Line> lines = lineQueryService.searchLines(lineName);

        return lines.stream()
                    .map(line -> new LineResponse(line.getName(), mapToSectionResponseFrom(line)))
                    .collect(Collectors.toList());
    }

    @PostMapping("/lines")
    @ResponseStatus(HttpStatus.CREATED)
    public LineResponse registerLine(@RequestBody RegisterLineRequest registerLineRequest) {
        lineCommandService.registerLine(registerLineRequest);

        final Line line = lineQueryService.searchByLineName(registerLineRequest.getLineName());

        return new LineResponse(line.getName(), mapToSectionResponseFrom(line));
    }

    private List<SectionResponse> mapToSectionResponseFrom(final Line line) {
        return line.getSections()
                   .stream()
                   .map(section -> new SectionResponse(
                           section.getStations().getCurrent().getName(),
                           section.getStations().getNext().getName(),
                           section.getStations().getDistance()))
                   .collect(Collectors.toList());
    }
}
