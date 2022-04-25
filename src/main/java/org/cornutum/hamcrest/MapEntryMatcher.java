//////////////////////////////////////////////////////////////////////////////
// 
//                    Copyright 2022, Cornutum Project
//                             www.cornutum.org
//
//////////////////////////////////////////////////////////////////////////////

package org.cornutum.hamcrest;

import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.util.Map;
import java.util.function.Function;

/**
 * A composite matcher for Map entries.
 */
public class MapEntryMatcher<K,V> extends BaseCompositeMatcher<Map.Entry<K,V>>
  {
  /**
   * Creates a new MapEntryMatcher instance.
   */
  public MapEntryMatcher( Map.Entry<K,V> expected, Function<K,Matcher<K>> keyMatcherSupplier, Function<V,Matcher<V>> valueMatcherSupplier)
    {
    super( expected);
    expectThat( valueOf( "key", Map.Entry::getKey).matches( keyMatcherSupplier));
    expectThat( valueOf( "value", Map.Entry::getValue).matches( valueMatcherSupplier));
    }

  /**
   * Builds and supplies a {@link MapEntryMatcher} matcher for a specified source entry.
   */
  public static class Supplier<K,V> implements Function<Map.Entry<K,V>,Matcher<Map.Entry<K,V>>>
    {
    private final Function<K,Matcher<K>> keyMatcherSupplier;
    private final Function<V,Matcher<V>> valueMatcherSupplier;
    
    /**
     * Creates a new Supplier that supplies a {@link MapEntryMatcher}  using
     * the given value Matcher supplier.
     */
    public Supplier( Function<V,Matcher<V>> valueMatcherSupplier)
      {
      this( Matchers::equalTo, valueMatcherSupplier);
      }
    
    /**
     * Creates a new Supplier that supplies a {@link MapEntryMatcher}  using
     * the given key and value Matcher suppliers.
     */
    public Supplier( Function<K,Matcher<K>> keyMatcherSupplier, Function<V,Matcher<V>> valueMatcherSupplier)
      {
      this.keyMatcherSupplier = keyMatcherSupplier;
      this.valueMatcherSupplier = valueMatcherSupplier;
      }

    /**
     * Returns the {@link MapEntryMatcher} supplied for the given source Map.
     */
    public Matcher<Map.Entry<K,V>> apply( Map.Entry<K,V> source)
      {
      return (Matcher<Map.Entry<K,V>>) new MapEntryMatcher<K,V>( source, keyMatcherSupplier, valueMatcherSupplier);
      }
    }
  }
