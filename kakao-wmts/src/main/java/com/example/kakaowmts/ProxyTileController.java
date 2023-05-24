package com.example.kakaowmts;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
public class ProxyTileController {
  private WebClient webClient;

  @GetMapping("/home")
  public String home(final Model model) {
    List<Post> postList = Arrays.asList(
        new Post(1, "title1", "content1"),
        new Post(2, "title2", "content2")
    );
    model.addAttribute("posts", postList);
    return "home";
  }

  @GetMapping(value = "/map_2d_hd/{tileMatrix}/{row}/{col}.png", produces = MediaType.IMAGE_PNG_VALUE)
  public Mono<DataBuffer> retrieveImage(@PathVariable int tileMatrix, @PathVariable int row, @PathVariable int col) {
    return webClient
        .get()
        .uri(uriBuilder -> uriBuilder
            .scheme("https")
            .host(String.format("map%d.daumcdn.net", col % 4))
            .path("/map_2d_hd/2303ksn/L{tileMatrix}/{row}/{col}.png")
            .build(tileMatrix, row, col))
        .accept(MediaType.valueOf(String.valueOf(MediaType.IMAGE_PNG)))
        .exchange()
        .flatMap(clientResponse -> clientResponse.bodyToMono(DataBuffer.class)
            .doOnSuccess(body -> {
              if (clientResponse.statusCode().isError()) {
                log.error("HttpStatusCode = {}", clientResponse.statusCode());
                log.error("HttpHeaders = {}", clientResponse.headers().asHttpHeaders());
                log.error("ResponseBody = {}", body);
              }
            }));
  }
}
