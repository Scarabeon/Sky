package com.bskyb.internettv.parental_control_service;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.bskyb.internettv.data.ControlLevelEnum;
import com.bskyb.internettv.thirdparty.MovieService;
import com.bskyb.internettv.thirdparty.TechnicalFailureException;
import com.bskyb.internettv.thirdparty.TitleNotFoundException;
import com.bskyb.internettv.utils.Utils;

@RunWith(MockitoJUnitRunner.class)
public class ParentalControlServiceTest {

	@Mock
	private MovieService movieService;

	private ParentalControlService parentalControlService;

	@Before
	public void init() throws TechnicalFailureException, TitleNotFoundException {
		parentalControlService = new ParentalControlServiceImpl(movieService);
	}

	/**
	 * Should throw {@link TechnicalFailureException} if mocking fails / mocked object is null
	 * @throws Exception
	 */
	@Test(expected = TechnicalFailureException.class)
	public void mockingFailed() throws Exception {
		movieService = null;
		String controlLevel = ControlLevelEnum.U.toString();
		String movieId = anyString();
		parentalControlService.canWatchMovie(controlLevel, movieId);
	}

	/**
	 * Should throw {@link TechnicalFailureException} when control level is null
	 * @throws Exception
	 */
	@Test(expected = TechnicalFailureException.class)
	public void blankControlLevel() throws Exception {
		String controlLevel = null;
		String movieId = anyString();
		when(movieService.getParentalControlLevel(movieId)).thenReturn(controlLevel);
		parentalControlService.canWatchMovie(controlLevel, movieId);
	}
	
	/**
	 * Should throw {@link TechnicalFailureException} when movie name is null
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = TechnicalFailureException.class)
	public void blankMovieName() throws Exception {
		String controlLevel = ControlLevelEnum.U.toString();
		String movieId = null;
		when(movieService.getParentalControlLevel(movieId)).thenThrow(TechnicalFailureException.class);
		parentalControlService.canWatchMovie(controlLevel, movieId);
	}

	/**
	 * Should throw {@link TechnicalFailureException} when movie name is an empty string
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = TechnicalFailureException.class)
	public void emptyMovieName() throws Exception {
		String controlLevel = ControlLevelEnum.A12.toString();
		String movieId = "";
		when(movieService.getParentalControlLevel(movieId)).thenThrow(TechnicalFailureException.class);
		parentalControlService.canWatchMovie(controlLevel, movieId);
	}

	/**
	 * Should throw {@link TechnicalFailureException} when empty string / null combination
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = TechnicalFailureException.class)
	public void emptyAndNullCombined1() throws Exception {
		String controlLevel = "";
		String movieId = null;
		when(movieService.getParentalControlLevel(movieId)).thenThrow(TechnicalFailureException.class);
		parentalControlService.canWatchMovie(controlLevel, movieId);
	}

	/**
	 * Should throw {@link TechnicalFailureException} if given control level does not exist
	 * @throws Exception 
	 */
	@Test(expected = TechnicalFailureException.class)
	public void invalidControlLevel() throws Exception {
		parentalControlService.canWatchMovie("A16", "Titanic");
	}

	/**
	 * Should throw {@link TitleNotFoundException} as the movie title does not exist
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = TitleNotFoundException.class)
	public void invalidFileName() throws Exception {
		String controlLevel = ControlLevelEnum.U.toString();
		String movieId = "Titanic";
		when(movieService.getParentalControlLevel(movieId)).thenThrow(TitleNotFoundException.class);
		parentalControlService.canWatchMovie(controlLevel, movieId);
	}

	/**
	 * Should allow to watch at the same restriction level
	 * @throws Exception
	 */
	@Test
	public void equalAge() throws Exception {
		String controlLevel = ControlLevelEnum.A12.toString();
		String movieId = anyString();
		when(movieService.getParentalControlLevel(movieId)).thenReturn(ControlLevelEnum.A12.toString());
		Assert.assertTrue(parentalControlService.canWatchMovie(controlLevel, movieId));
	}

