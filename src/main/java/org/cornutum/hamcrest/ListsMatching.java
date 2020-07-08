//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2020, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import static org.cornutum.hamcrest.CompositeUtils.*;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import static java.util.stream.Collectors.toList;

/**
 * Matches an Iterable containing a specified sequence of members, in order.  The specified "member
 * Matcher supplier" function defines the match conditions for Iterable members.
 * <P/>
 * First, for each member of the expected Iterable, a member-specific Matcher is derived by the
 * given supplier function.  Then each member of a matched Iterable must satisfy the member-specific
 * Matcher returned for its counterpart in the given expected Iterable.
 * <P/>
 * Unlike the {@link ListMembers} matcher, this matcher does not attempt to compare members using <CODE>equals()</CODE>.
 * The {@link ListMembers} matcher is preferred when <CODE>equals</CODE> methods are defined based on a few "primary key" fields.
 * In other cases, this matcher may provide better descriptions of match failures. But, unlike {@link ListMembers},
 * this matcher cannot always verify that an expected member is missing or that an actual member does not belong.
 */
public class ListsMatching<T> extends BaseMatcher<Iterable<T>>
  {
  private final List<T> expectedMembers;
  private final Function<T,Matcher<T>> memberMatcherSupplier;
  private MemberMatcher memberMatcher;

  /**
   * Represents the match between a member of a matched Iterable and its counterpart in the expected Iterable.
   */
  private class MemberMatch
    {
    private final int index;
    private final T actualMember;
    private final Matcher<T> matcher;
    
    /**
     * Creates a new MemberMatch instance.
     */
    public MemberMatch( int index, T actualMember, Matcher<T> matcher)
      {
      this.index = index;
      this.actualMember = actualMember;
      this.matcher = matcher;
      }

    public T getActualMember()
      {
      return actualMember;
      }

    public int getIndex()
      {
      return index;
      }

    public Matcher<T> getMatcher()
      {
      return matcher;
      }

    public String toString()
      {
      return String.format( "%s[actual=%s, index=%s, matcher=%s]", getClass().getSimpleName(), actualMember, index, matcher);
      }
    }

  /**
   * Builds and supplies a {@link ListsMatching} matcher for a specified source Iterable.
   */
  public static class Supplier<T,S extends Iterable<T>> implements Function<S,Matcher<S>>
    {
    private Function<T,Matcher<T>> memberMatcherSupplier;
    
    /**
     * Creates a new ListsMatchingSupplier that supplies a {@link ListsMatching} matcher using
     * the given member Matcher supplier.
     */
    public Supplier( Function<T,Matcher<T>> memberMatcherSupplier)
      {
      this.memberMatcherSupplier = memberMatcherSupplier;
      }

    /**
     * Returns the {@link ListsMatching} matcher supplied for the given source Iterable.
     */
    @SuppressWarnings("unchecked")
    public Matcher<S> apply( S source)
      {
      return (Matcher<S>) new ListsMatching<T>( source, memberMatcherSupplier);
      }
    }

  /**
   * Matches an actual Iterable with the list of expected members.
   */
  private class MemberMatcher
    {
    private final Object matched;
    private String iterableMismatch;
    private MemberMatch memberMismatch;
    
    /**
     * Creates a new MemberMatcher instance.
     */
    @SuppressWarnings("unchecked")
    public MemberMatcher( Object actual)
      {
      matched = actual;
      iterableMismatch = null;
      memberMismatch = null;

      // Expected and actual objects have the same "nullity"?
      if( (expectedMembers == null) != (actual == null))
        {
        iterableMismatch = 
          expectedMembers == null
          ? "was not null"
          : "was null";
        }
      else if( actual != null)
        {
        // Comparing to an actual Iterable?
        Iterable<T> actualMembers =
          Iterable.class.isInstance( actual)?
          (Iterable<T>) actual :

          Iterator.class.isInstance( actual)?
          () -> (Iterator<T>) actual :

          actual.getClass().isArray() ?
          Arrays.asList( (T[]) actual) :
          
          null;

        if( actualMembers == null)
          {
          iterableMismatch = "was not an Iterable";
          }
        else
          {
          // Must collect the actual sequence in advance to prepare for multiple matching traversals.
          List<T> actualSequence = streamFor( actualMembers).collect( toList());

          // Does each actual member satisfy the specified member Matcher for its expected member counterpart?
          int matchedSize = Math.min( expectedMembers.size(), actualSequence.size());
          memberMismatch = 
            IntStream.range( 0, matchedSize)
            .mapToObj( i -> new MemberMatch( i, actualSequence.get(i), memberMatcherSupplier.apply( expectedMembers.get(i))))
            .filter( m -> !m.getMatcher().matches( m.getActualMember()))
            .findFirst()
            .orElse( null);

          if( memberMismatch == null)
            {
            // Any expected members missing?
            if( expectedMembers.size() > matchedSize)
              {
              List<T> missing = expectedMembers.subList( matchedSize, expectedMembers.size());
              iterableMismatch =
                String.format(
                  "was missing %s members=[%s] starting at position=%s",
                  missing.size(),
                  toString( missing),
                  matchedSize);
              }

            // Any actual members unexpected?
            else if( actualSequence.size() > matchedSize)
              {
              List<T> unexpected = actualSequence.subList( matchedSize, actualSequence.size());
              iterableMismatch =
                String.format(
                  "had %s unexpected members=[%s] starting at position=%s",
                  unexpected.size(),
                  toString( unexpected),
                  matchedSize);
              }
            }
          }
        }
      }

    /**
     * Returns the matching result.
     */
    public boolean matches()
      {
      return !getIterableMismatch().isPresent() && !getMemberMismatch().isPresent();
      }

    /**
     * Returns the object matched by this MemberMatcher.
     */
    public Object getMatched()
      {
      return matched;
      }

    /**
     * Returns a description of any mismatch between the expected Iterable and the {@link #getMatched matched} object.
     */
    public Optional<String> getIterableMismatch()
      {
      return Optional.ofNullable( iterableMismatch);
      }

    /**
     * Returns any mismatch between a member of a matched Iterable and its counterpart in the expected Iterable.
     */
    public Optional<MemberMatch> getMemberMismatch()
      {
      return Optional.ofNullable( memberMismatch);
      }

    /**
     * Returns a string representing the given list.
     */
    private String toString( List<?> members)
      {
      return
        members.stream()
        .map( m -> String.valueOf(m))
        .reduce( "", (list, m) -> list + (list.isEmpty()? "" : ", ") + m);
      }

    public String toString()
      {
      return String.format( "%s[%s]", getClass().getSimpleName(), ListsMatching.this.getClass().getSimpleName());
      }
    }
 
  /**
   * Creates a new ListsMatching instance. Each member of a matched Iterable must satisfy the
   * Matcher returned by the given supplier for its counterpart in the given expected Iterable.
   */
  public ListsMatching( Iterable<? extends T> expected, Function<T,Matcher<T>> memberMatcherSupplier)
    {
    this.memberMatcherSupplier = memberMatcherSupplier;
    
    expectedMembers =
      expected == null
      ? null
      : streamFor( expected).collect( toList());
    }

  public boolean matches( Object actual)
    {
    return getMemberMatcher( actual).matches();
    }

  public void describeTo( Description description)
    {
    description.appendText(
      expectedMembers == null?
      "null" :

      getMemberMismatch()
      .map( m -> String.format( "At position=%s, %s", m.getIndex(), descriptionOf( m.getMatcher())))
      .orElse( "Sequence of " + expectedMembers.size() + " members"));
    }

  public void describeMismatch( Object actual, Description description)
    {
    MemberMatcher memberMatcher = getMemberMatcher( actual);

    String mismatch =
      memberMatcher.getIterableMismatch()
      .orElse(
        memberMatcher.getMemberMismatch()
        .map( m -> mismatchFor( m.getMatcher(), m.getActualMember()))
        .orElse( null));

    if( mismatch != null)
      {
      description.appendText( mismatch);
      }
    }

  /**
   * Returns the MemberMatcher for the given actual object
   */
  private MemberMatcher getMemberMatcher( Object actual)
    {
    if( memberMatcher == null || memberMatcher.getMatched() != actual)
      {
      memberMatcher = new MemberMatcher( actual);
      }

    return memberMatcher;
    }

  /**
   * Returns any mismatch between a member of a matched Iterable and its counterpart in the expected Iterable.
   */
  private Optional<MemberMatch> getMemberMismatch()
    {
    return
      memberMatcher == null
      ? Optional.empty()
      : memberMatcher.getMemberMismatch();
    }

  public String toString()
    {
    return String.format( "%s[]", getClass().getSimpleName());
    }
  }
