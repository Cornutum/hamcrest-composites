//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2018, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import static org.cornutum.hamcrest.CompositeUtils.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

import org.hamcrest.Matcher;

/**
 * Defines methods to create composite matchers.
 */
public final class Composites
  {
  /**
   * Creates a new Composites instance.
   */
  private Composites()
    {
    // Static methods only
    }

  /**
   * Returns a Matcher for an Iterable containing the given collection of members in any order.
   */
  public static <T> Matcher<Iterable<T>> containsMembers( Iterable<? extends T> expected)
    {
    return new ContainsMembers<T>( expected);
    }

  /**
   * Returns a Matcher for an Iterable containing the given collection of members in any order, with
   * an additional match condition: each member of a matched Iterable must satisfy the Matcher returned 
   * by the given supplier for its <CODE>equals</CODE>-matching counterpart in the given expected Iterable.
   */
  public static <T> Matcher<Iterable<T>> containsMembers( Function<T,Matcher<T>> memberMatcherSupplier, Iterable<? extends T> expected)
    {
    return new ContainsMembers<T>( expected, memberMatcherSupplier);
    }

  /**
   * Returns a Matcher for an Iterable containing the given collection of members in any order.
   */
  @SafeVarargs
  public static <T> Matcher<Iterable<T>> containsMembers( T... expected)
    {
    return containsMembers( Arrays.asList( expected));
    }

  /**
   * Returns a Matcher for an Iterable containing the given collection of members in any order, with
   * an additional match condition: each member of a matched Iterable must satisfy the Matcher returned 
   * by the given supplier for its <CODE>equals</CODE>-matching counterpart in the given expected Iterable.
   */
  @SafeVarargs
  public static <T> Matcher<Iterable<T>> containsMembers( Function<T,Matcher<T>> memberMatcherSupplier, T... expected)
    {
    return containsMembers( memberMatcherSupplier, Arrays.asList( expected));
    }

  /**
   * Returns a Matcher for an Iterable containing the given collection of members in any order.
   */
  public static <T> Matcher<Iterable<T>> containsMembers( Iterator<T> expected)
    {
    Iterable<T> iterable = () -> expected;
    return containsMembers( iterable);
    }

  /**
   * Returns a Matcher for an Iterable containing the given collection of members in any order, with
   * an additional match condition: each member of a matched Iterable must satisfy the Matcher returned 
   * by the given supplier for its <CODE>equals</CODE>-matching counterpart in the given expected Iterable.
   */
  public static <T> Matcher<Iterable<T>> containsMembers( Function<T,Matcher<T>> memberMatcherSupplier, Iterator<T> expected)
    {
    Iterable<T> iterable = () -> expected;
    return containsMembers( memberMatcherSupplier, iterable);
    }

  /**
   * Returns a Matcher that compares values of the given function, using a result Matcher returned by the given supplier.
   */
  public static <T,R> Matcher<T> matchesFunction( String functionName, Function<T,R> function, T source, Function<R,Matcher<R>> resultMatcherSupplier)
    {
    return new MatchesFunction<T,R>( functionName, function, source, resultMatcherSupplier);
    }

  /**
   * To create a more expressive reference, especially for a constructor expression, simply returns the given Matcher value.
   */
  public static <T> Matcher<T> matches( Matcher<T> matcherExpression)
    {
    return matcherExpression;
    }

  /**
   * Returns a new {@link MatchesFunction.Builder} for the given expected object.
   */
  public static <T,R> MatchesFunction.Builder<T,R> comparedTo( T expected)
    {
    return new MatchesFunction.Builder<>( expected);
    }

  /**
   * Throws an AssertionError if the given array does not satisfy the given Iterable matcher.
   */
  public static <T> void assertThatArray( String reason, T[] actual, Matcher<Iterable<T>> matcher)
    {
    assertMatches( reason, actual, matcher);
    }

  /**
   * Throws an AssertionError if the given array does not satisfy the given Iterable matcher.
   */
  public static <T> void assertThatArray( T[] actual, Matcher<Iterable<T>> matcher)
    {
    assertThatArray( "", actual, matcher);
    }

  /**
   * Throws an AssertionError if the given iterator does not satisfy the the given Iterable matcher.
   */
  public static <T> void assertThatIterator( String reason, Iterator<T> actual, Matcher<Iterable<T>> matcher)
    {
    assertMatches( reason, actual, matcher);
    }

  /**
   * Throws an AssertionError if the given iterator does not satisfy the given Iterable matcher.
   */
  public static <T> void assertThatIterator( Iterator<T> actual, Matcher<Iterable<T>> matcher)
    {
    assertThatIterator( "", actual, matcher);
    }

  /**
   * Throws an AssertionError if the given object does not satisfy the given matcher.
   */
  private static <T> void assertMatches( String reason, Object actual, Matcher<?> matcher)
    {
    if( !matcher.matches( actual))
      {
      StringBuilder mismatch =
        new StringBuilder()
        .append( reason)
        .append( "\nExpected: ")
        .append( descriptionOf( matcher))
        .append( "\n     but: ")
        .append( mismatchFor( matcher, actual));

      throw new AssertionError( mismatch.toString());
      }
    }
}
