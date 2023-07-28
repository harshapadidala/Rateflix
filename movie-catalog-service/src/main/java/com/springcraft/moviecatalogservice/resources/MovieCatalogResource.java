package com.springcraft.moviecatalogservice.resources;

import com.springcraft.moviecatalogservice.models.CatalogItem;
import com.springcraft.moviecatalogservice.models.Movie;
import com.springcraft.moviecatalogservice.models.Rating;
import com.springcraft.moviecatalogservice.models.UserRating;
import org.apache.catalina.User;
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
        //call ratingsdata service and get ratings of movies user has given
        UserRating userRating = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class);

        //for each movie ID call movie info service and get details
        return userRating.getRatingList()
                .stream()
                .map(rating -> {
                        Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
                        return new CatalogItem(movie.getName(), "Description", rating.getRating());
                })
                .collect(Collectors.toList());
    }

    /*
            Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/" + rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();
     */
}
