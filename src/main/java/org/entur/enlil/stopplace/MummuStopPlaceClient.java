package org.entur.enlil.stopplace;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Fetches stop place data from the upstream read API and projects it to small
 * records immediately. The DTOs below declare only the handful of fields we use;
 * {@code ignoreUnknown} means the large geometry members are skipped during
 * parsing rather than materialised, so they never accumulate in heap.
 */
@Component
public class MummuStopPlaceClient implements StopPlaceClient {

  /** The batch size the frontend already used against this API. */
  private static final int CHUNK_SIZE = 200;

  /**
   * Responses run to tens of MB uncompressed. WebClient's default in-memory
   * limit is 256 KB, which would fail these requests outright.
   */
  private static final int MAX_IN_MEMORY_BYTES = 32 * 1024 * 1024;

  private static final ParameterizedTypeReference<List<UpstreamStopPlace>> STOP_PLACE_LIST =
    new ParameterizedTypeReference<>() {};

  private static final ParameterizedTypeReference<List<UpstreamTopographicPlace>> TOPOGRAPHIC_LIST =
    new ParameterizedTypeReference<>() {};

  private final WebClient webClient;

  public MummuStopPlaceClient(
    WebClient.Builder builder,
    @Value("${enlil.stop-places.url}") String baseUrl
  ) {
    this.webClient =
      builder
        .baseUrl(baseUrl)
        .defaultHeader("ET-Client-Name", "entur-enlil")
        .codecs(configurer ->
          configurer.defaultCodecs().maxInMemorySize(MAX_IN_MEMORY_BYTES)
        )
        .build();
  }

  @Override
  public List<StopPlaceRecord> fetchStopPlaces(Collection<String> ids) {
    return fetchChunked(ids, "/stop-places", STOP_PLACE_LIST)
      .stream()
      .map(sp ->
        new StopPlaceRecord(
          sp.id(),
          sp.transportMode(),
          sp.topographicPlaceRef() == null ? null : sp.topographicPlaceRef().ref()
        )
      )
      .toList();
  }

  @Override
  public List<TopographicPlaceRecord> fetchTopographicPlaces(Collection<String> ids) {
    return fetchChunked(ids, "/topographic-places", TOPOGRAPHIC_LIST)
      .stream()
      .map(tp ->
        new TopographicPlaceRecord(
          tp.id(),
          tp.descriptor() == null || tp.descriptor().name() == null
            ? null
            : tp.descriptor().name().value()
        )
      )
      .toList();
  }

  private <T> List<T> fetchChunked(
    Collection<String> ids,
    String path,
    ParameterizedTypeReference<List<T>> type
  ) {
    List<String> all = List.copyOf(ids);
    List<T> results = new ArrayList<>();
    for (int start = 0; start < all.size(); start += CHUNK_SIZE) {
      List<String> chunk = all.subList(start, Math.min(start + CHUNK_SIZE, all.size()));
      List<T> page = webClient
        .get()
        .uri(uriBuilder ->
          uriBuilder.path(path).queryParam("ids", String.join(",", chunk)).build()
        )
        .retrieve()
        .bodyToMono(type)
        .block();
      if (page != null) {
        results.addAll(page);
      }
    }
    return results;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record UpstreamStopPlace(
    String id,
    String transportMode,
    TopographicPlaceRef topographicPlaceRef
  ) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    record TopographicPlaceRef(String ref) {}
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  record UpstreamTopographicPlace(String id, Descriptor descriptor) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    record Descriptor(Name name) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record Name(String value) {}
  }
}
