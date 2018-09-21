//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2018, Cornutum Project
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
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import static java.util.stream.Collectors.toList;

/**
 * Matches an Iterable containing a specified sequence of members, in order.
 * <P/>
 * Optionally, given a "member Matcher supplier" function, applies additional detailed match conditions for
 * Iterable members.  First, for each member of the expected Iterable, a member-specific Matcher is derived by the
 * given supplier function.  Then each member of a matched Iterable must satisfy the member-specific
 * Matcher returned for its counterpart in the given expected Iterable.
 */
public class ListsMembers<T> extends BaseMatcher<Iterable<T>>
  {
  private final List<T> expectedMembers;
  private final Function<T,Matcher<T>> memberMatcherSupplier;
  private MemberMatcher memberMatcher;

  /**
   * Represents the match between a member of a matched Iterable and its counterpart in the expected Iterable.
   */
  private class MemberMatch
    {
    private final T actualMember;
    private final Matcher<T> matcher;
    
    /**
     * Creates a new MemberMatch instance.
     */
    public MemberMatch( T actualMember, Matcher<T> matcher)
      {
      this.actualMember = actualMember;
      this.matcher = matcher;
      }

    public T getActualMember()
      {
      return actualMember;
      }

    public Matcher<T> getMatcher()
      {
      return matcher;
      }    
    }

  /**
   * Builds and supplies a {@link ListsMembers} matcher for a specified source Iterable.
   */
  public static class Supplier<T,S extends Iterable<T>> implements Function<S,Matcher<S>>
    {
    private Function<T,Matcher<T>> memberMatcherSupplier;
    
    /**
     * Creates a new ListsMembersSupplier that supplies a {@link ListsMembers} matcher using
     * the given member Matcher supplier.
     */
    public Supplier( Function<T,Matcher<T>> memberMatcherSupplier)
      {
      this.memberMatcherSupplier = memberMatcherSupplier;
      }

    /**
     * Returns the {@link ListsMembers} matcher supplied for the given source Iterable.
     */
    @SuppressWarnings("unchecked")
    public Matcher<S> apply( S source)
      {
      return (Matcher<S>) new ListsMembers<T>( source, memberMatcherSupplier);
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

      // Comparing to an actual Iterable?
      Iterable<T> actualMembers =
        actual == null?
        null :
        
        Iterable.class.isInstance( actual)?
        (Iterable<T>) actual :

        Iterator.class.isInstance( actual)?
        () -> (Iterator<T>) actual :

        actual.getClass().isArray() ?
        Arrays.asList( (T[]) actual) :
          
        null;

      if( actual != null && actualMembers == null)
        {
        iterableMismatch = "was not an Iterable";
        }
      else
        {
        // Must collect the actual sequence in advance to prepare for multiple matching traversals.
        List<T> actualSequence =
          actualMembers == null
          ? null
          : streamFor( actualMembers).collect( toList());

        // Are actual members a 1-to-1 "equals" match for expected members (regardless of order)?
        ContainsMembers<T> containsMembers = new ContainsMembers<T>( expectedMembers);
        if( !containsMembers.matches( actualSequence))
          {
          iterableMismatch = mismatchFor( containsMembers, actualSequence);
          }
        else if( actualSequence != null)
          {
          // Are actual members in the expected sequence?
          int mismatchPosition = 
            IntStream.range( 0, expectedMembers.size())
            .filter( i -> !Objects.equals( actualSequence.get(i), expectedMembers.get(i)))
            .findFirst()
            .orElse( -1);

          if( mismatchPosition >= 0)
            {
            iterableMismatch =
              "at index="
              + mismatchPosition
              + ", found "
              + actualSequence.get( mismatchPosition)
              + " instead of "
              + expectedMembers.get( mismatchPosition);
            }
          else if( memberMatcherSupplier != null)
            {
            // Does each actual member satisfy the specified member Matcher for its expected member counterpart?
            memberMismatch = 
              IntStream.range( 0, expectedMembers.size())
              .mapToObj( i -> new MemberMatch( actualSequence.get(i), memberMatcherSupplier.apply( expectedMembers.get(i))))
              .filter( m -> !m.getMatcher().matches( m.getActualMember()))
              .findFirst()
              .orElse( null);
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
    }
 
  /**
   * Creates a new ListsMembers instance.
   */
  public ListsMembers( Iterable<? extends T> expected)
    {
    this( expected, null);
    }
 
  /**
   * Creates a new ListsMembers instance that adds an additional match condition: each member of a
   * matched Iterable must satisfy the Matcher returned by the given supplier for its 
   * counterpart in the given expected Iterable.
   */
  public ListsMembers( Iterable<? extends T> expected, Function<T,Matcher<T>> memberMatcherSupplier)
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
      .map( m -> "Sequence containing " + descriptionOf( m.getMatcher()))
      
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
  }
