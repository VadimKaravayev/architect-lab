package dev.lab.api.link;

import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;

@Service
public class LinkService {

    private final LinkRepository linkRepository;

    public LinkService(LinkRepository linkRepository) {
        this.linkRepository = linkRepository;
    }

    public Link create(CreateLinkCommand command) {
        String url = command.url();
        validateUrl(url);
        Instant expiresAt = command.expiresAt();
        long id = linkRepository.nextId();
        String code = Base62.encode(id);
        linkRepository.insert(code, url, expiresAt);

        return new Link(code, url, expiresAt);
    }

    private static void validateUrl(String url) {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            throw new InvalidUrlException(e.getMessage());
        }
        String scheme = uri.getScheme();
        if (scheme == null
                || !scheme.equalsIgnoreCase("http")
                && !scheme.equalsIgnoreCase("https")) {
            throw new InvalidUrlException("Url must have http or https");
        }

        String host = uri.getHost();
        if (host == null || host.isEmpty()) {
            throw new InvalidUrlException("Host is empty");
        }
    }
}
