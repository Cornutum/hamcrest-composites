//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2018, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import static org.cornutum.hamcrest.Composites.*;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Runs tests for the {@link ContainsMembers} matcher.
 */
public class ContainsMembersTest
  {

  @Test
  public void matchesNull()
    {
    // Given...
    List<String> expected = null;
    List<String> actual = null;
    
    // When...
    assertThat( "Null lists", actual, containsMembers( expected));
    }

  @Test
  public void matchesNull_fails()
    {
    // Given...
    List<String> expected = null;
    List<String> actual = Arrays.asList( "Red", "Green", "Blue");
    
    // When...
    try
      {
      assertThat( "Null list", actual, containsMembers( expected));
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
            "Expected: null",
            "but: was not null")));
      }
    catch( Throwable t)
      {
      fail( "Unexpected failure: " + t);
      }
    }

  @Test
  public void matchesNonNull_fails()
    {
    // Given...
    List<String> expected = Arrays.asList( "Red", "Green", "Blue");
    List<String> actual = null;
    
    // When...
    try
      {
      assertThat( "Non-null list", actual, containsMembers( expected));
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
            "Expected: Iterable with 3 members",
            "but: was null")));
      }
    catch( Throwable t)
      {
      fail( "Unexpected failure: " + t);
      }
    }

  @Test
  public void matchesList()
    {
    // Given...
    List<String> expected = Arrays.asList( "Red", "Green", "Blue");
    List<String> actual = Arrays.asList( "Green", "Blue", "Red");
    
    // When...
    assertThat( "Lists", actual, containsMembers( expected));
    }

  @Test
  public void matchesList_missing()
    {
    // Given...
    List<String> expected = Arrays.asList( "Red", "Green", "Blue", "Magenta", "Cyan");
    List<String> actual = Arrays.asList( "Green", "Blue", "Red");
    
    // When...
    try
      {
      assertThat( "Missing members", actual, containsMembers( expected));
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
            "Expected: Iterable with 5 members",
            "but: was missing 2 members=[Magenta, Cyan]")));
      }
    catch( Throwable t)
      {
      fail( "Unexpected failure: " + t);
      }
    }

  @Test
  public void matchesList_unexpected()
    {
    // Given...
    List<String> expected = Arrays.asList( "Blue");
    List<String> actual = Arrays.asList( "Green", "Blue", "Red", "Blue");
    
    // When...
    try
      {
      assertThat( "Missing members", actual, containsMembers( expected));
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
            "Expected: Iterable with 1 members",
            "but: had 3 unexpected members=[Green, Red, Blue]")));
      }
    catch( Throwable t)
      {
      fail( "Unexpected failure: " + t);
      }
    }

  @Test
  public void matchesList_overlapping()
    {
    // Given...
    List<String> expected = Arrays.asList( "Red", "Green", "Blue", "Magenta", "Cyan");
    List<String> actual = Arrays.asList( "Cyan", "Blue", "Red", "Yellow", "Magenta");
    
    // When...
    try
      {
      assertThat( "Overlapping members", actual, containsMembers( expected));
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
            "Expected: Iterable with 5 members",
            "but: was missing 1 members=[Green]",
            "and: had 1 unexpected members=[Yellow]")));
      }
    catch( Throwable t)
      {
      fail( "Unexpected failure: " + t);
      }
    }

  @Test
  public void matchesNonIterable_fails()
    {
    // Given...
    List<String> expected = Arrays.asList( "Red", "Green", "Blue");
    String actual = "Red, Green, Blue";
    
    // When...
    try
      {
      assertThat( "Non-iterable matches", containsMembers( expected).matches( actual));
      fail( "Expected failure not reported");
      }

    // Then...
    catch( AssertionError failure)
      {
      }
    catch( Throwable t)
      {
      fail( "Unexpected failure: " + t);
      }
    }

  @Test
  public void matchesArray()
    {
    // Given...
    String[] actual = new String[]{ "Green", "Blue", "Red"};
    
    // When...
    assertThatArray( "Arrays", actual, containsMembers( "Red", "Green", "Blue"));
    }

  @Test
  public void matchesIterator()
    {
    // Given...
    Iterator<String> expected = Arrays.asList( "Red", "Green", "Blue").iterator();
    Iterator<String> actual = Arrays.asList( "Green", "Blue", "Red").iterator();
    
    // When...
    assertThatIterator( actual, containsMembers( expected));
    }
  }
