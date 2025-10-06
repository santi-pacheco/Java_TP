package ui;
import info.movito.themoviedbapi.TmdbApi;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import entity.Genre; // 
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.movielists.*;
import info.movito.themoviedbapi.tools.TmdbException;
import service.ExternalApiService;

import java.util.List;
import java.util.stream.Collectors;

public class Main {
	
	public static void main(String[] args) {
		ExternalApiService apiService = new ExternalApiService("eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJhNDdiYTBiMTI3NDk5YjBlMWIyOGNlYjBhMTgzZWM1NyIsIm5iZiI6MTc1NTYwOTMwOC44NzIsInN1YiI6IjY4YTQ3OGRjNWJkMTI3ZjcyY2RhNThjYSIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ._mkAgrQSPf-YCaYm1TFxuNDEgAtESQEaBOPI5t-8i8Q");
		apiService.test();
	}
}