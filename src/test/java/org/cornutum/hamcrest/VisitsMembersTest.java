//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2018, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import org.cornutum.hamcrest.Drawing.DrawingMatcher;
import static org.cornutum.hamcrest.Composites.*;
import static org.cornutum.hamcrest.Drawing.*;
import static org.cornutum.hamcrest.Drawing.Color.*;
import static org.cornutum.hamcrest.ExpectedFailure.*;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Runs tests for the {@link VisitsMembers} matcher.
 */
public class VisitsMembersTest
  {

  @Test
  public void matchesNull()
    {
    // Given...
    Iterator<String> expected = null;
    Iterator<String> actual = null;
    
    // When...
    assertThat( "Null iterators", actual, visitsMembers( expected));
    }

  @Test
  public void matchesNull_fails()
    {
    // Given...
    Iterator<String> expected = null;
    Iterator<String> actual = Arrays.asList( "Red", "Green", "Blue").iterator();
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Null iterator", actual, visitsMembers( expected)))
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
    Iterator<String> expected = Arrays.asList( "Red", "Green", "Blue").iterator();
    Iterator<String> actual = null;
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Non-null list", actual, visitsMembers( expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Iterable with 3 members",
                   "but: was null"))));
    }

  @Test
  public void matchesIterable()
    {
    // Given...
    List<String> expected = Arrays.asList( "Red", "Green", "Blue");
    Iterator<String> actual = Arrays.asList( "Green", "Blue", "Red").iterator();
    
    // When...
    assertThat( "Iterable", actual, visitsMembers( expected));
    }

  @Test
  public void matchesArray()
    {
    // Given...
    Iterator<String> actual = Arrays.asList( "Green", "Blue", "Red").iterator();
    
    // When...
    assertThat( "Iterator", actual, visitsMembers( "Red", "Green", "Blue"));
    }

  @Test
  public void matchesIterator()
    {
    // Given...
    Iterator<String> expected = Arrays.asList( "Red", "Green", "Blue").iterator();
    Iterator<String> actual = Arrays.asList( "Green", "Blue", "Red").iterator();
    
    // When...
    assertThat( "Iterator", actual, visitsMembers( expected));
    }

  @Test
  public void matchesIterator_fails()
    {
    // Given...
    Iterator<String> expected = Arrays.asList( "Red", "Green", "Blue", "Magenta", "Cyan").iterator();
    Iterator<String> actual = Arrays.asList( "Cyan", "Blue", "Red", "Yellow", "Magenta").iterator();
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Overlapping elements", actual, visitsMembers( expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Iterable with 5 members",
                   "but: was missing 1 members=[Green]",
                   "and: had 1 unexpected members=[Yellow]"))));
    }

  @Test
  public void matchesMemberMatcher_fails()
    {
    // Given...
    Iterator<Drawing> expected =
      Arrays.asList(
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( BLUE), triangle( BLUE)),
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)))
      .iterator();

    Iterator<Drawing> actual =
      Arrays.asList(
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( RED), triangle( BLUE)),
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)))
      .iterator();
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Member matchers", actual, visitsMembers( DrawingMatcher::new, expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Iterable containing Drawing[Blues] matching elements=Iterable containing CIRCLE[Color[0,0,255]] matching color=<Color[0,0,255]>",
                   "but: was <Color[255,0,0]>"))));
    }
  }
