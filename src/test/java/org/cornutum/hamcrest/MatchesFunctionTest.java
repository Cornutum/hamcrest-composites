//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2018, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import static org.cornutum.hamcrest.Composites.*;

import org.hamcrest.Matchers;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.junit.Assert.fail;

import java.util.Arrays;

/**
 * Runs tests for the {@link MatchesFunction} matcher.
 */
public class MatchesFunctionTest
  {  
  @Test
  public void matchesNullSource_fails()
    {
    // Given...
    Drawing expected = null;
    Drawing actual = null;
    
    // When...
    try
      {
      assertThat( "Null source", actual, matchesFunction( "name", Drawing::getName, expected, Matchers::equalTo));
      fail( "Expected failure not reported");
      }

    // Then...
    catch( IllegalArgumentException failure)
      {
      }
    catch( Throwable t)
      {
      fail( "Unexpected failure: " + t);
      }
    }

  @Test
  public void matchesNull_fails()
    {
    // Given...
    Drawing expected = new Drawing( "Empty");
    Drawing actual = null;
    
    // When...
    try
      {
      assertThat( "Null object", actual, matchesFunction( "name", Drawing::getName, expected, Matchers::equalTo));
      fail( "Expected failure not reported");
      }

    // Then...
    catch( AssertionError failure)
      {
      assertThat(
        "Failure message",
        failure.getMessage(),
        stringContainsInOrder(
          Arrays.asList(
            "Expected: name=\"Empty\"",
            "but: name can't be derived from a null object")));
      }
    catch( Throwable t)
      {
      fail( "Unexpected failure: " + t);
      }
    }

  @Test
  public void matchesNullResult()
    {
    // Given...
    Drawing expected = new Drawing( null);
    Drawing actual = new Drawing( null);
    
    // When...
    assertThat( "Null result", actual, matchesFunction( "name", Drawing::getName, expected, Matchers::equalTo));
    }

  @Test
  public void matchesNullResult_fails()
    {
    // Given...
    Drawing expected = new Drawing( null);
    Drawing actual = new Drawing( "Empty");
    
    // When...
    try
      {
      assertThat( "Null result", actual, matchesFunction( "name", Drawing::getName, expected, Matchers::equalTo));
      fail( "Expected failure not reported");
      }

    // Then...
    catch( AssertionError failure)
      {
      assertThat(
        "Failure message",
        failure.getMessage(),
        stringContainsInOrder(
          Arrays.asList(
            "Expected: name=null",
            "but: was \"Empty\"")));
      }
    catch( Throwable t)
      {
      fail( "Unexpected failure: " + t);
      }
    }

  @Test
  public void matchesNonNullResult_fails()
    {
    // Given...
    Drawing expected = new Drawing( "Empty");
    Drawing actual = new Drawing( null);
    
    // When...
    try
      {
      assertThat( "Non-null result", actual, matchesFunction( "name", Drawing::getName, expected, Matchers::equalTo));
      fail( "Expected failure not reported");
      }

    // Then...
    catch( AssertionError failure)
      {
      assertThat(
        "Failure message",
        failure.getMessage(),
        stringContainsInOrder(
          Arrays.asList(
            "Expected: name=\"Empty\"",
            "but: was null")));
      }
    catch( Throwable t)
      {
      fail( "Unexpected failure: " + t);
      }
    }

  @Test
  public void matchesResult()
    {
    // Given...
    Drawing expected = new Drawing( "Empty");
    Drawing actual = new Drawing( "Empty");
    
    // When...
    assertThat( "Names", actual, matchesFunction( "name", Drawing::getName, expected, Matchers::equalTo));
    }

  @Test
  public void matchesResult_fails()
    {
    // Given...
    Drawing expected = new Drawing( "Empty");
    Drawing actual = new Drawing( "Not Empty");
    
    // When...
    try
      {
      assertThat( "Names", actual, matchesFunction( "name", Drawing::getName, expected, Matchers::equalTo));
      fail( "Expected failure not reported");
      }

    // Then...
    catch( AssertionError failure)
      {
      assertThat(
        "Failure message",
        failure.getMessage(),
        stringContainsInOrder(
          Arrays.asList(
            "Expected: name=\"Empty\"",
            "but: was \"Not Empty\"")));
      }
    catch( Throwable t)
      {
      fail( "Unexpected failure: " + t);
      }
    }
  }