	/**
	 * Should allow to watch with lower restriction level
	 * @throws Exception
	 */
	@Test
	public void higherAge() throws Exception {
		String controlLevel = ControlLevelEnum.A15.toString();
		String movieId = anyString();
		when(movieService.getParentalControlLevel(movieId)).thenReturn(ControlLevelEnum.PG.toString());
		Assert.assertTrue(parentalControlService.canWatchMovie(controlLevel, movieId));
	}

	/**
	 * Should not allow to watch with higher restriction level
	 * @throws Exception
	 */
	@Test
	public void lowerAge() throws Exception {
		String controlLevel = ControlLevelEnum.U.toString();
		String movieId = anyString();
		when(movieService.getParentalControlLevel(movieId)).thenReturn(ControlLevelEnum.A12.toString());
		Assert.assertFalse(parentalControlService.canWatchMovie(controlLevel, movieId));
	}

	/**
	 * Should not allow to watch the movie for all levels below 15.
	 * Multiple asserts are out of the JUnit test convenience but very demonstrative in this example
	 * @throws Exception
	 */
	@Test
	public void allLevelsAssertion() throws Exception {
		String movieId = anyString();
		when(movieService.getParentalControlLevel(movieId)).thenReturn(ControlLevelEnum.A15.toString());
		Assert.assertFalse(parentalControlService.canWatchMovie(ControlLevelEnum.U.toString(), movieId));
		Assert.assertFalse(parentalControlService.canWatchMovie(ControlLevelEnum.PG.toString(), movieId));
		Assert.assertFalse(parentalControlService.canWatchMovie(ControlLevelEnum.A12.toString(), movieId));
		Assert.assertTrue(parentalControlService.canWatchMovie(ControlLevelEnum.A15.toString(), movieId));
		Assert.assertTrue(parentalControlService.canWatchMovie(ControlLevelEnum.A18.toString(), movieId));
	}

	/**
	 * Should throw {@link RuntimeException} when the title equals to "QQQ"
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Test(expected = RuntimeException.class)
	public void predefinedVauleFail() throws Exception {
		String controlLevel = ControlLevelEnum.A18.toString();
		String movieId = "QQQ";
		when(movieService.getParentalControlLevel(movieId)).thenThrow(RuntimeException.class);
		parentalControlService.canWatchMovie(controlLevel, movieId);
	}

	/**
	 * Should throw {@link TechnicalFailureException} when control level received from movie ID does not exist
	 * @throws Exception
	 */
	@Test(expected = TechnicalFailureException.class)
	public void illegalControlLevelReturned() throws Exception {
		String controlLevel = ControlLevelEnum.PG.toString();
		String movieId = anyString();
		when(movieService.getParentalControlLevel(movieId)).thenReturn("A17");
		parentalControlService.canWatchMovie(controlLevel, movieId);
	}

	/**
	 * Should throw {@link TechnicalFailureException} when control level received from movie ID is null
	 * @throws Exception
	 */
	@Test(expected = TechnicalFailureException.class)
	public void nullControlLevelReturned() throws Exception {
		String controlLevel = ControlLevelEnum.PG.toString();
		String movieId = anyString();
		when(movieService.getParentalControlLevel(movieId)).thenReturn(null);
		parentalControlService.canWatchMovie(controlLevel, movieId);
	}

	/**
	 * Movie levels are case sensitive: should not be the evaluaion {@link Utils#existsControlLevel(String)}
	 * has to be adjusted accordingly. I have done this for curiosity reason only &#9786; 
	 * @throws Exception
	 */
	@Test(expected = TechnicalFailureException.class)
	public void caseSensitivity() throws Exception {
		String controlLevel = "u";
		String movieId = anyString();
		when(movieService.getParentalControlLevel(movieId)).thenReturn(ControlLevelEnum.U.toString());
		parentalControlService.canWatchMovie(controlLevel, movieId);
	}
}