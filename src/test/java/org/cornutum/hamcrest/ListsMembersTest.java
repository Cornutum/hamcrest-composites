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
import java.util.Iterator;
import java.util.List;

/**
 * Runs tests for the {@link ListsMembers} matcher.
 */
public class ListsMembersTest
  {

  @Test
  public void matchesNull()
    {
    // Given...
    List<String> expected = null;
    List<String> actual = null;
    
    // When...
    assertThat( "Null lists", actual, listsMembers( expected));
    }

  @Test
  public void matchesNull_fails()
    {
    // Given...
    List<String> expected = null;
    List<String> actual = Arrays.asList( "Red", "Green", "Blue");
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Null list", actual, listsMembers( expected)))
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
    List<String> expected = Arrays.asList( "Red", "Green", "Blue");
    List<String> actual = null;
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Non-null list", actual, listsMembers( expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Sequence of 3 members",
                   "but: was null"))));
    }

  @Test
  public void matchesList()
    {
    // Given...
    List<String> expected = Arrays.asList( "Red", "Green", "Blue");
    List<String> actual = Arrays.asList( "Red", "Green", "Blue");
    
    // When...
    assertThat( "Lists", actual, listsMembers( expected));
    }

  @Test
  public void matchesSequence_fails()
    {
    // Given...
    List<String> expected = Arrays.asList( "Red", "Green", "Blue");
    List<String> actual = Arrays.asList( "Red", "Blue", "Green");
    
    // When...
    expectFailure()
      .when( () -> assertThat( "Sequence", actual, listsMembers( expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Sequence of 3 members",
                   "but: at index=1, found Blue instead of Green"))));
    }

  @Test
  public void matchesList_missing()
    {
    // Given...
    List<String> expected = Arrays.asList( "Red", "Green", "Blue", "Magenta", "Cyan");
    List<String> actual = Arrays.asList( "Green", "Blue", "Red");
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Missing members", actual, listsMembers( expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Sequence of 5 members",
                   "but: was missing 2 members=[Magenta, Cyan]"))));
    }

  @Test
  public void matchesList_unexpected()
    {
    // Given...
    List<String> expected = Arrays.asList( "Blue");
    List<String> actual = Arrays.asList( "Green", "Blue", "Red", "Blue");
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Missing members", actual, listsMembers( expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Sequence of 1 members",
                   "but: had 3 unexpected members=[Green, Red, Blue]"))));
    }

  @Test
  public void matchesList_overlapping()
    {
    // Given...
    List<String> expected = Arrays.asList( "Red", "Green", "Blue", "Magenta", "Cyan");
    List<String> actual = Arrays.asList( "Cyan", "Blue", "Red", "Yellow", "Magenta");
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Overlapping members", actual, listsMembers( expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Sequence of 5 members",
                   "but: was missing 1 members=[Green]",
                   "and: had 1 unexpected members=[Yellow]"))));
    }

  @Test
  public void matchesNonIterable_fails()
    {
    // Given...
    List<String> expected = Arrays.asList( "Red", "Green", "Blue");
    String actual = "Red, Green, Blue";
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Matching a non-iterable", listsMembers( expected).matches( actual)))
      .then( failure -> assertThat( "Failure message", failure.getMessage(), is( equalTo( "Matching a non-iterable"))));
    }

  @Test
  public void matchesArray()
    {
    // Given...
    List<String> actual = Arrays.asList( "Red", "Green", "Blue");
    
    // When...
    assertThat( "Array", actual, listsMembers( "Red", "Green", "Blue"));
    }

  @Test
  public void matchesIterator()
    {
    // Given...
    Iterator<String> expected = Arrays.asList( "Red", "Green", "Blue").iterator();
    List<String> actual = Arrays.asList( "Red", "Green", "Blue");
    
    // When...
    assertThat( "Iterator", actual, listsMembers( expected));
    }

  @Test
  public void matchesIterator_fails()
    {
    // Given...
    Iterator<String> expected = Arrays.asList( "Red", "Green", "Blue").iterator();
    List<String> actual = Arrays.asList( "Green", "Blue", "Red", "White");
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Iterator", actual, listsMembers( expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Sequence of 3 members",
                   "but: had 1 unexpected members=[White]"))));
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
    assertThat( "Member matchers", actual, listsMembers( DrawingMatcher::new, expected));
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
      .when( () -> assertThat( "Member matchers", actual, listsMembers( DrawingMatcher::new, expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Sequence containing Drawing[Blues] matching elements=Iterable containing CIRCLE[Color[0,0,255]] matching color=<Color[0,0,255]>",
                   "but: was <Color[255,0,0]>"))));
    }
  }
