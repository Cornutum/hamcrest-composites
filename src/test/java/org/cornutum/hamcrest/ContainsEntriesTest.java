//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2022, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import org.cornutum.hamcrest.Drawing.DrawingMatcher;
import static org.cornutum.hamcrest.Drawing.*;
import static org.cornutum.hamcrest.Drawing.Color.*;
import static org.cornutum.hamcrest.Composites.*;
import static org.cornutum.hamcrest.ExpectedFailure.*;

import org.hamcrest.Matchers;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.Map;

/**
 * Runs tests for the {@link ContainsEntries} matcher.
 */
public class ContainsEntriesTest
  {

  @Test
  public void matchesNull()
    {
    // Given...
    Map<Integer,String> expected = null;
    Map<Integer,String> actual = null;
    
    // When...
    assertThat( "Null maps", actual, containsEntries( expected));
    }

  @Test
  public void matchesNull_fails()
    {
    // Given...
    Map<Integer,String> expected = null;
    Map<Integer,String> actual = new MapBuilder<Integer,String>().put( 0, "Hello").build();
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Null map", actual, containsEntries( expected)))
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
    Map<Integer,String> expected = new MapBuilder<Integer,String>().put( 0, "Hello").build();
    Map<Integer,String> actual = null;
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Non-null map", actual, containsEntries( expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Map with 1 entries",
                   "but: was null"))));
    }

  @Test
  public void matchesMap()
    {
    // Given...
    Map<Integer,String> expected =
      new MapBuilder<Integer,String>()
      .put( 1, "World")
      .put( 0, "Hello")
      .build();

    Map<Integer,String> actual =
      new MapBuilder<Integer,String>()
      .put( 0, "Hello")
      .put( 1, "World")
      .build();
    
    // Then...
    assertThat( "Matching", actual, containsEntries( expected));
    }

  @Test
  public void matchesMap_missing()
    {
    // Given...
    Map<Integer,String> expected =
      new MapBuilder<Integer,String>()
      .put( 0, "Hello")
      .put( 1, "World")
      .put( 2, "Peace")
      .build();

    Map<Integer,String> actual =
      new MapBuilder<Integer,String>()
      .put( 0, "Hello")
      .put( 2, "Peace")
      .build();

    // Then...
    expectFailure()
      .when( () -> assertThat( "Missing", actual, containsEntries( expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Map entry set with 3 members",
                   "but: was missing 1 members=[1=World]"))));
    }

  @Test
  public void matchesMap_unexpected()
    {
    // Given...
    Map<Integer,String> expected =
      new MapBuilder<Integer,String>()
      .put( 0, "Hello")
      .put( 1, "World")
      .build();

    Map<Integer,String> actual =
      new MapBuilder<Integer,String>()
      .put( 0, "Hello")
      .put( 1, "World")
      .put( 2, "Peace")
      .put( 3, "Now")
      .build();

    // Then...
    expectFailure()
      .when( () -> assertThat( "Unexpected", actual, containsEntries( expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Map entry set with 2 members",
                   "but: had 2 unexpected members=[2=Peace, 3=Now]"))));
    }

  @Test
  public void matchesNonMap_fails()
    {
    // Given...
    Map<Integer,String> expected =
      new MapBuilder<Integer,String>()
      .put( 0, "Hello")
      .put( 1, "World")
      .build();

    String actual = "Hello, World";
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Matching a non-map", containsEntries( expected).matches( actual)))
      .then( failure -> assertThat( "Failure message", failure.getMessage(), is( equalTo( "Matching a non-map"))));
    }

  @Test
  public void matchesValueMatcher()
    {
    // Given...
    Map<Integer,Drawing> expected =
      new MapBuilder<Integer,Drawing>()
      .put( 0, new Drawing( "Reds", triangle( RED), rectangle( RED), circle( RED)))
      .put( 1, new Drawing( "Greens", triangle( GREEN), rectangle( GREEN), circle( GREEN)))
      .build();

    Map<Integer,Drawing> actual =
      new MapBuilder<Integer,Drawing>()
      .put( 1, new Drawing( "Greens", triangle( GREEN), rectangle( GREEN), circle( GREEN)))
      .put( 0, new Drawing( "Reds", triangle( RED), rectangle( RED), circle( RED)))
      .build();
        
    
    // When...
    assertThat( "Value matchers", actual, containsEntries( DrawingMatcher::new, expected));
    }

  @Test
  public void matchesValueMatcher_fails()
    {
    // Given...
    Map<Integer,Drawing> expected =
      new MapBuilder<Integer,Drawing>()
      .put( 0, new Drawing( "Reds", triangle( RED), rectangle( RED), circle( RED)))
      .put( 1, new Drawing( "Greens", triangle( GREEN), rectangle( GREEN), circle( GREEN)))
      .build();

    Map<Integer,Drawing> actual =
      new MapBuilder<Integer,Drawing>()
      .put( 1, new Drawing( "Greens", triangle( GREEN), rectangle( GREEN), circle( BLUE)))
      .put( 0, new Drawing( "Reds", triangle( RED), rectangle( RED), circle( RED)))
      .build();
        
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Value matchers", actual, containsEntries( DrawingMatcher::new, expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Map entry set containing 1=Drawing[Greens]"
                   + " matching value=Drawing[Greens]"
                   + " matching elements=Iterable containing CIRCLE[Color[0,255,0]]"
                   + " matching color=<Color[0,255,0]>",
                   
                   "but: was <Color[0,0,255]>"))));      
    }

  @Test
  public void matchesKeyMatcher()
    {
    // Given...
    Map<Drawing,Integer> expected =
      new MapBuilder<Drawing,Integer>()
      .put( new Drawing( "Reds", triangle( RED), rectangle( RED), circle( RED)), 0)
      .put( new Drawing( "Greens", triangle( GREEN), rectangle( GREEN), circle( GREEN)), 1)
      .build();

    Map<Drawing,Integer> actual =
      new MapBuilder<Drawing,Integer>()
      .put( new Drawing( "Greens", triangle( GREEN), rectangle( GREEN), circle( GREEN)), 1)
      .put( new Drawing( "Reds", triangle( RED), rectangle( RED), circle( RED)), 0)
      .build();
    
    // When...
    assertThat( "Key matchers", actual, containsEntries( DrawingMatcher::new, Matchers::equalTo, expected));
    }

  @Test
  public void matchesKeyMatcher_fails()
    {
    // Given...
    Map<Drawing,Integer> expected =
      new MapBuilder<Drawing,Integer>()
      .put( new Drawing( "Reds", triangle( RED), rectangle( RED), circle( RED)), 0)
      .put( new Drawing( "Greens", triangle( GREEN), rectangle( GREEN), circle( GREEN)), 1)
      .build();

    Map<Drawing,Integer> actual =
      new MapBuilder<Drawing,Integer>()
      .put( new Drawing( "Greens", triangle( GREEN), rectangle( BLUE), circle( GREEN)), 1)
      .put( new Drawing( "Reds", triangle( RED), rectangle( RED), circle( RED)), 0)
      .build();
    
    // Then...
    expectFailure()
      .when( () -> assertThat( "Key matchers", actual, containsEntries( DrawingMatcher::new, Matchers::equalTo, expected)))
      .then( failure ->
             assertThat(
               "Failure message",
               failure.getMessage(),
               stringContainsInOrder(
                 Arrays.asList(
                   "Expected: Map entry set containing Drawing[Greens]=1"
                   + " matching key=Drawing[Greens]"
                   + " matching elements=Iterable containing RECTANGLE[Color[0,255,0]]"
                   + " matching color=<Color[0,255,0]>",
                   
                   "but: was <Color[0,0,255]>"))));      
    }
  }
