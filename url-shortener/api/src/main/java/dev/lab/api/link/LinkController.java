package dev.lab.api.link;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/links")
public class LinkController {

    private final LinkService linkService;
    private final String baseUrl;

    public LinkController(LinkService linkService, @Value("${app.base-url}")String baseUrl) {
        this.linkService = linkService;
        this.baseUrl = baseUrl;
    }

    @PostMapping
    public ResponseEntity<CreateLinkResponse> createLink(
            @Valid @RequestBody CreateLinkRequest request) {

        Link link = linkService.create(new CreateLinkCommand(request.url(), request.expiresAt()));
        String shortUrl = baseUrl + "/" + link.code();

        return ResponseEntity
                .created(URI.create(shortUrl))
                .body(new CreateLinkResponse(
                        link.code(),
                        shortUrl));
    }
}
