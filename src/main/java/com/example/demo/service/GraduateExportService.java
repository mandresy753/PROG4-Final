package com.example.demo.service;

import com.example.demo.enums.Track;
import com.example.demo.file.bucket.BucketComponent;
import java.time.Duration;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GraduateExportService {

    private static final String BUCKET_PREFIX = "graduates/";
    private static final Duration DOWNLOAD_LINK_DURATION = Duration.ofMinutes(15);

    private final GraduationService graduationService;
    private final GraduateXlsxGenerator graduateXlsxGenerator;
    private final BucketComponent bucketComponent;

    public String exportToXlsx(Track track) {
        var graduates = graduationService.listGraduates(track);
        var file = graduateXlsxGenerator.generate(graduates);

        var bucketKey = BUCKET_PREFIX + track + "-" + LocalDate.now() + ".xlsx";
        bucketComponent.upload(file, bucketKey);

        return bucketComponent.presign(bucketKey, DOWNLOAD_LINK_DURATION).toString();
    }
}
