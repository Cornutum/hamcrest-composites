//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2018, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.StringDescription;

/**
 * Defines support methods for composite matchers.
 */
public final class CompositeUtils
  {
  /**
   * Creates a new CompositeUtils instance.
   */
  private CompositeUtils()
    {
    // Static methods only
    }

  /**
   * Returns a Stream that traverses the members of the given Iterable.
   */
  public static <T> Stream<T> streamFor( Iterable<T> iterable)
    {
    return StreamSupport.stream( iterable.spliterator(), false);
    }

  /**
   * Returns an array that contains the members of the given Iterable.
   */
  @SuppressWarnings("unchecked")
  public static <T> T[] toArray( Iterable<T> iterable)
    {
    return (T[]) streamFor( iterable).toArray();
    }

  /**
   * Returns a description of the mismatch reported by the given Matcher for the given object.
   */
  public static String mismatchFor( Matcher<?> matcher, Object object)
    {
    Description description = new StringDescription();
    matcher.describeMismatch( object, description);
    return description.toString();
    }

  /**
   * Returns a description of the given Matcher.
   */
  public static String descriptionOf( Matcher<?> matcher)
    {
    Description description = new StringDescription();
    matcher.describeTo( description);
    return description.toString();
    }
  }
