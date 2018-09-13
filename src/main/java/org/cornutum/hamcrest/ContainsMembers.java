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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import static java.util.stream.Collectors.toList;


/**
 * Matches an Iterable containing a specified collection of members in any order.
 * <P/>
 * Optionally, given a "member Matcher supplier" function, applies additional detailed match conditions for
 * Iterable members.  First, for each member of the expected Iterable, a member-specific Matcher is derived by the
 * given supplier function.  Then each member of a matched Iterable must satisfy the member-specific
 * Matcher returned for its <CODE>equals</CODE>-matching counterpart in the given expected Iterable.
 */
public class ContainsMembers<T> extends BaseMatcher<Iterable<T>>
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
          // Are actual members a 1-to-1 "equals" match for expected members?
          List<T> unmatched = new ArrayList<>( expectedMembers);
          List<T> unexpected = new ArrayList<>();

          for( T member : actualMembers)
            {
            int i = unmatched.indexOf( member);
            if( i >= 0)
              {
              unmatched.remove( i);
              }
            else
              {
              unexpected.add( member);
              }
            }

          // Any expected members missing?
          if( !unmatched.isEmpty())
            {
            iterableMismatch =
              "was missing "
              + unmatched.size()
              + " members=["
              + toString( unmatched)
              + "]";
            }

          // Any actual members unexpected?
          if( !unexpected.isEmpty())
            {
            iterableMismatch =
              (iterableMismatch == null? "" : (iterableMismatch + "\n     and: "))
              + "had "
              + unexpected.size()
              + " unexpected members=["
              + toString( unexpected)
              + "]";
            }

          if( iterableMismatch == null && memberMatcherSupplier != null)
            {
            // Does each actual member satisfy the specified member Matcher for its expected member counterpart?
            List<T> expected = new ArrayList<>( expectedMembers);
            memberMismatch = 
              streamFor( actualMembers)
              .map( actualMember -> new MemberMatch( actualMember, memberMatcherSupplier.apply( expected.remove( expected.indexOf( actualMember)))))
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
    }
 
  /**
   * Creates a new ContainsMembers instance.
   */
  public ContainsMembers( Iterable<? extends T> expected)
    {
    this( expected, null);
    }
 
  /**
   * Creates a new ContainsMembers instance that adds an additional match condition: each member of a
   * matched Iterable must satisfy the Matcher returned by the given supplier for its <CODE>equals</CODE>-matching 
   * counterpart in the given expected Iterable.
   */
  public ContainsMembers( Iterable<? extends T> expected, Function<T,Matcher<T>> memberMatcherSupplier)
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
      .map( m -> "Iterable containing " + descriptionOf( m.getMatcher()))
      
      .orElse( "Iterable with " + expectedMembers.size() + " members"));
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
