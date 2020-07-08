//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2018, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import org.cornutum.hamcrest.Drawing.DrawingMatcher;
import static org.cornutum.hamcrest.Drawing.*;
import static org.cornutum.hamcrest.Drawing.Color.*;
import static org.cornutum.hamcrest.Composites.*;
import static org.cornutum.hamcrest.ExpectedFailure.*;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.List;

/**
 * Runs tests for the {@link ListsMatching} matcher.
 */
public class ListsMatchingTest
  {

  @Test
  public void matchesNull()
    {
    // Given...
    List<Drawing> expected = null;
    List<Drawing> actual = null;
    
    // When...
    assertThat( "Null lists", actual, listsMatching( DrawingMatcher::new, expected));
    }

  @Test
  public void matchesNull_fails()
    {
    // Given...
    List<Drawing> expected = null;
    List<Drawing> actual = Arrays.asList( new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)));
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Null list", actual, listsMatching( DrawingMatcher::new, expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: null",
                   "but: was not null"))));
    }

  @Test
  public void matchesNonNull_fails()
    {
    // Given...
    List<Drawing> expected = Arrays.asList( new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)));
    List<Drawing> actual = null;
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Non-null list", actual, listsMatching( DrawingMatcher::new, expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Sequence of 1 members",
                   "but: was null"))));
    }

  @Test
  public void matchesSequence_fails()
    {
    // Given...
    List<Drawing> expected =
      Arrays.asList(
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)),
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( BLUE), triangle( BLUE)));

    List<Drawing> actual =
      Arrays.asList(
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)),
        new Drawing( "Blues", rectangle( BLUE), circle( BLUE), triangle( BLUE)),
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)));
    
    // When...
    expectFailure()
      .when( () -> assertThat( "Sequence", actual, listsMatching( DrawingMatcher::new, expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: At position=1, Drawing[Greens] matching name=\"Greens\"",
                   "but: was \"Blues\""))));
    }

  @Test
  public void matchesList_missing()
    {
    // Given...
    List<Drawing> expected =
      Arrays.asList(
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)),
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( BLUE), triangle( BLUE)));

    List<Drawing> actual =
      Arrays.asList(
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)),
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)));
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Missing members", actual, listsMatching( DrawingMatcher::new, expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Sequence of 3 members",
                   "but: was missing 1 members=[Drawing[Blues]] starting at position=2"))));
    }

  @Test
  public void matchesList_unexpected()
    {
    // Given...
    List<Drawing> expected =
      Arrays.asList(
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)));

    List<Drawing> actual =
      Arrays.asList(
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)),
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( BLUE), triangle( BLUE)));
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Missing members", actual, listsMatching( DrawingMatcher::new, expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Sequence of 1 members",
                   "but: had 2 unexpected members=[Drawing[Greens], Drawing[Blues]] starting at position=1"))));
    }

  @Test
  public void matchesNonIterable_fails()
    {
    // Given...
    List<Drawing> expected =
      Arrays.asList(
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)),
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( BLUE), triangle( BLUE)));
    
    String actual = "Red, Green, Blue";
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Matching a non-iterable", listsMatching( DrawingMatcher::new, expected).matches( actual)))
      .then( failure -> assertThat( "Failure message", failure.getMessage(), is( equalTo( "Matching a non-iterable"))));
    }

  @Test
  public void matchesArray()
    {
    // Given...
    List<Drawing> actual =
      Arrays.asList(
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)),
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( BLUE), triangle( BLUE)));
    
    // When...
    assertThat(
      "Array",
      actual,
      listsMatching(
        DrawingMatcher::new,
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)),
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( BLUE), triangle( BLUE))));
    }

  @Test
  public void matchesIterator()
    {
    // Given...
    List<Drawing> actual =
      Arrays.asList(
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)),
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( BLUE), triangle( BLUE)));
    
    // When...
    assertThat( "Iterator", actual, listsMatching( DrawingMatcher::new, actual.iterator()));
    }

  @Test
  public void matchesMemberMatcher()
    {
    // Given...
    Drawing[] expected =
      new Drawing[]
        {
          new Drawing( "Reds", triangle( RED), rectangle( RED), circle( RED)),
          new Drawing( "Greens", triangle( GREEN), rectangle( GREEN), circle( GREEN)),
          new Drawing( "Blues", triangle( BLUE), rectangle( BLUE), circle( BLUE))
        };

    List<Drawing> actual =
      Arrays.asList(
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)),
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( BLUE), triangle( BLUE)));
        
    
    // When...
    assertThat( "Member matchers", actual, listsMatching( DrawingMatcher::new, expected));
    }

  @Test
  public void matchesMemberMatcher_fails()
    {
    // Given...
    List<Drawing> expected =
      Arrays.asList(
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( BLUE), triangle( BLUE)),
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)));

    List<Drawing> actual =
      Arrays.asList(
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( RED), triangle( BLUE)),
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)));
        
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Member matchers", actual, listsMatching( DrawingMatcher::new, expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: At position=1, Drawing[Blues] matching elements=Iterable containing CIRCLE[Color[0,0,255]] matching color=<Color[0,0,255]>",
                   "but: was <Color[255,0,0]>"))));
    }
  }
