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
 * Runs tests for the {@link ListsElements} matcher.
 */
public class ListsElementsTest
  {

  @Test
  public void matchesNull()
    {
    // Given...
    String[] expected = null;
    String[] actual = null;
    
    // When...
    assertThat( "Null arrays", actual, listsElements( expected));
    }

  @Test
  public void matchesNull_fails()
    {
    // Given...
    String[] expected = null;
    String[] actual = new String[]{ "Red", "Green", "Blue"};
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Null array", actual, listsElements( expected)))
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
    String[] expected = new String[]{ "Red", "Green", "Blue"};
    String[] actual = null;
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Non-null list", actual, listsElements( expected)))
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
  public void matchesArrayProperty()
    {
    // Given...
    Drawing expected = new Drawing( "Reds", triangle( RED), rectangle( RED), circle( RED));
    expected.setTags( "monocolor", "red");
    
    Drawing actual = new Drawing( "Reds", triangle( RED), rectangle( RED), circle( RED));
    actual.setTags( "red", "monocolor");
    
    // When...
    assertThat( "Array function", actual, matches( new DrawingMatcher( expected)));
    }

  @Test
  public void matchesIterable()
    {
    // Given...
    List<String> expected = Arrays.asList( "Red", "Green", "Blue");
    String[] actual = new String[]{ "Red", "Green", "Blue"};
    
    // When...
    assertThat( "Iterable", actual, listsElements( expected));
    }

  @Test
  public void matchesIterator()
    {
    // Given...
    Iterator<String> expected = Arrays.asList( "Red", "Green", "Blue").iterator();
    String[] actual = new String[]{ "Red", "Green", "Blue"};
    
    // When...
    assertThat( "Iterator", actual, listsElements( expected));
    }

  @Test
  public void matchesArray_fails()
    {
    // Given...
    String[] expected = new String[]{ "Red", "Green", "Blue", "Magenta", "Cyan"};
    String[] actual = new String[]{ "Cyan", "Blue", "Red", "Yellow", "Magenta"};
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Overlapping elements", actual, listsElements( expected)))
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
  public void matchesSequence_fails()
    {
    // Given...
    String[] expected = new String[]{ "Red", "Green", "Blue"};
    String[] actual = new String[]{ "Red", "Blue", "Green"};
    
    // When...
    expectFailure()
      .when( () -> assertThat( "Sequence", actual, listsElements( expected)))
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
  public void matchesElementMatcher()
    {
    // Given...
    Drawing[] expected =
      new Drawing[]
        {
          new Drawing( "Reds", triangle( RED), rectangle( RED), circle( RED)),
          new Drawing( "Greens", triangle( GREEN), rectangle( GREEN), circle( GREEN)),
          new Drawing( "Blues", triangle( BLUE), rectangle( BLUE), circle( BLUE))
        };

    Drawing[] actual =
      new Drawing[]{
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED)),
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( BLUE), triangle( BLUE))};
        
    
    // When...
    assertThat( "Element matchers", actual, listsElements( DrawingMatcher::new, expected));
    }

  @Test
  public void matchesElementMatcher_fails()
    {
    // Given...
    Drawing[] expected =
      new Drawing[]{
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( BLUE), triangle( BLUE)),
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED))};

    Drawing[] actual =
      new Drawing[]{
        new Drawing( "Greens", triangle( GREEN), circle( GREEN), rectangle( GREEN)),
        new Drawing( "Blues", rectangle( BLUE), circle( RED), triangle( BLUE)),
        new Drawing( "Reds", circle( RED), triangle( RED), rectangle( RED))};
        
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Element matchers", actual, listsElements( DrawingMatcher::new, expected)))
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
