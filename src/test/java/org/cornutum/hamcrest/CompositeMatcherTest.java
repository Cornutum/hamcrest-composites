//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2018, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import org.cornutum.hamcrest.Drawing.DrawingMatcher;
import static org.cornutum.hamcrest.Composites.matches;
import static org.cornutum.hamcrest.Drawing.*;
import static org.cornutum.hamcrest.Drawing.Color.*;
import static org.cornutum.hamcrest.ExpectedFailure.*;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.stringContainsInOrder;

import java.util.Arrays;

/**
 * Runs tests for the {@link CompositeMatcher} matcher.
 */
public class CompositeMatcherTest
  {
  @Test
  public void matchesNull_fails()
    {
    // Given...
    Drawing expected = new Drawing( "Empty");
    Drawing actual = null;
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Null object", actual, matches( new DrawingMatcher( expected))))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: " + expected.toString() + " matching not null",
                   "but: was null"))));
    }

  @Test
  public void matchesNullResult()
    {
    // Given...
    Drawing expected = new Drawing( null);
    Drawing actual = new Drawing( null);
    
    // When...
    assertThat( "Null result", actual, matches( new DrawingMatcher( expected)));
    }

  @Test
  public void matchesNullProperty_fails()
    {
    // Given...
    Drawing expected = new Drawing( null);
    Drawing actual = new Drawing( "Empty");
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Null result", actual, matches( new DrawingMatcher( expected))))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: " + expected.toString() + " matching name=null",
                   "but: was \"Empty\""))));
    }

  @Test
  public void matchesNonNullProperty_fails()
    {
    // Given...
    Drawing expected = new Drawing( "Empty");
    Drawing actual = new Drawing( null);
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Non-null result", actual, matches( new DrawingMatcher( expected))))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: " + expected.toString() + " matching name=\"Empty\"",
                   "but: was null"))));
    }

  @Test
  public void matchesProperties()
    {
    // Given...
    Drawing expected = new Drawing( "Dots", triangle( RED), circle( BLUE));
    Drawing actual = new Drawing( "Dots", circle( BLUE), triangle( RED));
    
    // When...
    assertThat( "Names", actual, matches( new DrawingMatcher( expected)));
    }

  @Test
  public void matchesProperties_fails()
    {
    // Given...
    Drawing expected = new Drawing( "Dots", circle( RED), circle( BLUE));
    Drawing actual = new Drawing( "Dots", triangle( BLUE), circle( RED));
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Names", actual, matches( new DrawingMatcher( expected))))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: " + expected.toString() + " matching elements=Iterable with 2 members",
                   "but: was missing 1 members=[CIRCLE[Color[0,0,255]]]",
                   "and: had 1 unexpected members=[TRIANGLE[Color[0,0,255]]]"))));
    }
  }
