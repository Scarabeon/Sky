package com.bskyb.internettv.parental_control_service;

import com.bskyb.internettv.thirdparty.MovieService;
import com.bskyb.internettv.thirdparty.TechnicalFailureException;
import com.bskyb.internettv.utils.Utils;

/**
 * Implementation of {@link ParentalControlService} interface
 * @author Michal Bielik
 *
 */
public class ParentalControlServiceImpl implements ParentalControlService {

	private MovieService movieService;

	public ParentalControlServiceImpl(MovieService movieService) throws TechnicalFailureException {
		if (movieService == null) {
			throw new TechnicalFailureException();
		}
		this.movieService = movieService;
	}

	public boolean canWatchMovie(String customerParentalControlLevel, String movieId) throws Exception {
		/* First checks if given control level exists in the list */
		if (!Utils.existsControlLevel(customerParentalControlLevel)) {
			throw new TechnicalFailureException();
		}

		/* Gathers control level of given movie id - the method controls validity of ID itself */
		String level = movieService.getParentalControlLevel(movieId);

		/* Checks if returned control level exists */
		if (!Utils.existsControlLevel(level)) {
			throw new TechnicalFailureException();
		}

		/* If all limit control level, movie ID and actual control level are valid comes to evaluation */
		return Utils.levelMatch(level, customerParentalControlLevel);
	}
}