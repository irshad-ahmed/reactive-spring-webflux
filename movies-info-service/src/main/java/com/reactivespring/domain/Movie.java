package com.reactivespring.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Movie {
    @Id
    private String movieId;
    @NotBlank(message = "Movie name must be provided")
    private String name;

    private String description;

    private List<@NotBlank(message = "cast must be present") String> cast;

    private LocalDate releaseDate;

    @Positive(message = "Year must be a positive number")
    private Integer year;
}
