package com.springcraft.moviecatalogservice.resources;

import com.springcraft.moviecatalogservice.models.CatalogItem;
import com.springcraft.moviecatalogservice.models.Movie;
import com.springcraft.moviecatalogservice.models.Rating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {
        //get all rated Movie IDs
        List<Rating> ratings = Arrays.asList(
                new Rating("123", 4),
                new Rating("268", 5)
        );

        //for each movie ID call movie info service and get details
        return ratings.stream()
                .map(rating -> {
                        //Movie movie = restTemplate.getForObject("http://localhost:8082/movies/" + rating.getMovieId(), Movie.class);
                     
                        Movie movie = webClientBuilder.build()
                                .get()
                                .uri("http://localhost:8082/movies/" + rating.getMovieId())
                                .retrieve()
                                .bodyToMono(Movie.class)
                                .block();

                        return new CatalogItem(movie.getName(), "Description", rating.getRating());
                })
                .collect(Collectors.toList());
    }
}
