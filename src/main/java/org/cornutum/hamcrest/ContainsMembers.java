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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import static java.util.stream.Collectors.toList;


/**
 * Matches an Iterable containing a specified collection of members in any order.
 */
public class ContainsMembers<T> extends BaseMatcher<Iterable<T>>
  {
  private final List<T> expectedMembers;
  private MemberMatcher memberMatcher;

  /**
   * Matches an actual Iterable with the list of expected members.
   */
  private class MemberMatcher
    {
    private final Object matched;
    private final StringBuilder mismatch;
    
    /**
     * Creates a new MemberMatcher instance.
     */
    @SuppressWarnings("unchecked")
    public MemberMatcher( Object actual)
      {
      matched = actual;
      mismatch = new StringBuilder();

      if( (expectedMembers == null) != (actual == null))
        {
        mismatch.append(
          expectedMembers == null
          ? "was not null"
          : "was null");
        }
      else if( actual != null)
        {
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
          mismatch.append( "was not an Iterable");
          }
        else
          {
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

          if( !unmatched.isEmpty())
            {
            mismatch
              .append( "was missing ")
              .append( unmatched.size())
              .append( " members=[")
              .append( toString( unmatched))
              .append( "]");
            }

          if( !unexpected.isEmpty())
            {
            mismatch
              .append( mismatch.length() == 0? "" : "\n     and: ")
              .append( "had ")
              .append( unexpected.size())
              .append( " unexpected members=[")
              .append( toString( unexpected))
              .append( "]");
            }
          }
        }
      }

    /**
     * Returns the object matched by this MemberMatcher.
     */
    public Object getMatched()
      {
      return matched;
      }

    /**
     * Returns a description of any mismatch between the expected list and the {@link #getMatched matched} object.
     */
    public Optional<String> getMismatch()
      {
      return
        mismatch.length() > 0
        ? Optional.of( mismatch.toString())
        : Optional.empty();
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
    expectedMembers =
      expected == null
      ? null
      : streamFor( expected).collect( toList());
    }

  public boolean matches( Object actual)
    {
    return !getMemberMatcher( actual).getMismatch().isPresent();
    }

  public void describeTo( Description description)
    {
    description.appendText(
      expectedMembers == null
      ? "null"
      : "Iterable with " + expectedMembers.size() + " members");
    }

  public void describeMismatch( Object actual, Description description)
    {
    getMemberMatcher( actual).getMismatch().ifPresent( mismatch -> description.appendText( mismatch));
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
  }
