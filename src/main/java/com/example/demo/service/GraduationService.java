package com.example.demo.service;

import com.example.demo.enums.Track;
import com.example.demo.enums.UserRole;
import com.example.demo.model.Graduate;
import com.example.demo.model.User;
import com.example.demo.repository.GraduateRankingRow;
import com.example.demo.repository.GraduationQueryRepository;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GraduationService {

  public static final int EXPECTED_TOTAL_CREDITS =
      SemesterCreditPolicy.MAX_CREDITS_PER_SEMESTER * 6;

  private final GraduationQueryRepository graduationQueryRepository;

  /**
   * Lists the graduates of a given track (EL/TN) for a given promotion, ranked by overall average
   * (best first). The promotion is the label of the academic year in which the student started
   * their L1 - i.e. their entry cohort, independent of any group changes since.
   *
   * <p>The averaging, completeness check and ranking all happen in a single SQL query (see {@link
   * GraduationQueryRepository#findRankedGraduates}) rather than in Java, so this stays fast
   * regardless of how many students are enrolled overall - only the matching promotion/track is
   * ever touched, and only graduated students are ever pulled back into the JVM.
   */
  public List<Graduate> listGraduates(Track track, String promotion) {
    return graduationQueryRepository
        .findRankedGraduates(track.name(), promotion, EXPECTED_TOTAL_CREDITS)
        .stream()
        .map(row -> toGraduate(row, track, promotion))
        .toList();
  }

  /**
   * Lists both tracks of a promotion at once (one SQL query per track), for the combined EL+TN
   * export. Kept as two queries rather than one because ranking is computed per track.
   */
  public Map<Track, List<Graduate>> listGraduatesByPromotion(String promotion) {
    var byTrack = new EnumMap<Track, List<Graduate>>(Track.class);
    for (Track track : Track.values()) {
      byTrack.put(track, listGraduates(track, promotion));
    }
    return byTrack;
  }

  private Graduate toGraduate(GraduateRankingRow row, Track track, String promotion) {
    var student =
        User.builder()
            .id(row.getId())
            .reference(row.getReference())
            .lastName(row.getLastName())
            .firstName(row.getFirstName())
            .email(row.getEmail())
            .role(UserRole.STUDENT)
            .build();

    return Graduate.builder()
        .student(student)
        .track(track)
        .promotion(promotion)
        .overallAverage(row.getOverallAverage())
        .totalCredits(row.getTotalCredits())
        .graduated(true)
        .rank(row.getRank())
        .build();
  }
}
