package io.vef.academy.download.service.controllers;

import io.vef.academy.download.service.domain.Download;
import io.vef.academy.download.service.services.DownloadService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import responses.DownloadedContentResponse;

@Controller
@RequestMapping(value = "/api/v1/downloads")
public class DownloadController {

    private final DownloadService downloadService;

    public DownloadController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getDownloadedContentById(@PathVariable String id) {
        Download download = this.downloadService.getDownloadById(id);
        return new ResponseEntity<DownloadedContentResponse>(
                new DownloadedContentResponse(download.getId(), download.getContent()),
                HttpStatus.OK
        );
    }
}
