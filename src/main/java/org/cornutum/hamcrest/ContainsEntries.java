//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2022, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import static org.cornutum.hamcrest.CompositeUtils.*;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import static java.util.stream.Collectors.toSet;


/**
 * Matches a Map containing a specified collection of entries.
 * <P/>
 * Optionally, given a "entry Matcher supplier" function, applies additional detailed match conditions for
 * each Map entry.  First, for each entry of the expected Map, an entry-specific Matcher is derived by the
 * given supplier function.  Then each entry of a matched Map must satisfy the entry-specific
 * Matcher returned for its <CODE>equals</CODE>-matching counterpart in the given expected Map.
 */
public class ContainsEntries<K,V> extends BaseMatcher<Map<K,V>>
  {
  private final Set<Map.Entry<K,V>> expectedEntries;
  private final ContainsMembers<Map.Entry<K,V>> containsMembers;
  private String mapMismatch;
  private boolean entryMismatch;

  /**
   * Builds and supplies a {@link ContainsEntries} matcher for a specified source Map.
   */
  public static class Supplier<K,V> implements Function<Map<K,V>,Matcher<Map<K,V>>>
    {
    private Function<Map.Entry<K,V>,Matcher<Map.Entry<K,V>>> entryMatcherSupplier;
    
    /**
     * Creates a new Supplier that supplies a {@link ContainsEntries} matcher that
     * matches entries using the given value Matcher supplier.
     */
    public Supplier( Function<V,Matcher<V>> valueMatcherSupplier)
      {
      entryMatcherSupplier = new MapEntryMatcher.Supplier<K,V>( valueMatcherSupplier);
      }
    
    /**
     * Creates a new Supplier that supplies a {@link ContainsEntries} matcher that
     * matches entries using the given key and value Matcher suppliers.
     */
    public Supplier( Function<K,Matcher<K>> keyMatcherSupplier, Function<V,Matcher<V>> valueMatcherSupplier)
      {
      entryMatcherSupplier = new MapEntryMatcher.Supplier<K,V>( keyMatcherSupplier, valueMatcherSupplier);
      }

    /**
     * Returns the {@link ContainsEntries} matcher supplied for the given source Map.
     */
    public Matcher<Map<K,V>> apply( Map<K, V> source)
      {
      return (Matcher<Map<K,V>>) new ContainsEntries<K,V>( source, entryMatcherSupplier);
      }
    }
 
  /**
   * Creates a new ContainsEntries instance.
   */
  public ContainsEntries( Map<K,V> expected)
    {
    this( expected, null);
    }
 
  /**
   * Creates a new ContainsEntries instance that adds an additional match condition: each entry of a
   * matched Map must satisfy the Matcher returned by the given supplier for its <CODE>equals</CODE>-matching 
   * counterpart in the given expected Map.
   */
  public ContainsEntries( Map<K,V> expected, Function<Map.Entry<K,V>,Matcher<Map.Entry<K,V>>> entryMatcherSupplier)
    {
    expectedEntries =
      Optional.ofNullable( expected)
      .map( Map::entrySet)
      .map( entries -> entries.stream().collect( toSet()))
      .orElse( null);
    
    containsMembers = new ContainsMembers<Map.Entry<K,V>>( "entry set", expectedEntries, entryMatcherSupplier);
    }

  @SuppressWarnings("unchecked")
  public boolean matches( Object actual)
    {
    mapMismatch = null;
    entryMismatch = false;
    
    // Expected and actual objects have the same "nullity"?
    if( (expectedEntries == null) != (actual == null))
      {
      mapMismatch = 
        expectedEntries == null
        ? "was not null"
        : "was null";
      }
    else if( actual != null)
      {
      // Comparing to an actual Map?
      Map<K,V> actualMap =
        Map.class.isInstance( actual)
        ? (Map<K,V>) actual
        : null;

      if( actualMap == null)
        {
        mapMismatch = "was not a Map";
        }
      else
        {
        entryMismatch = !containsMembers.matches( actualMap.entrySet());
        }
      }

    return mapMismatch == null && !entryMismatch;
    }

  public void describeTo( Description description)
    {
    description.appendText(
      expectedEntries == null?
      "null" :

      entryMismatch?
      "Map " + descriptionOf( containsMembers) :
      
      "Map with " + expectedEntries.size() + " entries");
    }

  @SuppressWarnings("unchecked")
  public void describeMismatch( Object actual, Description description)
    {
    String mismatch =
      mapMismatch != null?
      mapMismatch :

      entryMismatch?
      mismatchFor( containsMembers, ((Map<K,V>) actual).entrySet()) :

      null;

    if( mismatch != null)
      {
      description.appendText( mismatch);
      }
    }

  public String toString()
    {
    return String.format( "%s[]", getClass().getSimpleName());
    }
  }
